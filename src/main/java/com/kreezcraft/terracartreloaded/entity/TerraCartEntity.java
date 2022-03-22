package com.kreezcraft.terracartreloaded.entity;

import javax.annotation.Nonnull;

import com.kreezcraft.terracartreloaded.CartRegistry;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.PoweredRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.network.PlayMessages;

public class TerraCartEntity extends AbstractMinecart {
	public TerraCartEntity(EntityType<?> type, Level level) {
		super(type, level);
	}

	public TerraCartEntity(Level level, double x, double y, double z) {
		super(CartRegistry.TERRA_CART.get(), level, x, y, z);
	}

	public TerraCartEntity(PlayMessages.SpawnEntity spawnEntity, Level level) {
		this(CartRegistry.TERRA_CART.get(), level);
	}

	@Override
	public void activateMinecart(int x, int y, int z, boolean receivingPower) {
		if (receivingPower) {
			this.ejectPassengers();
			
			if (this.getDamage() == 0) {
				this.setHurtDir(-this.getHurtDir());
				this.setHurtTime(10);
				this.setDamage(50.0F);
				this.markHurt();
			}
		}
	}
	
	@Override
	protected void moveAlongTrack(BlockPos pos, BlockState state) {
		this.resetFallDistance();
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		Vec3 vector3d = this.getPos(d0, d1, d2);
		d1 = pos.getY();
		boolean boosted = false;
		boolean breaking = false;
		BaseRailBlock baseRailBlock = (BaseRailBlock) state.getBlock();
		if ( baseRailBlock instanceof PoweredRailBlock
		  && !((PoweredRailBlock) baseRailBlock).isActivatorRail() ) {
			boosted = state.getValue(PoweredRailBlock.POWERED);
			breaking = !boosted;
		} else {
			if (this.getFirstPassenger() != null) {
				boosted = true;
			}
		}

		Vec3 vec31 = this.getDeltaMovement();
		RailShape railshape = ((BaseRailBlock) state.getBlock()).getRailDirection(state, this.level, pos, this);
		switch(railshape) {
			case ASCENDING_EAST:
				this.setDeltaMovement(vec31.add(-1 * getSlopeAdjustment(), 0.0D, 0.0D));
				++d1;
				break;
			case ASCENDING_WEST:
				this.setDeltaMovement(vec31.add(getSlopeAdjustment(), 0.0D, 0.0D));
				++d1;
				break;
			case ASCENDING_NORTH:
				this.setDeltaMovement(vec31.add(0.0D, 0.0D, getSlopeAdjustment()));
				++d1;
				break;
			case ASCENDING_SOUTH:
				this.setDeltaMovement(vec31.add(0.0D, 0.0D, -1 * getSlopeAdjustment()));
				++d1;
		}

		vec31 = this.getDeltaMovement();
		Pair<Vec3i, Vec3i> pair = exits(railshape);
		Vec3i vec3i = pair.getFirst();
		Vec3i vector3i1 = pair.getSecond();
		double d4 = vector3i1.getX() - vec3i.getX();
		double d5 = vector3i1.getZ() - vec3i.getZ();
		double d6 = Math.sqrt(d4 * d4 + d5 * d5);
		double d7 = vec31.x * d4 + vec31.z * d5;
		if (d7 < 0.0D) {
			d4 = -d4;
			d5 = -d5;
		}

		double d8 = Math.min(2.0D, vec31.horizontalDistance());
		vec31 = new Vec3(d8 * d4 / d6, vec31.y, d8 * d5 / d6);
		this.setDeltaMovement(vec31);
		Entity entity = this.getFirstPassenger();
		if (entity instanceof Player) {
			Vec3 vector3d2 = entity.getDeltaMovement();
			double d9 = vector3d2.horizontalDistanceSqr();
			double d11 = this.getDeltaMovement().horizontalDistanceSqr();
			if (d9 > 1.0E-4D && d11 < 0.01D) {
				this.setDeltaMovement(this.getDeltaMovement().add(vector3d2.x * 0.1D, 0.0D, vector3d2.z * 0.1D));
				breaking = false;
			}
		}

		if (breaking && shouldDoRailFunctions()) {
			double d22 = this.getDeltaMovement().horizontalDistance();
			if (d22 < 0.03D) {
				this.setDeltaMovement(Vec3.ZERO);
			} else {
				this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, 0.0D, 0.5D));
			}
		}

		double d23 = (double)pos.getX() + 0.5D + (double)vec3i.getX() * 0.5D;
		double d10 = (double)pos.getZ() + 0.5D + (double)vec3i.getZ() * 0.5D;
		double d12 = (double)pos.getX() + 0.5D + (double)vector3i1.getX() * 0.5D;
		double d13 = (double)pos.getZ() + 0.5D + (double)vector3i1.getZ() * 0.5D;
		d4 = d12 - d23;
		d5 = d13 - d10;
		double d14;
		if (d4 == 0.0D) {
			d14 = d2 - (double)pos.getZ();
		} else if (d5 == 0.0D) {
			d14 = d0 - (double)pos.getX();
		} else {
			double d15 = d0 - d23;
			double d16 = d2 - d10;
			d14 = (d15 * d4 + d16 * d5) * 2.0D;
		}

		d0 = d23 + d4 * d14;
		d2 = d10 + d5 * d14;
		this.setPos(d0, d1, d2);
		this.moveMinecartOnRail(pos);
		if (vec3i.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vec3i.getX() && Mth.floor(this.getZ()) - pos.getZ() == vec3i.getZ()) {
			this.setPos(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
		} else if (vector3i1.getY() != 0 && Mth.floor(this.getX()) - pos.getX() == vector3i1.getX() && Mth.floor(this.getZ()) - pos.getZ() == vector3i1.getZ()) {
			this.setPos(this.getX(), this.getY() + (double)vector3i1.getY(), this.getZ());
		}

		this.applyNaturalSlowdown();
		Vec3 vector3d3 = this.getPos(this.getX(), this.getY(), this.getZ());
		if (vector3d3 != null && vector3d != null) {
			double d17 = (vector3d.y - vector3d3.y) * 0.05D;
			Vec3 vector3d4 = this.getDeltaMovement();
			double d18 = vector3d4.horizontalDistance();
			if (d18 > 0.0D) {
				this.setDeltaMovement(vector3d4.multiply((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
			}

			this.setPos(this.getX(), vector3d3.y, this.getZ());
		}

		int j = Mth.floor(this.getX());
		int i = Mth.floor(this.getZ());
		if (j != pos.getX() || i != pos.getZ()) {
			Vec3 vector3d5 = this.getDeltaMovement();
			double d26 = vector3d5.horizontalDistance();
			this.setDeltaMovement(d26 * (double)(j - pos.getX()), vector3d5.y, d26 * (double)(i - pos.getZ()));
		}

		if (shouldDoRailFunctions())
			((BaseRailBlock) state.getBlock()).onMinecartPass(state, level, pos, this);

		if (boosted && shouldDoRailFunctions()) {
			Vec3 vector3d6 = this.getDeltaMovement();
			double d27 = vector3d6.horizontalDistance();
			if (d27 > 0.01D) {
				double d19 = 0.06D;
				this.setDeltaMovement(vector3d6.add(vector3d6.x / d27 * d19, 0.0D, vector3d6.z / d27 * d19));
			} else {
				Vec3 vec37 = this.getDeltaMovement();
				double d20 = vec37.x;
				double d21 = vec37.z;
				if (railshape == RailShape.EAST_WEST) {
					if (this.isRedstoneConductor(pos.west())) {
						d20 = 0.02D;
					} else if (this.isRedstoneConductor(pos.east())) {
						d20 = -0.02D;
					}
				} else {
					if (railshape != RailShape.NORTH_SOUTH) {
						return;
					}

					if (this.isRedstoneConductor(pos.north())) {
						d21 = 0.02D;
					} else if (this.isRedstoneConductor(pos.south())) {
						d21 = -0.02D;
					}
				}

				this.setDeltaMovement(d20, vec37.y, d21);
			}
		}
	}
	
	@Override
	public ItemStack getPickResult() {
		return ItemStack.EMPTY;
	}
	
	@Override
	public boolean isPoweredCart() {
		return false;
	}

	@Override
	@Nonnull
	public Type getMinecartType() {
		return Type.RIDEABLE;
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		if (!this.level.isClientSide && !this.isRemoved()) {
			if (this.isInvulnerableTo(source)) {
				return false;
			} else {
				this.setHurtDir(-this.getHurtDir());
				this.setHurtTime(10);
//				this.markHurt();
				this.setDamage(this.getDamage() + amount * 10.0F);
				boolean flag = source.getEntity() instanceof Player && ((Player) source.getEntity()).isCreative();
				if (flag || this.getDamage() > 40.0F) {
					this.ejectPassengers();
					if (flag && !this.hasCustomName()) {
						this.remove(RemovalReason.DISCARDED);
					} else {
						this.destroy(source);
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}
	
	@Override
	public void destroy(@NotNull DamageSource source) {
		this.kill();
	}
	
	@Override
	public boolean isInWater() {
		return false;
	}
	
	@Override
	public void tick() {
		if (this.getFirstPassenger() == null) {
			this.destroy(DamageSource.GENERIC);
		}
		if (this.onGround) {
			if (this.getDeltaMovement().lengthSqr() < 0.000001D) {
				this.destroy(DamageSource.GENERIC);
			}
		}
		super.tick();
	}
}

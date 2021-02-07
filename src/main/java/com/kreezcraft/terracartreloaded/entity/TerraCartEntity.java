package com.kreezcraft.terracartreloaded.entity;

import com.kreezcraft.terracartreloaded.CartRegistry;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class TerraCartEntity extends AbstractMinecartEntity {
	public TerraCartEntity(EntityType<?> type, World worldIn) {
		super(type, worldIn);
	}

	public TerraCartEntity(World worldIn, double x, double y, double z) {
		super(CartRegistry.TERRA_CART.get(), worldIn, x, y, z);
	}

	public TerraCartEntity(FMLPlayMessages.SpawnEntity spawnEntity, World worldIn) {
		this(CartRegistry.TERRA_CART.get(), worldIn);
	}

	@Override
	public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
		if (receivingPower) {
			if (this.isBeingRidden()) {
				this.removePassengers();
			}

			if (this.getRollingAmplitude() == 0) {
				this.setRollingDirection(-this.getRollingDirection());
				this.setRollingAmplitude(10);
				this.setDamage(50.0F);
				this.markVelocityChanged();
			}
		}
	}

	@Override
	protected void moveDerailedMinecart() {
		double d0 = onGround ? this.getMaximumSpeed() : getMaxSpeedAirLateral();
		Vector3d vector3d = this.getMotion();
		this.setMotion(MathHelper.clamp(vector3d.x, -d0, d0), vector3d.y, MathHelper.clamp(vector3d.z, -d0, d0));

		if (getMaxSpeedAirVertical() > 0 && getMotion().y > getMaxSpeedAirVertical()) {
			if(Math.abs(getMotion().x) < 0.3f && Math.abs(getMotion().z) < 0.3f)
				setMotion(new Vector3d(getMotion().x, 0.15f, getMotion().z));
			else
				setMotion(new Vector3d(getMotion().x, getMaxSpeedAirVertical(), getMotion().z));
		}

		if (this.onGround) {
			this.setMotion(this.getMotion().scale(0.5D));
			Vector3d motion = this.getMotion();
			if (Math.abs(motion.x) < 0.02D && Math.abs(motion.y) < 0.02D && Math.abs(motion.z) < 0.02D) {
				//this.kill();
				this.killMinecart(null);
			}
		}

		this.move(MoverType.SELF, this.getMotion());

		if (!this.onGround) {
			this.setMotion(this.getMotion().scale(getDragAir()));
		}
	}

	@Override
	protected void moveAlongTrack(BlockPos pos, BlockState state) {
		this.fallDistance = 0.0F;
		double d0 = this.getPosX();
		double d1 = this.getPosY();
		double d2 = this.getPosZ();
		Vector3d vector3d = this.getPos(d0, d1, d2);
		d1 = (double)pos.getY();
		boolean boosted = false;
		boolean breaking = false;
		AbstractRailBlock abstractrailblock = (AbstractRailBlock)state.getBlock();
		if (abstractrailblock instanceof PoweredRailBlock && !((PoweredRailBlock) abstractrailblock).isActivatorRail()) {
			boosted = state.get(PoweredRailBlock.POWERED);
			breaking = !boosted;
		} else {
			if (this.isBeingRidden()) {
				boosted = true;
			}
		}

		double d3 = 0.0078125D;
		Vector3d vector3d1 = this.getMotion();
		RailShape railshape = ((AbstractRailBlock)state.getBlock()).getRailDirection(state, this.world, pos, this);
		switch(railshape) {
			case ASCENDING_EAST:
				this.setMotion(vector3d1.add(-1 * getSlopeAdjustment(), 0.0D, 0.0D));
				++d1;
				break;
			case ASCENDING_WEST:
				this.setMotion(vector3d1.add(getSlopeAdjustment(), 0.0D, 0.0D));
				++d1;
				break;
			case ASCENDING_NORTH:
				this.setMotion(vector3d1.add(0.0D, 0.0D, getSlopeAdjustment()));
				++d1;
				break;
			case ASCENDING_SOUTH:
				this.setMotion(vector3d1.add(0.0D, 0.0D, -1 * getSlopeAdjustment()));
				++d1;
		}

		vector3d1 = this.getMotion();
		Pair<Vector3i, Vector3i> pair = getMovementMatrixForShape(railshape);
		Vector3i vector3i = pair.getFirst();
		Vector3i vector3i1 = pair.getSecond();
		double d4 = (double)(vector3i1.getX() - vector3i.getX());
		double d5 = (double)(vector3i1.getZ() - vector3i.getZ());
		double d6 = Math.sqrt(d4 * d4 + d5 * d5);
		double d7 = vector3d1.x * d4 + vector3d1.z * d5;
		if (d7 < 0.0D) {
			d4 = -d4;
			d5 = -d5;
		}

		double d8 = Math.min(2.0D, Math.sqrt(horizontalMag(vector3d1)));
		vector3d1 = new Vector3d(d8 * d4 / d6, vector3d1.y, d8 * d5 / d6);
		this.setMotion(vector3d1);
		Entity entity = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);
		if (entity instanceof PlayerEntity) {
			Vector3d vector3d2 = entity.getMotion();
			double d9 = horizontalMag(vector3d2);
			double d11 = horizontalMag(this.getMotion());
			if (d9 > 1.0E-4D && d11 < 0.01D) {
				this.setMotion(this.getMotion().add(vector3d2.x * 0.1D, 0.0D, vector3d2.z * 0.1D));
				breaking = false;
			}
		}

		if (breaking && shouldDoRailFunctions()) {
			double d22 = Math.sqrt(horizontalMag(this.getMotion()));
			if (d22 < 0.03D) {
				this.setMotion(Vector3d.ZERO);
			} else {
				this.setMotion(this.getMotion().mul(0.5D, 0.0D, 0.5D));
			}
		}

		double d23 = (double)pos.getX() + 0.5D + (double)vector3i.getX() * 0.5D;
		double d10 = (double)pos.getZ() + 0.5D + (double)vector3i.getZ() * 0.5D;
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
		this.setPosition(d0, d1, d2);
		this.moveMinecartOnRail(pos);
		if (vector3i.getY() != 0 && MathHelper.floor(this.getPosX()) - pos.getX() == vector3i.getX() && MathHelper.floor(this.getPosZ()) - pos.getZ() == vector3i.getZ()) {
			this.setPosition(this.getPosX(), this.getPosY() + (double)vector3i.getY(), this.getPosZ());
		} else if (vector3i1.getY() != 0 && MathHelper.floor(this.getPosX()) - pos.getX() == vector3i1.getX() && MathHelper.floor(this.getPosZ()) - pos.getZ() == vector3i1.getZ()) {
			this.setPosition(this.getPosX(), this.getPosY() + (double)vector3i1.getY(), this.getPosZ());
		}

		this.applyDrag();
		Vector3d vector3d3 = this.getPos(this.getPosX(), this.getPosY(), this.getPosZ());
		if (vector3d3 != null && vector3d != null) {
			double d17 = (vector3d.y - vector3d3.y) * 0.05D;
			Vector3d vector3d4 = this.getMotion();
			double d18 = Math.sqrt(horizontalMag(vector3d4));
			if (d18 > 0.0D) {
				this.setMotion(vector3d4.mul((d18 + d17) / d18, 1.0D, (d18 + d17) / d18));
			}

			this.setPosition(this.getPosX(), vector3d3.y, this.getPosZ());
		}

		int j = MathHelper.floor(this.getPosX());
		int i = MathHelper.floor(this.getPosZ());
		if (j != pos.getX() || i != pos.getZ()) {
			Vector3d vector3d5 = this.getMotion();
			double d26 = Math.sqrt(horizontalMag(vector3d5));
			this.setMotion(d26 * (double)(j - pos.getX()), vector3d5.y, d26 * (double)(i - pos.getZ()));
		}

		if (shouldDoRailFunctions())
			((AbstractRailBlock)state.getBlock()).onMinecartPass(state, world, pos, this);

		if (boosted && shouldDoRailFunctions()) {
			Vector3d vector3d6 = this.getMotion();
			double d27 = Math.sqrt(horizontalMag(vector3d6));
			if (d27 > 0.01D) {
				double d19 = 0.06D;
				this.setMotion(vector3d6.add(vector3d6.x / d27 * 0.06D, 0.0D, vector3d6.z / d27 * 0.06D));
			} else {
				Vector3d vector3d7 = this.getMotion();
				double d20 = vector3d7.x;
				double d21 = vector3d7.z;
				if (railshape == RailShape.EAST_WEST) {
					if (this.isNormalCube(pos.west())) {
						d20 = 0.02D;
					} else if (this.isNormalCube(pos.east())) {
						d20 = -0.02D;
					}
				} else {
					if (railshape != RailShape.NORTH_SOUTH) {
						return;
					}

					if (this.isNormalCube(pos.north())) {
						d21 = 0.02D;
					} else if (this.isNormalCube(pos.south())) {
						d21 = -0.02D;
					}
				}

				this.setMotion(d20, vector3d7.y, d21);
			}
		}
	}

	@Override
	public ItemStack getCartItem() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isPoweredCart() {
		return false;
	}

	@Override
	protected boolean canBeRidden(Entity entityIn) {
		return true;
	}

	@Override
	public Type getMinecartType() {
		return Type.RIDEABLE;
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (!this.world.isRemote && !this.removed) {
			if (this.isInvulnerableTo(source)) {
				return false;
			} else {
				this.setRollingDirection(-this.getRollingDirection());
				this.setRollingAmplitude(10);
//				this.markVelocityChanged();
				this.setDamage(this.getDamage() + amount * 10.0F);
				boolean flag = source.getTrueSource() instanceof PlayerEntity && ((PlayerEntity)source.getTrueSource()).abilities.isCreativeMode;
				if (flag || this.getDamage() > 40.0F) {
					this.removePassengers();
					if (flag && !this.hasCustomName()) {
						this.remove();
					} else {
						this.killMinecart(source);
					}
				}

				return true;
			}
		} else {
			return true;
		}
	}

	@Override
	public void killMinecart(DamageSource source) {
		this.setDead();
	}

	@Override
	public void tick() {
		Vector3d motion = this.getMotion();
		if (!this.isBeingRidden() && Math.abs(motion.x) < 0.0001D && Math.abs(motion.y) < 0.02D && Math.abs(motion.z) < 0.0001D) {
			this.killMinecart(null);
			//this.kill();
		}

		if (this.getRollingAmplitude() > 0) {
			this.setRollingAmplitude(this.getRollingAmplitude() - 1);
		}

		if (this.getDamage() > 0.0F) {
			this.setDamage(this.getDamage() - 1.0F);
		}

		if (this.getPosY() < -64.0D) {
			this.killMinecart(null);
//			this.outOfWorld();
		}

		this.updatePortal();
		if (this.world.isRemote) {
			if (this.turnProgress > 0) {
				double d4 = this.getPosX() + (this.minecartX - this.getPosX()) / (double)this.turnProgress;
				double d5 = this.getPosY() + (this.minecartY - this.getPosY()) / (double)this.turnProgress;
				double d6 = this.getPosZ() + (this.minecartZ - this.getPosZ()) / (double)this.turnProgress;
				double d1 = MathHelper.wrapDegrees(this.minecartYaw - (double)this.rotationYaw);
				this.rotationYaw = (float)((double)this.rotationYaw + d1 / (double)this.turnProgress);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
				--this.turnProgress;
				this.setPosition(d4, d5, d6);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				this.recenterBoundingBox();
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

		} else {
			if (!this.hasNoGravity()) {
				this.setMotion(this.getMotion().add(0.0D, -0.04D, 0.0D));
			}

			int Xint = MathHelper.floor(this.getPosX());
			int Yint = MathHelper.floor(this.getPosY());
			int Zint = MathHelper.floor(this.getPosZ());
			if (this.world.getBlockState(new BlockPos(Xint, Yint - 1, Zint)).isIn(BlockTags.RAILS)) {
				--Yint;
			}

			BlockPos blockpos = new BlockPos(Xint, Yint, Zint);
			BlockState blockstate = this.world.getBlockState(blockpos);
			if (canUseRail() && AbstractRailBlock.isRail(blockstate)) {
				this.moveAlongTrack(blockpos, blockstate);
				if (blockstate.getBlock() instanceof PoweredRailBlock && ((PoweredRailBlock) blockstate.getBlock()).isActivatorRail()) {
					this.onActivatorRailPass(Xint, Yint, Zint, blockstate.get(PoweredRailBlock.POWERED));
				}
			} else {
				this.moveDerailedMinecart();
			}

			this.doBlockCollisions();
			this.rotationPitch = 0.0F;
			double d0 = this.prevPosX - this.getPosX();
			double d2 = this.prevPosZ - this.getPosZ();
			if (d0 * d0 + d2 * d2 > 0.001D) {
				this.rotationYaw = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI);
				if (this.isInReverse) {
					this.rotationYaw += 180.0F;
				}
			}

			double d3 = (double)MathHelper.wrapDegrees(this.rotationYaw - this.prevRotationYaw);
			if (d3 < -170.0D || d3 >= 170.0D) {
				this.rotationYaw += 180.0F;
				this.isInReverse = !this.isInReverse;
			}

			this.setRotation(this.rotationYaw, this.rotationPitch);
			AxisAlignedBB box;
			if (getCollisionHandler() != null) box = getCollisionHandler().getMinecartCollisionBox(this);
			else                               box = this.getBoundingBox().grow(0.2F, 0.0D, 0.2F);
			if (canBeRidden() && horizontalMag(this.getMotion()) > 0.01D) {
				List<Entity> list = this.world.getEntitiesInAABBexcluding(this, box, EntityPredicates.pushableBy(this));
				if (!list.isEmpty()) {
					for (Entity entity : list) {
						if (!(entity instanceof PlayerEntity) && !(entity instanceof IronGolemEntity) && !(entity instanceof AbstractMinecartEntity) && !this.isBeingRidden() && !entity.isPassenger()) {
							entity.startRiding(this);
						} else {
							entity.applyEntityCollision(this);
						}
					}
				}
			} else {
				for(Entity entity : this.world.getEntitiesWithinAABBExcludingEntity(this, box)) {
					if (!this.isPassenger(entity) && entity.canBePushed() && entity instanceof AbstractMinecartEntity) {
						entity.applyEntityCollision(this);
					}
				}
			}

			this.func_233566_aG_();
			if (this.isInLava()) {
				this.setOnFireFromLava();
				this.fallDistance *= 0.5F;
			}

			this.firstUpdate = false;
		}
	}

	@Override
	public IPacket<?> createSpawnPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}

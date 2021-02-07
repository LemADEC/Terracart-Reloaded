package com.kreezcraft.terracartreloaded.handlers;

import com.kreezcraft.terracartreloaded.config.Config;
import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.item.minecart.MinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RailsClickHandler {
	
	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
	public ActionResultType onEvent(PlayerInteractEvent.RightClickBlock event) {
		World worldIn = event.getEntity().world;
		BlockPos pos = event.getPos();
		PlayerEntity player = event.getPlayer();

		BlockState iblockstate = worldIn.getBlockState(pos);

		if (!AbstractRailBlock.isRail(iblockstate) || !(event.getEntity() instanceof PlayerEntity) || (!player.inventory.getCurrentItem().isEmpty())) {
			return ActionResultType.FAIL;
		} else {
			if (!worldIn.isRemote) {
				RailShape railShape = iblockstate
						.getBlock() instanceof AbstractRailBlock
								? ((AbstractRailBlock) iblockstate.getBlock()).getRailDirection(iblockstate, worldIn, pos,
										null)
								: RailShape.NORTH_SOUTH;
				double d0 = 0.0D;

				if (railShape.isAscending()) {
					d0 = 0.5D;
				}

				AbstractMinecartEntity cart;
				if (Config.COMMON.useVanillaCart.get()) {
					cart = new MinecartEntity(worldIn, (double) pos.getX() + 0.5D,
							(double) pos.getY() + 0.0625D + d0, (double) pos.getZ() + 0.5D);

					cart.getPersistentData().putBoolean("terracart", true);
				} else {
					cart = new TerraCartEntity(worldIn, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + d0,
							(double) pos.getZ() + 0.5D);
				}

				worldIn.addEntity(cart);
				player.startRiding(cart, true);
			} 

			return ActionResultType.SUCCESS;
		}
	}}

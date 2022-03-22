package com.kreezcraft.terracartreloaded.handlers;

import com.kreezcraft.terracartreloaded.config.Config;
import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RailsClickHandler {
	
	@SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
	public Event.Result onEvent(PlayerInteractEvent.RightClickBlock event) {
		Level level = event.getEntity().level;
		BlockPos pos = event.getPos();
		Player player = event.getPlayer();
		
		BlockState blockstate = level.getBlockState(pos);
		
		if ( !BaseRailBlock.isRail(blockstate)
		  || !(event.getEntity() instanceof Player)
		  || !player.getMainHandItem().isEmpty() ) {
			return Result.DENY;
		}
		
		if (!level.isClientSide) {
			RailShape railShape = blockstate
					.getBlock() instanceof BaseRailBlock
							? ((BaseRailBlock) blockstate.getBlock()).getRailDirection(blockstate, level, pos, null)
							: RailShape.NORTH_SOUTH;
			double d0 = 0.0D;
			
			if (railShape.isAscending()) {
				d0 = 0.5D;
			}
			
			AbstractMinecart cart;
			if (Config.COMMON.useVanillaCart.get()) {
				cart = new Minecart(level, (double) pos.getX() + 0.5D,
						(double) pos.getY() + 0.0625D + d0, (double) pos.getZ() + 0.5D);
				
				cart.getPersistentData().putBoolean("terracart", true);
			} else {
				cart = new TerraCartEntity(level, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.0625D + d0,
						(double) pos.getZ() + 0.5D);
			}
			
			level.addFreshEntity(cart);
			player.startRiding(cart, true);
		} 
		
		return Result.ALLOW;
	}
}

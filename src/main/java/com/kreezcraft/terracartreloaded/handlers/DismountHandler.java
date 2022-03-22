package com.kreezcraft.terracartreloaded.handlers;

import com.kreezcraft.terracartreloaded.TerraCart;
import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.Level;

import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DismountHandler {
    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public Event.Result onEvent(EntityMountEvent event) {
        Level world = event.getWorldObj();
        if ( !world.isClientSide
          && event.isDismounting()
          && ( event.getEntityBeingMounted() instanceof TerraCartEntity
            || event.getEntityBeingMounted().getPersistentData().getBoolean("terracart") ) ) {
            // cancel the event if we're already dismounting since removing the cart triggers a dismounting event again, causing a stack overflow
            if (event.getEntityBeingMounted().getPersistentData().getBoolean("terracart_isDismounting")) {
                event.setCanceled(true);
                return Result.DENY;
            }
            // mark the entity
            event.getEntityBeingMounted().getPersistentData().putBoolean("terracart_isDismounting", true);
            try {
                // actually remove the entity
                event.getEntityBeingMounted().remove(RemovalReason.DISCARDED);
            } catch (Exception exception) {
                TerraCart.logger.error("Exception during entity removal: " + exception.getMessage());
                exception.printStackTrace();
            }
            // ensure the tag is always removed so we don't get stuck somehow
            event.getEntityBeingMounted().getPersistentData().remove("terracart_isDismounting");
        }
        
        return Result.ALLOW;
    }
}

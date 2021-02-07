package com.kreezcraft.terracartreloaded.handlers;

import com.kreezcraft.terracartreloaded.entity.TerraCartEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DismountHandler {
    @SubscribeEvent(priority= EventPriority.HIGH, receiveCanceled=true)
    public ActionResultType onEvent(EntityMountEvent event) {
        World world = event.getWorldObj();
        if (!world.isRemote && event.isDismounting() && (event.getEntityBeingMounted() instanceof TerraCartEntity || event.getEntityBeingMounted().getPersistentData().getBoolean("terracart"))) {
            event.getEntityBeingMounted().remove();
        }

        return ActionResultType.SUCCESS;
    }
}

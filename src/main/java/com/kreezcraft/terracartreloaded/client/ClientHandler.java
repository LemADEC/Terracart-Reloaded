package com.kreezcraft.terracartreloaded.client;

import com.kreezcraft.terracartreloaded.CartRegistry;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.MinecartRenderer;

import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		EntityRenderers.register(CartRegistry.TERRA_CART.get(), (context) -> new MinecartRenderer<>(context, ModelLayers.MINECART));
	}
}

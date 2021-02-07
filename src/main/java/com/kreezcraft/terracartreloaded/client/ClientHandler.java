package com.kreezcraft.terracartreloaded.client;

import com.kreezcraft.terracartreloaded.CartRegistry;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
	public static void onClientSetup(final FMLClientSetupEvent event) {
		RenderingRegistry.registerEntityRenderingHandler(CartRegistry.TERRA_CART.get(), MinecartRenderer::new);
	}
}

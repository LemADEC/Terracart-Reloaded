package com.kreezcraft.terracartreloaded.config;

import com.kreezcraft.terracartreloaded.TerraCart;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import org.apache.commons.lang3.tuple.Pair;

public class Config {
	public static class Common {
		public final BooleanValue useVanillaCart;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.comment("MINECART TWEAKS")
					.push("general");
			
			useVanillaCart = builder
					.comment("Set this to true to screw your users with a cart that has friction!")
					.define("useVanillaCart", false);
			
			builder.pop();
		}
	}
	
	public static final ForgeConfigSpec commonSpec;
	public static final Common COMMON;
	
	static {
		final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
		commonSpec = specPair.getRight();
		COMMON = specPair.getLeft();
	}
	
	@SubscribeEvent
	public static void onLoad(final ModConfigEvent.Loading configEvent) {
		TerraCart.logger.debug("Loaded TerraCart' config file {}", configEvent.getConfig().getFileName());
	}
	
	@SubscribeEvent
	public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
		TerraCart.logger.fatal("TerraCart' config just got changed on the file system!");
	}
}

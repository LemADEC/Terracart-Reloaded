package com.kreezcraft.terracartreloaded;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.property.Properties;

import org.apache.logging.log4j.Level;

public class Config {
	public static String CATEGORY_GENERAL = "MINECART TWEAKS";
	public static Property use_vanilla_cart;
	public static Configuration cfg = TerraCart.config;

	public static void readConfig() {
		try {
			cfg.load();

			use_vanilla_cart = cfg.get(CATEGORY_GENERAL, "Use vanilla cart", false,
					"Set this to true to screw your users with a cart that has friction!");

		} catch (Exception e1) {
			TerraCart.logger.log(Level.ERROR, "Problem loading config file!", e1);
		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}
}

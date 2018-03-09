package com.kreezcraft.terracartreloaded;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.property.Properties;

import java.io.File;

import org.apache.logging.log4j.Level;

public class Config {
	public static String CATEGORY_GENERAL = "MINECART TWEAKS";
	public static Property use_vanilla_cart;
	public static Configuration cfg = TerraCart.config;

	public static void readConfig() {
		try {
			cfg.load();
			cfg.setCategoryComment(CATEGORY_GENERAL, "This is a valid category, you can delete the other one if it is exists");
			use_vanilla_cart = cfg.get(CATEGORY_GENERAL, "Use vanilla cart", false,
					"Set this to true to screw your users with a cart that has friction!");

		} catch (Exception e1) {
			TerraCart.logger.log(Level.ERROR, "Problem loading config file!", e1);
		} finally {
			if (cfg.hasChanged()) {
				if(cfg.hasCategory(TerraCart.MODID)) {
					if(TerraCart.configFile.delete()) {
						System.out.println("old config deleted");
						TerraCart.configFile = new File(TerraCart.directory, TerraCart.MODID+".cfg"); 
						TerraCart.config = new Configuration(TerraCart.configFile);
						cfg = TerraCart.config;
					} else {
						System.out.println("there was problem deleting the old configuration");
					}
				}
				cfg.save();
			}
		}
	}
}

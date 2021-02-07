package com.kreezcraft.terracartreloaded;

import com.kreezcraft.terracartreloaded.client.ClientHandler;
import com.kreezcraft.terracartreloaded.commands.CommandTC;
import com.kreezcraft.terracartreloaded.config.Config;
import com.kreezcraft.terracartreloaded.handlers.DismountHandler;
import com.kreezcraft.terracartreloaded.handlers.RailsClickHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TerraCart.MODID)
public class TerraCart {
    public static final String MODID = "terracart";
    public static final Logger logger = LogManager.getLogger(TerraCart.MODID);

    public TerraCart() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.commonSpec);
        eventBus.register(Config.class);

        CartRegistry.ENTITIES.register(eventBus);

        MinecraftForge.EVENT_BUS.register(new RailsClickHandler());
        MinecraftForge.EVENT_BUS.register(new DismountHandler());

        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::onClientSetup);
        });
    }

    public void onCommandRegister(RegisterCommandsEvent event) {
        CommandTC.initializeCommands(event.getDispatcher());
    }
}

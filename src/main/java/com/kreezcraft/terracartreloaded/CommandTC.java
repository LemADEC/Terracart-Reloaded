package com.kreezcraft.terracartreloaded;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.config.Configuration;

public class CommandTC extends CommandBase {

	public CommandTC() {
		aliases = Lists.newArrayList(TerraCart.MODID, "tc","terracart");
	}
	
	private final List<String> aliases;
	

	@Override
	@Nonnull
	public String getName() {
		return "tc";
	}

	@Override
	@Nonnull
	public String getUsage(@Nonnull ICommandSender sender) {
		return "/tc <vanillacart|vc> <true|false>\n    force using the friction based vanilla cart";
	}

	@Override
	@Nonnull
	public List<String> getAliases() {
		return aliases;
	}

	@Override
	public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args)
			throws CommandException {
		boolean theTruth;
		if (args.length < 2) {
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}
		String action = args[0].toLowerCase();
		String truth = args[1].toLowerCase();

		if(action.equals("vanillacart")||action.equals("vc")) {
		if (truth.equalsIgnoreCase("true")) {
			theTruth = true;
		} else if (truth.equalsIgnoreCase("false")) {
			theTruth = false;
		} else {
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}
		} else {
			sender.sendMessage(new TextComponentString(getUsage(sender)));
			return;
		}
		
		sender.sendMessage(new TextComponentString("It is "+theTruth+" that the vanilla cart will be used"));
		Config.use_vanilla_cart.set(theTruth);
		sender.sendMessage(new TextComponentString("Config updated"));
		Config.cfg.save();

		return;
	
	}

	@Override
	@Nonnull
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			@Nullable BlockPos targetPos) {
		return Collections.emptyList();
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if(server.getPlayerList().getOppedPlayers().getGameProfileFromName(sender.getName()) != null || server.isSinglePlayer()) {
			return true; //ops can use the command
		} 
		return false;
	}

}

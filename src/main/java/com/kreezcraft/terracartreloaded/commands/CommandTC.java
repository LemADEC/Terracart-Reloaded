package com.kreezcraft.terracartreloaded.commands;

import com.kreezcraft.terracartreloaded.config.Config;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandTC {

	public static void initializeCommands (CommandDispatcher<CommandSource> dispatcher) {
		LiteralCommandNode<CommandSource> literalcommandnode =
				dispatcher.register(Commands.literal("terracart").requires((p_198721_0_) -> p_198721_0_.hasPermissionLevel(2))
						.then(Commands.literal("vannillacart").executes(CommandTC::sendCurrentOption)
								.then(Commands.argument("boolean", BoolArgumentType.bool()).executes(CommandTC::setOption)))
						.then(Commands.literal("vc").executes(CommandTC::sendCurrentOption)
								.then(Commands.argument("boolean", BoolArgumentType.bool()).executes(CommandTC::setOption))));

		LiteralCommandNode<CommandSource> literalcommandnode2 =
				dispatcher.register(Commands.literal("tc").requires((p_198721_0_) -> p_198721_0_.hasPermissionLevel(2))
						.then(Commands.literal("vannillacart").executes(CommandTC::sendCurrentOption)
								.then(Commands.argument("boolean", BoolArgumentType.bool()).executes(CommandTC::setOption)))
						.then(Commands.literal("vc").executes(CommandTC::sendCurrentOption)
								.then(Commands.argument("boolean", BoolArgumentType.bool()).executes(CommandTC::setOption))));
	}

	private static int sendCurrentOption(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		ctx.getSource().sendFeedback(new TranslationTextComponent("commands.vanillacart.get.current", Config.COMMON.useVanillaCart.get()), false);
		return 0;
	}

	private static int setOption(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
		boolean option = BoolArgumentType.getBool(ctx, "boolean");
		Config.COMMON.useVanillaCart.set(option);
		Config.COMMON.useVanillaCart.save();
		ctx.getSource().sendFeedback(new TranslationTextComponent("commands.vanillacart.set.feedback", option), true);
		ctx.getSource().sendFeedback(new TranslationTextComponent("commands.vanillacart.set.updated"), true);
		return 0;
	}
}

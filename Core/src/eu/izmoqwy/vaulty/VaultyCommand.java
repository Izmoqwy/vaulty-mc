/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty;

import com.google.common.collect.Maps;
import eu.izmoqwy.vaulty.exceptions.CommandException;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

public abstract class VaultyCommand implements CommandExecutor, TabCompleter {

	@Getter
	private final String name;
	private final boolean playerOnly;

	@Setter
	private String prefix = VaultyCore.PREFIX;

	protected Map<String, String> helpCommands = Maps.newLinkedHashMap();

	public VaultyCommand(String name, boolean playerOnly) {
		Command command = Bukkit.getPluginCommand(name);
		if (command != null)
			this.name = command.getName();
		else
			this.name = name;

		this.playerOnly = playerOnly;
	}

	protected void send(CommandSender commandSender, String message) {
		commandSender.sendMessage(message != null ? prefix + message : " ");
	}

	protected void sendHelp(CommandSender commandSender) {
		if (helpCommands == null || helpCommands.isEmpty()) {
			commandSender.sendMessage(prefix + "§cAucune aide n'est disponible pour cette commande !");
			return;
		}

		commandSender.sendMessage(prefix + "§3Aide pour la commande §9/" + name + "§3:");
		helpCommands.forEach((command, help) -> commandSender.sendMessage("§6/" + name + " " + command + " §8- §e" + help));
	}

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
		if (command.getName().equals(name)) {
			if (playerOnly && !(commandSender instanceof Player)) {
				commandSender.sendMessage("§cCette commande est réservée aux joueurs !");
				return true;
			}

			try {
				executeCommand(commandSender, args);
			}
			catch (CommandException ex) {
				send(commandSender, "§c" + ex.getMessage());
			}
			return true;
		}
		return false;
	}

	public abstract void executeCommand(CommandSender commandSender, String[] args);

	@Override
	public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
		return null;
	}

	protected boolean match(String[] args, int index, String... values) {
		if (args.length <= index)
			return false;

		for (String value : values) {
			if (args[index].equalsIgnoreCase(value))
				return true;
		}
		return false;
	}

	@Contract("false, _ -> fail")
	protected void checkArgument(boolean valid, String message) {
		if (!valid)
			throw new CommandException(message);
	}

	protected void missingArg(String[] args, int index, String needed) {
		checkArgument(args.length > index, "Argument manquant en position " + (index + 1) + ": " + needed);
	}

	protected Player getTarget(String[] args, int index, String needed) {
		missingArg(args, index, needed);
		Player target = Bukkit.getPlayerExact(args[index]);
		if (target == null)
			throw new CommandException("Le joueur §6" + args[index] + " §cn'éxiste pas ou n'est pas en ligne.");
		return target;
	}

	protected OfflinePlayer getOfflineTarget(String[] args, int index, String needed) {
		missingArg(args, index, needed);
		OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[index]);
		if (target == null)
			throw new CommandException("Le joueur §6" + args[index] + "§c n'éxite pas.");
		return target;
	}

}

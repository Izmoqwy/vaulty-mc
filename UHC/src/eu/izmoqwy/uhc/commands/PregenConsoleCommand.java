package eu.izmoqwy.uhc.commands;

import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.VaultyCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PregenConsoleCommand extends VaultyCommand {
	public PregenConsoleCommand() {
		super("pregen", false);
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		if (commandSender instanceof Player) {
			send(commandSender, ChatColor.DARK_RED + "(-) Action de console");
			return;
		}

		String reason = "§cRequête administrative";
		Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(reason));
		UHCWorldManager.get.getPreGenerator().preGenerate(1);
	}
}

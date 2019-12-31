/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub.moderation;

import eu.izmoqwy.vaulty.config.VaultyConfig;
import eu.izmoqwy.vaultyhub.VaultyHub;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Moderation extends VaultyConfig {

	@Getter
	public static final Moderation moderation = new Moderation();

	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	private Moderation() {
		super(new File(VaultyHub.getPlugin(VaultyHub.class).getDataFolder(), "sanctions.data"));
	}

	public boolean isBanned(OfflinePlayer player) {
		String path = getPath(player, "bans", "endsAt");
		if (getYamlConfiguration().getLong(path, -1) != -1) {
			if (getYamlConfiguration().getLong(path) > System.currentTimeMillis())
				return true;

			getYamlConfiguration().set(getPath(player, "bans", null), null);
			handledSave();
		}
		return false;
	}

	public void ban(CommandSender from, OfflinePlayer player, String reason, long endsAt) {
		String path = getPath(player, "bans", "");
		getYamlConfiguration().set(path + "endsAt", endsAt);
		getYamlConfiguration().set(path + "reason", reason);
		getYamlConfiguration().set(path + "by", (from instanceof Player) ? ((Player) from).getUniqueId().toString() : from.getName());
		handledSave();
	}

	public String getBanMessage(OfflinePlayer player) {
		if (!isBanned(player))
			return "§4Erreur";

		String reason = getYamlConfiguration().getString(getPath(player, "bans", "reason"), "§cNon spécifié");
		return "§cVous avez été banni.\n\n§fRaison: §b" + reason +
				"\n§fExpire le: §a" + sdf.format(new Date(getYamlConfiguration().getLong(getPath(player, "bans", "endsAt"))));
	}

	public boolean isMuted(OfflinePlayer player) {
		String path = getPath(player, "mutes", "endsAt");
		if (getYamlConfiguration().getLong(path, -1) != -1) {
			if (getYamlConfiguration().getLong(path) > System.currentTimeMillis())
				return true;

			getYamlConfiguration().set(getPath(player, "mutes", null), null);
			handledSave();
		}
		return false;
	}

	public void mute(CommandSender from, OfflinePlayer player, String reason, long endsAt) {
		String path = getPath(player, "mutes", "");
		getYamlConfiguration().set(path + "endsAt", endsAt);
		getYamlConfiguration().set(path + "reason", reason);
		getYamlConfiguration().set(path + "by", (from instanceof Player) ? ((Player) from).getUniqueId().toString() : from.getName());
		handledSave();
	}

	public void sendMuteMessages(Player player, String init) {
		if (!isMuted(player))
			return;

		String reason = getYamlConfiguration().getString(getPath(player, "mutes", "reason"));
		player.sendMessage(init);
		player.sendMessage("§fRaison: " + (reason != null ? "§b" + reason : "§cNon spécifié"));
		player.sendMessage("§fExpire le: §a" + sdf.format(new Date(getYamlConfiguration().getLong(getPath(player, "mutes", "endsAt")))));
	}

	private String getPath(OfflinePlayer player, String section, String key) {
		return section + "." + player.getUniqueId().toString().replace("-", "") + (key != null ? "." + key : "");
	}

}

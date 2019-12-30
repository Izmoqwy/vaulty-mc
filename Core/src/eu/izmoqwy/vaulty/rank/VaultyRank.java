/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.rank;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.config.VaultyConfig;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class VaultyRank {

	private static VaultyConfig config = new VaultyConfig(new File(VaultyCore.getInstance().getDataFolder(), "ranks.yml"));
	private static Map<UUID, Rank> cache = Maps.newHashMap();

	public static Rank get(OfflinePlayer player) {
		Preconditions.checkNotNull(player);

		UUID id = player.getUniqueId();
		if (cache.containsKey(id)) {
			Rank rank = cache.get(id);
			return rank != null ? rank : Rank.DEFAULT;
		}

		Rank storedRank = Rank.getById(config.getYamlConfiguration().getInt(getPath(id), -1));
		cache.put(player.getUniqueId(), storedRank);
		return storedRank;
	}

	public static void set(OfflinePlayer player, Rank rank) throws IOException {
		Preconditions.checkNotNull(player);

		UUID id = player.getUniqueId();
		if (rank == null || rank == Rank.DEFAULT)
			config.getYamlConfiguration().set(getPath(id), null);
		else
			config.getYamlConfiguration().set(getPath(id), rank.getId());
		config.save();

		cache.put(id, rank == null || rank == Rank.DEFAULT ? null : rank);
	}

	private static String getPath(UUID id) {
		return "players." + id + "." + "rank";
	}

}

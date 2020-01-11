/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.oss;

import com.google.common.collect.Lists;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ServerSlice {

	@Getter
	private final String name;
	private List<Player> onlinePlayers = Lists.newArrayList();

	@Setter
	private ServerListener listener = new ServerListener() {
	};

	public ServerSlice(String name) {
		this.name = name;
	}

	public boolean isDefault() {
		return this.equals(OSS.getMainServer());
	}

	public void onJoin(Player player, boolean nativeJoin) {
		onlinePlayers.add(player);
		listener.onJoin(player);
		if (!nativeJoin) {
			PlayerUtil.showPlayersMutually(player, onlinePlayers.toArray(new Player[0]));
		}
	}

	public void onQuit(Player player, boolean nativeQuit) {
		onlinePlayers.remove(player);
		listener.onQuit(player);
		if (!nativeQuit) {
			PlayerUtil.hidePlayersMutually(player, onlinePlayers.toArray(new Player[0]));
		}
	}

	public void broadcastMessage(String message) {
		onlinePlayers.forEach(player -> player.sendMessage(message));
	}

	public Collection<Player> getOnlinePlayers() {
		return Collections.unmodifiableCollection(onlinePlayers);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ServerSlice)) return false;
		ServerSlice that = (ServerSlice) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(onlinePlayers, that.onlinePlayers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, onlinePlayers);
	}
}

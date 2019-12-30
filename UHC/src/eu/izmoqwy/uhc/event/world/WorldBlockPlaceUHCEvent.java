/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.world;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;

@Getter
public class WorldBlockPlaceUHCEvent extends CancellableEvent {

	private Player player;
	private Block block;
	private BlockPlaceEvent bukkitEvent;

	public WorldBlockPlaceUHCEvent(BlockPlaceEvent bukkitEvent) {
		super(false);
		this.player = bukkitEvent.getPlayer();
		this.block = bukkitEvent.getBlock();
		this.bukkitEvent = bukkitEvent;
	}
}

/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.world;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class WorldBlockBreakUHCEvent extends CancellableEvent {

	private Player player;
	private Block block;
	private BlockBreakEvent bukkitEvent;

	@Setter
	private ItemStack customDrop;
	@Setter
	private int expToDrop;

	public WorldBlockBreakUHCEvent(BlockBreakEvent bukkitEvent) {
		super(false);
		this.player = bukkitEvent.getPlayer();
		this.block = bukkitEvent.getBlock();
		this.bukkitEvent = bukkitEvent;
		this.expToDrop = bukkitEvent.getExpToDrop();
	}
}

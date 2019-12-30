/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class PlayerDropOrPickupUHCEvent extends CancellableEvent {

	private Player player;
	private ItemStack itemStack;
	private boolean dropping;

	public PlayerDropOrPickupUHCEvent(Player player, ItemStack itemStack, boolean dropping) {
		super(false);
		this.player = player;
		this.itemStack = itemStack;
		this.dropping = dropping;
	}
}

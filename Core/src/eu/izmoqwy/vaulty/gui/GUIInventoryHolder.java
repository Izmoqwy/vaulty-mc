/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.gui;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
class GUIInventoryHolder implements InventoryHolder {

	private VaultyInventory vaultyInventory;

	protected GUIInventoryHolder(VaultyInventory vaultyInventory) {
		this.vaultyInventory = vaultyInventory;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}
}

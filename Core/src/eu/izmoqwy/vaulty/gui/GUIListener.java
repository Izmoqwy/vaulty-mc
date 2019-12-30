/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface GUIListener {

	default boolean onOpen(Player player) { return true; }
	default Map<Integer, ItemStack> load(Player player) { return null; }

	default void onClick(Player player, ItemStack clickedItem, int slot) {}
	default void onClick_event(Player player, ItemStack clickedItem, int slot, InventoryClickEvent event) {}

	default void onClose(Player player) {}

}

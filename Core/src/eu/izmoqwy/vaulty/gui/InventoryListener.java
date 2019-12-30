/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryListener implements Listener {

	private boolean isVaultyInventory(Inventory inventory) {
		return inventory != null && inventory.getHolder() != null && inventory.getHolder() instanceof GUIInventoryHolder;
	}

	private VaultyInventory getInventory(InventoryEvent event) {
		if (isVaultyInventory(event.getInventory()))
			return ((GUIInventoryHolder) event.getInventory().getHolder()).getVaultyInventory();
		return null;
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryOpenNormal(InventoryOpenEvent event) {
		if (event.getInventory() == null || event.getPlayer() == null)
			return;

		VaultyInventory vaultyInventory = getInventory(event);
		if (vaultyInventory == null)
			return;

		Player player = (Player) event.getPlayer();
		for (GUIListener listener : vaultyInventory.getListeners()) {
			if (!listener.onOpen(player))
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onInventoryOpenMonitor(InventoryOpenEvent event) {
		if (event.getInventory() == null || event.getPlayer() == null)
			return;

		VaultyInventory vaultyInventory = getInventory(event);
		if (vaultyInventory == null)
			return;

		Player player = (Player) event.getPlayer();
		Map<Integer, ItemStack> toAdd;
		for (GUIListener listener : vaultyInventory.getListeners()) {
			if ((toAdd = listener.load(player)) != null) {
				toAdd.forEach((slot, item) -> event.getInventory().setItem(slot, item));
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClose(InventoryCloseEvent event) {
		if (event.getInventory() == null || event.getPlayer() == null)
			return;

		VaultyInventory vaultyInventory = getInventory(event);
		if (vaultyInventory == null)
			return;

		Player player = (Player) event.getPlayer();
		for (GUIListener listener : vaultyInventory.getListeners()) {
			listener.onClose(player);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null || event.getClickedInventory() == null || event.getWhoClicked() == null)
			return;

		VaultyInventory vaultyInventory = getInventory(event);
		if (vaultyInventory == null)
			return;

		event.setCancelled(true);
		if (event.getCurrentItem() == null)
			return;

		Player player = (Player) event.getWhoClicked();
		ItemStack currentItem = event.getCurrentItem();
		int slot = event.getSlot();

		for (GUIListener listener : vaultyInventory.getListeners()) {
			listener.onClick(player, currentItem, slot);
			listener.onClick_event(player, currentItem, slot, event);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		if (isVaultyInventory(event.getDestination()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryDrag(InventoryDragEvent event) {
		if (isVaultyInventory(event.getInventory()))
			event.setCancelled(true);
	}
}

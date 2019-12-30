/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Objects;

@Getter
public abstract class VaultyInventory {

	@Getter(AccessLevel.NONE)
	private final int internalId;
	@Getter(AccessLevel.NONE)
	private final GUIInventoryHolder holder;

	private final VaultyInventory parent;
	private GUIListener[] listeners = new GUIListener[0];

	private String title;
	private boolean shared;

	private int rows = 3;
	private InventoryType inventoryType = InventoryType.CHEST;

	private Map<Integer, ItemStack> slots;
	private Inventory bukkitInventory;

	public VaultyInventory(VaultyInventory parent, String title, boolean shared) {
		this.internalId = ++GUIManager.idIncrementer;

		this.parent = parent;
		if (title.length() > 32)
			title = title.substring(0, 32);
		this.title = title;
		this.shared = shared;

		this.holder = new GUIInventoryHolder(this);
		this.slots = Maps.newHashMap();
	}

	public void setRows(int rows) {
		Preconditions.checkArgument(rows > 0 && rows < 7);
		this.rows = rows;
		breakBukkitInventory();
	}

	public void setInventoryType(InventoryType inventoryType) {
		this.inventoryType = inventoryType != null ? inventoryType : InventoryType.CHEST;
		breakBukkitInventory();
	}

	public void addListener(GUIListener listener) {
		if (listener == null)
			return;

		listeners = (GUIListener[]) ArrayUtils.add(listeners, listener);
		breakBukkitInventory();
	}

	public void setItem(int slot, ItemStack itemStack) {
		slots.put(slot, itemStack);
		if (bukkitInventory != null && shared)
			bukkitInventory.setItem(slot, itemStack);
	}

	public void breakBukkitInventory() {
		if (bukkitInventory == null)
			return;

		if (!bukkitInventory.getViewers().isEmpty())
			bukkitInventory.getViewers().forEach(HumanEntity::closeInventory);
		bukkitInventory = null;
	}

	public Inventory toBukkitInventory() {
		if (bukkitInventory == null) {
			if (inventoryType == InventoryType.CHEST) bukkitInventory = Bukkit.createInventory(holder, rows * 9, title);
			else bukkitInventory = Bukkit.createInventory(holder, inventoryType, title);
			slots.forEach(bukkitInventory::setItem);
		}

		if (bukkitInventory != null) {
			if (shared)
				return bukkitInventory;
			else return cloneBukkitInventory(bukkitInventory);
		}
		return null;
	}

	public void open(Player player) {
		Preconditions.checkNotNull(player);
		Inventory inventory = toBukkitInventory();
		if (inventory == null)
			return;
		player.openInventory(inventory);
	}

	private Inventory cloneBukkitInventory(Inventory original) {
		Inventory cloned = original.getType() != InventoryType.CHEST ?
				Bukkit.createInventory(original.getHolder(), original.getType(), original.getTitle()) :
				Bukkit.createInventory(original.getHolder(), original.getSize(), original.getTitle());
		cloned.setContents(original.getContents());
		cloned.setMaxStackSize(original.getMaxStackSize());
		return cloned;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VaultyInventory that = (VaultyInventory) o;
		return internalId == that.internalId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(internalId);
	}
}

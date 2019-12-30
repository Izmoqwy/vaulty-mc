/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.inventory;

import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.WaitingRoom;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.vaulty.nms.NMS;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryGUI extends VaultyInventory implements GUIListener, GUIConfigurable {

	private GameComposer gameComposer;
	private EnchantSelector enchantSelector;

	public InventoryGUI(VaultyInventory parent, String title, GameComposer gameComposer) {
		super(parent, title, true);
		this.gameComposer = gameComposer;

		this.enchantSelector = new EnchantSelector(this, "§e§lLivres");

		setItem(0, new ItemBuilder(Material.ANVIL).name("§e§lEnclume").appendLore("§7Ouvre une enclume").toItemStack());
		setItem(1, new ItemBuilder(Material.ENCHANTED_BOOK).name("§e§lLivres").appendLore("§7Obtenir un livre").toItemStack());
		setItem(8, new ItemBuilder(Material.BOOK).name("§a§lSauvegarder").appendLore("§7Sauvegarder l'inventaire").toItemStack());

		setRows(1);
		addListener(this);
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		WaitingRoom waitingRoom = GameManager.get.getWaitingRoom();
		if (waitingRoom.getComposerGUI().getEditingInventory() != player)
			return;

		switch (slot) {
			case 0:
				NMS.packets.openAnvil(player, "§e§lEnclume");
				break;
			case 1:
				enchantSelector.open(player);
				break;
			case 8:
				gameComposer.setStartingInventoryContents(player.getInventory().getContents());
				gameComposer.setStartingInventoryArmorContents(player.getInventory().getArmorContents());
				player.setGameMode(GameMode.SURVIVAL);
				waitingRoom.resetInventory(player);
				waitingRoom.getComposerGUI().setEditingInventory(null);
				player.closeInventory();
				break;
		}
	}
}

/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.inventory;

import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EnchantSelector extends VaultyInventory implements GUIListener {
	public EnchantSelector(VaultyInventory parent, String title) {
		super(parent, title, true);

		List<Enchantment> enchantments = Arrays.asList(Enchantment.values());
		int allowedRows = (int) Math.min(Math.ceil(enchantments.size() / 9d), 5);
		for (int i = 0; i < enchantments.size(); i++) {
			if (i > (allowedRows + 1) * 9)
				break;

			Enchantment enchantment = enchantments.get(i);
			setItem(i, new ItemBuilder(enchantment, 1).appendLore("§7Choisir le niveau").toItemStack());
		}
		setRows(allowedRows + 1);
		setItem(allowedRows * 9, new ItemBuilder(Material.ARROW).name("§cRetour").appendLore("§7Retourner au menu précédent").toItemStack());
		addListener(this);
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		if (slot == (getRows() - 1) * 9) {
			getParent().open(player);
			return;
		}

		if (slot < Enchantment.values().length) {
			Enchantment enchantment = Enchantment.values()[slot];
			new EnchantLevelSelector(this, getTitle() + " §7» §6" + enchantment.getName(), enchantment).open(player);
		}
	}

	private static class EnchantLevelSelector extends VaultyInventory implements GUIListener {
		private Enchantment enchantment;

		public EnchantLevelSelector(VaultyInventory parent, String title, Enchantment enchantment) {
			super(parent, title, true);
			this.enchantment = enchantment;

			for (int i = 0; i < enchantment.getMaxLevel(); i++) {
				setItem(i, new ItemBuilder(enchantment, i + 1).appendLore("§7Cliquer pour obtenir").toItemStack());
			}

			setRows(2);
			addListener(this);
		}

		@Override
		public void onClick(Player player, ItemStack clickedItem, int slot) {
			if (slot == (getRows() - 1) * 9) {
				getParent().open(player);
				return;
			}

			if (clickedItem != null) {
				ItemUtil.give(player, new ItemBuilder(enchantment, slot + 1).toItemStack());
				player.closeInventory();
			}
		}
	}
}
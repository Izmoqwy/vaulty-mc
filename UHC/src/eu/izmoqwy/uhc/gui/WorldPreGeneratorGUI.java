/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui;

import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class WorldPreGeneratorGUI extends VaultyInventory implements GUIListener, GUIConfigurable {

	@GUISetting(name = "Nombre", icon = Material.GRASS,
			max = 10)
	private int amount = 1;
	private SettingValue amountSetting;

	public WorldPreGeneratorGUI() throws NoSuchFieldException {
		super(null, "§3§lPré-Génération", true);

		Field amountField = getClass().getDeclaredField("amount");
		amountField.setAccessible(true);
		amountSetting = new SettingValue(this, "§3§lPré-Génération §7» §6Nombre", amountField, this, amountField.getAnnotation(GUISetting.class));
		setItem(0, amountSetting.bakeIcon());
		setItem(4, new ItemBuilder(Material.REDSTONE).name("§a§lLancer").appendLore("§cTous les joueurs seront expulsés", "§cet le serveur sera temporairement fermé").quickEnchant().toItemStack());

		setRows(1);
		addListener(this);
	}

	@Override
	public boolean onOpen(Player player) {
		setItem(0, amountSetting.bakeIcon());
		return true;
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		switch (slot) {
			case 0:
				amountSetting.getInventory().open(player);
				break;
			case 4:
				start();
				break;
		}
	}

	private void start() {
		String reason = "§cRequête administrative";
		Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(reason));
		UHCWorldManager.get.getPreGenerator().preGenerate(amount);
	}
}

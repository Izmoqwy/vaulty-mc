/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui;

import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.vaulty.utils.TimeUtil;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.lang.reflect.Field;
import java.util.function.Consumer;

class SettingValue {

	private Field field;
	private GUIConfigurable configurable;
	private GUISetting setting;

	@Getter
	private VaultyInventory inventory;

	public SettingValue(VaultyInventory parent, String title, Field field, GUIConfigurable configurable, GUISetting setting) {
		this.field = field;
		this.configurable = configurable;
		this.setting = setting;

		Class<?> type = field.getType();
		if (type.equals(boolean.class))
			return;

		inventory = new VaultyInventory(parent, title, true) {
		};
		inventory.setRows(1);

		Consumer<Integer> onSlot = null;
		if (type.equals(float.class) || type.equals(int.class)) {
			Object step1, step5, step10;
			if (type.equals(int.class)) {
				int step = setting.step();
				if (setting.duration()) {
					step1 = TimeUtil.fromSeconds(step);
					step5 = TimeUtil.fromSeconds(step * 5);
					step10 = TimeUtil.fromSeconds(step * 10);
				}
				else {
					step1 = step;
					step5 = step * 5;
					step10 = step * 10;
				}
				onSlot = slot -> {
					try {
						int newVal = field.getInt(configurable);
						if (slot >= 0 && slot <= 2)
							newVal -= slot == 0 ? step * 10 : (slot == 1 ? step * 5 : step);
						else if (slot >= 6 && slot <= 8)
							newVal += slot == 6 ? step : (slot == 7 ? step * 5 : step * 10);
						field.setInt(configurable, Math.min(Math.max(newVal, setting.min()), setting.max()));
						if (field.getName().equals("maxPlayers") && GameManager.get.getWaitingRoom() != null)
							GameManager.get.getWaitingRoom().updatePlayerCount();
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				};
			}
			else {
				float step = setting.stepf();
				step1 = step;
				step5 = step * 5;
				step10 = step * 10;
				onSlot = slot -> {
					try {
						float newVal = field.getFloat(configurable);
						if (slot >= 0 && slot <= 2)
							newVal -= slot == 2 ? step : (slot == 1 ? step * 5 : step * 10);
						else if (slot >= 6 && slot <= 8)
							newVal += slot == 6 ? step : (slot == 7 ? step * 5 : step * 10);
						field.setFloat(configurable, Math.min(Math.max(newVal, setting.minf()), setting.maxf()));
					}
					catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				};
			}
			inventory.setItem(0, bakeBanner(step10, false));
			inventory.setItem(1, bakeBanner(step5, false));
			inventory.setItem(2, bakeBanner(step1, false));

			inventory.setItem(4, bakeIcon());

			inventory.setItem(6, bakeBanner(step1, true));
			inventory.setItem(7, bakeBanner(step5, true));
			inventory.setItem(8, bakeBanner(step10, true));
		}

		Consumer<Integer> finalOnSlot = onSlot;
		if (finalOnSlot == null)
			return;

		inventory.addListener(new GUIListener() {
			@Override
			public void onClick(Player player, ItemStack clickedItem, int slot) {
				if (slot == 4) {
					inventory.getParent().open(player);
					return;
				}

				finalOnSlot.accept(slot);
				inventory.setItem(4, bakeIcon());
			}
		});
	}

	private static ItemStack bakeBanner(Object val, boolean add) {
		return new ItemBuilder(Material.BANNER).dyeColor(add ? DyeColor.GREEN : DyeColor.RED).name((add ? "§a+" : "§c-") + val).toItemStack();
	}

	@SuppressWarnings("deprecation")
	public ItemStack bakeIcon() {
		return new ItemBuilder(new MaterialData(setting.icon(), setting.iconData()))
				.name("§6" + (setting.name().isEmpty() ? field.getName() : setting.name())).appendLore("§7Valeur: §e" + getStrValue())
				.addFlags(ItemFlag.HIDE_ATTRIBUTES).toItemStack();
	}

	public void toggle() {
		if (field.getType().equals(boolean.class)) {
			try {
				field.setBoolean(configurable, !field.getBoolean(configurable));
				if (field.getName().equals("hideCoordinates") && GameManager.get.getWaitingRoom() != null)
					GameManager.get.getWaitingRoom().updateDebugInfoState();
			}
			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	private String getStrValue() {
		try {
			Class<?> type = field.getType();
			if (type.equals(boolean.class))
				return field.getBoolean(configurable) ? "§aActivé" : "§cDésactivé";
			if (type.equals(int.class) && setting.duration()) {
				return TimeUtil.fromSeconds(field.getInt(configurable));
			}
			return field.get(configurable).toString();
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

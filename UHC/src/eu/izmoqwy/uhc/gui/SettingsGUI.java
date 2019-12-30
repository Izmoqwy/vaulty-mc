/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.vaulty.utils.ReflectionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class SettingsGUI extends VaultyInventory implements GUIListener {

	private Map<Integer, SettingValue> settings;

	public SettingsGUI(VaultyInventory parent, String title, GUIConfigurable configurable, Class<? extends GameComposer> defaultComposerClass) {
		super(parent, title, true);
		settings = Maps.newHashMap();

		String[] titleParts = title.split(" ");
		String prefix = titleParts[titleParts.length - 1] + " §7» §6";

		List<Field> fields = ReflectionUtil.getFieldsAnnotatedWith(defaultComposerClass != null ? defaultComposerClass : configurable.getClass(), GUISetting.class);
		Map<String, List<SettingValue>> groups = Maps.newLinkedHashMap();
		for (Field field : fields) {
			field.setAccessible(true);
			GUISetting setting = field.getAnnotation(GUISetting.class);
			List<SettingValue> settings = groups.getOrDefault(setting.group(), Lists.newArrayList());
			settings.add(new SettingValue(this, prefix + setting.name(), field, configurable, setting));
			groups.put(setting.group(), settings);
		}

		int row = 0;
		loop:
		for (Map.Entry<String, List<SettingValue>> entry : groups.entrySet()) {
			List<SettingValue> list = entry.getValue();
			for (int i = 0; i < Math.ceil(list.size() / 9f); i++) {
				if (++row > 5)
					continue loop;
				for (int j = 0; j < Math.min(9, list.size()); j++) {
					settings.put((row - 1) * 9 + j, list.get(i * 9 + j));
				}
			}
		}

		setItem(row * 9, new ItemBuilder(Material.ARROW).name("§cRetour").appendLore("§7Retourner au menu précédent").toItemStack());
		setRows(row + 1);
		addListener(this);
	}

	public void update() {
		settings.forEach((slot, settingVal) -> setItem(slot, settingVal.bakeIcon()));
	}

	@Override
	public boolean onOpen(Player player) {
		update();
		return true;
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		if (slot == (getRows() - 1) * 9) {
			getParent().open(player);
			return;
		}

		if (settings.containsKey(slot)) {
			SettingValue setting = settings.get(slot);
			if (setting.getInventory() == null) {
				setting.toggle();
				setItem(slot, setting.bakeIcon());
			}
			else {
				setting.getInventory().open(player);
			}
		}
	}
}

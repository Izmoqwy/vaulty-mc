/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.composer;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.commands.UHCCommand;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class HostGUI extends VaultyInventory {

	public HostGUI(UHCCommand uhcCommand) {
		super(null, "§e§lLancer une partie", false);

		setInventoryType(InventoryType.HOPPER);
		addListener(new GUIListener() {
			@Override
			public Map<Integer, ItemStack> load(Player player) {
				Map<Integer, ItemStack> itemStackMap = Maps.newHashMap();
				for (int i = 0; i < GameManager.getAvailableGameTypes().size(); i++) {
					GameType gameType = GameManager.getAvailableGameTypes().get(i);
					itemStackMap.put(i, new ItemBuilder(gameType.getIcon())
							.name("§6" + gameType.getName())
							.appendLore("§8Description:", "§7" + gameType.getDescription())
							.addFlags(ItemFlag.HIDE_ATTRIBUTES)
							.toItemStack());
				}
				return itemStackMap;
			}

			@Override
			public void onClick(Player player, ItemStack clickedItem, int slot) {
				if (GameManager.getAvailableGameTypes().size() <= slot)
					return;

				player.closeInventory();

				GameType gameType = GameManager.getAvailableGameTypes().get(slot);
				uhcCommand.createGame(player, gameType);
			}
		});
	}
}

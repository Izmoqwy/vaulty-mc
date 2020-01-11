/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.composer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.commands.UHCCommand;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.scenario.GameTypeDetails;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class HostGUI extends VaultyInventory {

	public HostGUI(UHCCommand uhcCommand) {
		super(null, "§e§lLancer une partie", false);

		setInventoryType(InventoryType.HOPPER);
		addListener(new GUIListener() {
			@Override
			public Map<Integer, ItemStack> load(Player player) {
				Map<Integer, ItemStack> itemStackMap = Maps.newHashMap();
				List<GameTypeDetails> gameTypeDetailsList = Lists.newArrayList(GameManager.getAvailableGameTypes().values());

				for (int i = 0; i < gameTypeDetailsList.size(); i++) {
					GameTypeDetails gameType = gameTypeDetailsList.get(i);
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
				uhcCommand.createGame(player, Lists.newArrayList(GameManager.getAvailableGameTypes().keySet()).get(slot));
			}
		});
	}
}

/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.composer;

import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.gui.inventory.InventoryGUI;
import eu.izmoqwy.uhc.gui.SettingsGUI;
import eu.izmoqwy.uhc.scenario.Scenario;
import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ComposerGUI extends VaultyInventory implements GUIListener {

	private final GameComposer gameComposer;

	private SettingsGUI composerSettings;
	private ScenariosGUI scenariosGUI;
	private InventoryGUI inventoryGUI;

	@Getter
	@Setter
	private Player editingInventory;

	public ComposerGUI(GameComposer gameComposer) {
		super(null, "§e§lConfiguration", true);
		this.gameComposer = gameComposer;
		this.composerSettings = new SettingsGUI(this, "§e§lConfig §8» §a§lParamètres", gameComposer, gameComposer.getGameType().getDefaultComposerClass());
		this.scenariosGUI = new ScenariosGUI(this, "§e§lConfig §8» §a§lScénarios", gameComposer);
		this.inventoryGUI = new InventoryGUI(this, "§e§lConfig §8» §a§lInventaire", gameComposer);

		setItem(0, new ItemBuilder(Material.REDSTONE_COMPARATOR)
				.name("§a§lParamètres").appendLore("§7Modifier les paramètres de la partie")
				.toItemStack());
		setItem(8, new ItemBuilder(Material.ANVIL)
				.name("§a§lInventaire").appendLore("§7Préparer l'inventaire de départ")
				.toItemStack());
		setRows(1);
		addListener(this);
	}

	public void update() {
		ItemBuilder scenariosItem = new ItemBuilder(Material.GOLDEN_APPLE).name("§a§lScénarios");
		List<Scenario> scenarioList = gameComposer.getScenarios();
		if (!scenarioList.isEmpty()) {
			for (int i = 0; i < Math.min(5, scenarioList.size()); i++) {
				Scenario scenario = scenarioList.get(i);
				scenariosItem.appendLore("§8» §e" + scenario.getName());
			}
			if (scenarioList.size() > 3) {
				scenariosItem.appendLore("§a+ " + (scenarioList.size() - 3) + " autres scénarios");
			}
		}
		else {
			scenariosItem.appendLore("§cAucun scénario actif");
		}
		setItem(1, scenariosItem.toItemStack());
	}

	@Override
	public boolean onOpen(Player player) {
		update();
		return true;
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		switch (slot) {
			case 0:
				composerSettings.open(player);
				break;
			case 1:
				scenariosGUI.open(player);
				break;
			case 8:
				if (editingInventory != null) {
					if (editingInventory != player)
						player.sendMessage(VaultyCore.PREFIX + "§cUn autre joueur est entrain de modifier l'inventaire");
					else
						inventoryGUI.open(player);
					return;
				}
				editingInventory = player;

				PlayerUtil.clearInventory(player);
				if (gameComposer.getStartingInventoryContents() != null) {
					player.getInventory().setContents(gameComposer.getStartingInventoryContents());
				}
				if (gameComposer.getStartingInventoryArmorContents() != null) {
					player.getInventory().setArmorContents(gameComposer.getStartingInventoryArmorContents());
				}
				player.setGameMode(GameMode.CREATIVE);
				player.closeInventory();
				player.sendMessage(VaultyCore.PREFIX + "§aVous pouvez désormais modifier l'inventaire de départ. §7(faîtes '/composer edit' pour ré-ouvrir le menu et cliquez sur l'enclume pour le sauvegarder et accéder aux livres");
				break;
		}
	}
}

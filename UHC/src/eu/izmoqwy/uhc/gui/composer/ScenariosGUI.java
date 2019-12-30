/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.gui.composer;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.gui.SettingsGUI;
import eu.izmoqwy.uhc.scenario.Scenario;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ScenariosGUI extends VaultyInventory implements GUIListener {

	private GameComposer gameComposer;
	private Map<Integer, SettingsGUI> configurableScenarios = Maps.newHashMap();

	public ScenariosGUI(VaultyInventory parent, String title, GameComposer gameComposer) {
		super(parent, title, true);
		this.gameComposer = gameComposer;

		int allowedRows = (int) Math.min(Math.ceil(GameManager.getAvailableScenarios().size() / 9d), 5);
		for (int i = 0; i < GameManager.getAvailableScenarios().size(); i++) {
			if (i > (allowedRows + 1) * 9)
				break;
			update(i);

			Scenario scenario = GameManager.getAvailableScenarios().get(i);
			if (scenario instanceof GUIConfigurable) {
				configurableScenarios.put(i, new SettingsGUI(this, "§a§lScénarios §8» §3" + scenario.getName(), (GUIConfigurable) scenario, null));
			}
		}
		setRows(allowedRows + 1);
		setItem(allowedRows * 9, new ItemBuilder(Material.ARROW).name("§cRetour").appendLore("§7Retourner au menu précédent").toItemStack());
		addListener(this);
	}

	private void update(int slot) {
		Scenario scenario = GameManager.getAvailableScenarios().get(slot);
		setItem(slot, new ItemBuilder(scenario.getIcon())
				.name("§3" + scenario.getName())
				.appendLore("§7État: " + (gameComposer.getScenarios().contains(scenario) ? "§aActivé" : "§cDésactivé"), " ", "§8Description:", "§7" + scenario.getDescription())
				.addFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack());
	}

	@Override
	public void onClick_event(Player player, ItemStack clickedItem, int slot, InventoryClickEvent event) {
		if (slot == (getRows() - 1) * 9) {
			getParent().open(player);
			return;
		}

		if (GameManager.getAvailableScenarios().size() <= slot)
			return;

		if (event.getClick() == ClickType.RIGHT) {
			if (configurableScenarios.containsKey(slot) && gameComposer.getScenarios().contains(GameManager.getAvailableScenarios().get(slot)))
				configurableScenarios.get(slot).open(player);
		}
		else if (event.getClick() == ClickType.LEFT) {
			Scenario scenario = GameManager.getAvailableScenarios().get(slot);
			if (!gameComposer.getScenarios().remove(scenario))
				gameComposer.getScenarios().add(scenario);
			update(slot);
		}
	}
}

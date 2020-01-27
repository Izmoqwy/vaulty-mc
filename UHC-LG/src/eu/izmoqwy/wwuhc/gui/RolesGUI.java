/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.gui;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.gui.SettingsGUI;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import eu.izmoqwy.wwuhc.game.WWComposer;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.solo.RoleMurderer;
import eu.izmoqwy.wwuhc.role.solo.RoleThief;
import eu.izmoqwy.wwuhc.role.village.*;
import eu.izmoqwy.wwuhc.role.werewolves.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RolesGUI extends VaultyInventory implements GUIListener {

	private List<Role> availableRoles;

	private WWComposer gameComposer;
	private Map<Integer, SettingsGUI> configurableRoles = Maps.newHashMap();

	public RolesGUI(String title, VaultyInventory parent, WWComposer gameComposer) {
		super(parent, title, true);
		this.availableRoles = Arrays.asList(
				// Les loups
				new RoleWerewolf(),
				new RoleWhiteWW(),
				new RoleVFOW(),
				new RoleGhostWolf(),
				new RoleWolfMother(),
				new RoleCub(),

				// Le village
				new RoleWildChild(),
				new RoleLittleGirl(),
				new RoleWitch(),
				new RoleSeer(),
				new RoleFox(),
				new RoleBearTamer(),
				new RoleAncient(),
				new RoleHunter(),
				new RoleAngel(),
				new RoleSavior(),
				new RoleDevotedServant(),
				new RoleTeacher(),

				new RoleVillager(),

				// Les solos
				new RoleMurderer(),
				new RoleThief()
		);
		this.gameComposer = gameComposer;

		int allowedRows = (int) Math.min(Math.ceil(availableRoles.size() / 9d), 5);
		for (int i = 0; i < availableRoles.size(); i++) {
			if (i > (allowedRows + 1) * 9)
				break;
			update(i);

			Role role = availableRoles.get(i);
			if (role instanceof GUIConfigurable) {
				configurableRoles.put(i, new SettingsGUI(this, "§a§lRôles §8» §3" + role.getName(), (GUIConfigurable) role, null));
			}
		}
		setRows(allowedRows + 1);
		setItem(allowedRows * 9, new ItemBuilder(Material.ARROW).name("§cRetour").appendLore("§7Retourner au menu précédent").toItemStack());
		addListener(this);
	}

	public void clearBackButton() {
		setRows(getRows() - 1);
		setItem(getRows() * 9, null);
	}

	private void update(int slot) {
		Role role = availableRoles.get(slot);
		setItem(slot, new ItemBuilder(role.getIcon())
				.name("§e" + role.getName())
				.appendLore("§7Présent: " + (gameComposer.getRoles().contains(role) ? "§aOui" : "§cNon"))
				.addFlags(ItemFlag.HIDE_ATTRIBUTES)
				.toItemStack());
	}

	@Override
	public void onClick_event(Player player, ItemStack clickedItem, int slot, InventoryClickEvent event) {
		if (slot == (getRows() - 1) * 9) {
			getParent().open(player);
			return;
		}

		if (availableRoles.size() <= slot)
			return;

		if (event.getClick() == ClickType.RIGHT) {
			if (configurableRoles.containsKey(slot) && gameComposer.getRoles().contains(availableRoles.get(slot)))
				configurableRoles.get(slot).open(player);
		}
		else if (event.getClick() == ClickType.LEFT) {
			Role role = availableRoles.get(slot);
			if (!gameComposer.getRoles().remove(role))
				gameComposer.getRoles().add(role);
			update(slot);
		}
	}
}

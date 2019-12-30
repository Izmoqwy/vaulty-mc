/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class RoleWhiteWW extends RoleWerewolf {

	public RoleWhiteWW() {
		super("Loup-Garou Blanc", createDescription(
				"Vous devez trahir les autres loups. Pour ce faire, vous possédez 5 coeurs en plus."
		), new MaterialData(Material.DIAMOND_CHESTPLATE));
	}

	@Override
	public void onRoleReceive(Player player) {
		player.setMaxHealth(30);
	}
}

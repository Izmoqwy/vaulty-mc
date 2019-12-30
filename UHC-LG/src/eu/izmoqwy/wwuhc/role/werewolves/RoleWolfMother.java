/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Map;
import java.util.UUID;

public class RoleWolfMother extends RoleWerewolf {

	public RoleWolfMother() {
		super("Mère Louve", createDescription(
				"Vous disposez d'un louveteau en qui vous avez confiance. Votre louveteau ne connaît pas votre identité mais vous connaissez la sienne. Une fois que vous vous trouvez à moins de 10 blocs de lui, vous obtiendrez l'effet Resistance I. Si votre louveteau venait à mourir, vous perdrez 5 coeurs permanent."
		), new MaterialData(Material.BONE));
	}

	@Override
	public void onRoleReceive(Player player) {
		super.onRoleReceive(player);
		WWGameType werewolf = (WWGameType) GameManager.get.getCurrentComposer().getGameType();
		Map.Entry<UUID, Role> cub = werewolf.getAssignedRole(RoleCub.class);
		if (cub != null)
			player.sendMessage(werewolf.getPrefix() + "§2Votre louveteau est: §c§l" + Bukkit.getOfflinePlayer(cub.getKey()).getName());
		else
			player.sendMessage(werewolf.getPrefix() + "§cVous n'avez pas de louveteau. Vous ne perdez pas 5 coeurs pour ne pas être désavantagé.");
	}
}

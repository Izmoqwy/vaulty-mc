/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.village;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

public class RoleBearTamer extends Role {
	public RoleBearTamer() {
		super("Montreur d'ours", createDescription(
				"Chaque matin, un ours imaginaire va grogner autant de fois qu'il y a de loups-garous à moins de 30 blocs autour de vous. "
		), new MaterialData(Material.MILK_BUCKET), RoleSide.VILLAGE);
	}

	@Override
	public void onDayCycle(Player player, DayCycle current, DayCycle previous) {
		super.onDayCycle(player, current, previous);
		if (current == DayCycle.DAY && previous == DayCycle.NIGHT) {
			WWGameType werewolf = (WWGameType) GameManager.get.getCurrentComposer().getGameType();
			werewolf.getRoleMap().forEach((uuid, role) -> {
				if (role.getRoleSide() == RoleSide.WEREWOLF) {
					Player other = Bukkit.getPlayer(uuid);
					if (other == null)
						return;

					if (other.getLocation().distance(player.getLocation()) <= 50) {
						GameManager.get.getCurrentGame().broadcast("§6Grrrrrrrrrrrrrrrrr");
					}
				}
			});
		}
	}
}

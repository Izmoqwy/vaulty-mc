/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role;

import com.google.common.collect.Maps;
import eu.izmoqwy.wwuhc.role.village.RoleVillager;
import eu.izmoqwy.wwuhc.role.werewolves.RoleWerewolf;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoleGiver {

	public static Map<UUID, Role> attribute(List<Role> roles, List<UUID> players) {
		Collections.shuffle(roles);
		Collections.shuffle(players);

		if (roles.size() > players.size())
			Bukkit.shutdown();

		Map<UUID, Role> appliedRoles = Maps.newHashMap();
		for (Role role : roles) {
			if (role.getClass() == RoleWerewolf.class) {
				for (int i = 0; i < ((RoleWerewolf) role).getAmount(); i++) {
					appliedRoles.put(players.remove(0), new RoleWerewolf());
				}
			}
			else {
				appliedRoles.put(players.remove(0), role);
			}
		}

		List<Role> matchingRole = roles.stream().filter(role -> role.isApplicable(RoleVillager.class)).collect(Collectors.toList());
		final RoleVillager roleVillager = matchingRole.isEmpty() ? new RoleVillager() : (RoleVillager) matchingRole.get(0);
		players.stream().filter(player -> !appliedRoles.containsKey(player)).forEach(player -> appliedRoles.put(player, roleVillager));

		return appliedRoles;
	}

}

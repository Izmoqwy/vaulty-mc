/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role;

import com.google.common.collect.Maps;

import java.util.*;
import java.util.stream.Collectors;

public class RoleGiver {

	public static Map<UUID, Role> attribute(List<Role> roles, List<UUID> players) {
		Collections.shuffle(roles);
		Collections.shuffle(players);
		Random random = new Random();

		Map<UUID, Role> appliedRoles = Maps.newHashMap();
		while (players.size() > 0) {
			int villagers = count(appliedRoles, RoleSide.VILLAGE), werewolves = count(appliedRoles, RoleSide.WEREWOLF);
			Role role;

			Role soloRole = findRole(roles, RoleSide.SOLO, appliedRoles.values());
			if (soloRole != null)
				role = soloRole;
			else if (villagers < werewolves)
				role = findRole(roles, RoleSide.VILLAGE, appliedRoles.values());
			else if (werewolves < villagers)
				role = findRole(roles, RoleSide.WEREWOLF, appliedRoles.values());
			else {
				role = findRole(roles, random.nextBoolean() ? RoleSide.WEREWOLF : RoleSide.VILLAGE, appliedRoles.values());
			}

			if (role == null)
				role = findRole(roles, null, appliedRoles.values());

			appliedRoles.put(players.remove(0), role);
		}
		return appliedRoles;
	}

	private static Role findRole(List<Role> roles, RoleSide roleSide, Collection<Role> taken) {
		List<Role> remainingRoles = roles.stream().filter(role -> role.isNeeded() && !taken.contains(role) && (roleSide == null || roleSide == role.getRoleSide())).collect(Collectors.toList());
		if (!remainingRoles.isEmpty()) {
			return remainingRoles.get(0);
		}

		List<Role> applicableRoles = roles.stream().filter(role -> role.isMultiple() && (roleSide == null || roleSide == role.getRoleSide())).collect(Collectors.toList());
		if (!applicableRoles.isEmpty()) {
			Collections.shuffle(applicableRoles);
			return applicableRoles.get(0);
		}
		return null;
	}

	private static int count(Map<UUID, Role> appliedRoles, RoleSide roleSide) {
		return (int) appliedRoles.entrySet().stream().filter(entry -> entry.getValue().getRoleSide() == roleSide).count();
	}

}

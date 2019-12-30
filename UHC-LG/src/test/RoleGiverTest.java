/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.wwuhc.role.RoleSide;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

public class RoleGiverTest {

	public static void main(String[] args) {
		List<RoleTest> roles = Arrays.asList(
				new RoleTest("Vile Father Of Wolves", RoleSide.WEREWOLF, false, true),
				new RoleTest("Little Girl", RoleSide.VILLAGE, false, true),

				new RoleTest("Villager", RoleSide.VILLAGE, true, false),
				new RoleTest("Werewolf", RoleSide.WEREWOLF, true, false)

		);
		List<String> players = Lists.newArrayList("Izmoqwy", "TreyZ", "Flichos", "Hispaa", "Xinehp", "SkynoxMC", "Poupsit");

		for (int i = 0; i < 10; i++) {
			System.out.println("Gen #" + i);

			List<String> playersCopy = Lists.newArrayList(players);
			Collections.shuffle(roles);
			Collections.shuffle(playersCopy);
			Random random = new Random();

			Map<String, RoleTest> appliedRoles = Maps.newHashMap();

			while (playersCopy.size() > 0) {
				int villagers = count(appliedRoles, RoleSide.VILLAGE), werewolves = count(appliedRoles, RoleSide.WEREWOLF);
				RoleTest role;

				if (villagers < werewolves)
					role = findRole(roles, RoleSide.VILLAGE, appliedRoles.values());
				else if (werewolves < villagers)
					role = findRole(roles, RoleSide.WEREWOLF, appliedRoles.values());
				else {
					RoleTest soloRole = findRole(roles, RoleSide.SOLO, appliedRoles.values());
					role = soloRole != null ? soloRole : findRole(roles, random.nextBoolean() ? RoleSide.WEREWOLF : RoleSide.VILLAGE, appliedRoles.values());
				}

				if (role == null)
					role = findRole(roles, null, appliedRoles.values());

				appliedRoles.put(playersCopy.remove(0), role);
			}

			System.out.println("  " + count(appliedRoles, RoleSide.VILLAGE) + " / " + count(appliedRoles, RoleSide.WEREWOLF) + " / " + count(appliedRoles, RoleSide.SOLO));
			System.out.println("  " + appliedRoles.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue().getName()).collect(Collectors.joining(" | ")));
		}
	}

	private static RoleTest findRole(List<RoleTest> roles, RoleSide roleSide, Collection<RoleTest> taken) {
		List<RoleTest> remainingRoles = roles.stream().filter(role -> role.isNeeded() && !taken.contains(role) && (roleSide == null || roleSide == role.getRoleSide())).collect(Collectors.toList());
		if (!remainingRoles.isEmpty()) {
			return remainingRoles.get(0);
		}

		List<RoleTest> applicableRoles = roles.stream().filter(role -> role.isMultiple() && (roleSide == null || roleSide == role.getRoleSide())).collect(Collectors.toList());
		if (!applicableRoles.isEmpty()) {
			Collections.shuffle(applicableRoles);
			return applicableRoles.get(0);
		}
		return null;
	}

	private static int count(Map<String, RoleTest> appliedRoles, RoleSide roleSide) {
		return (int) appliedRoles.entrySet().stream().filter(entry -> entry.getValue().getRoleSide() == roleSide).count();
	}

	@Getter
	private static class RoleTest {
		private String name;
		private RoleSide roleSide;
		private boolean multiple, needed;

		public RoleTest(String name, RoleSide roleSide, boolean multiple, boolean needed) {
			this.name = name;
			this.roleSide = roleSide;
			this.multiple = multiple;
			this.needed = needed;
		}
	}


}

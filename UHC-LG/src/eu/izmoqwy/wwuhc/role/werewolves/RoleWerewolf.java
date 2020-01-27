/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

@Getter
public class RoleWerewolf extends Role implements GUIConfigurable {

	@GUISetting(name = "Nombre", icon = Material.QUARTZ_ORE)
	private int amount = 1;

	public RoleWerewolf() {
		super("Loup-Garou", createDescription(), new MaterialData(Material.IRON_SWORD), RoleSide.WEREWOLF, true);
		init();
	}

	public RoleWerewolf(String name, List<String> description, MaterialData icon) {
		super(name, description, icon, RoleSide.WEREWOLF, false);
		init();
	}

	protected static List<String> createDescription(String... lines) {
		List<String> description = Lists.newArrayList();
		description.add("Vous disposez de l'effet Night Vision et Strength I durant la nuit. Vous gagnerez deux coeurs d'absorptions et l'effet Speed I pendant une minute lors d'un meurtre.");
		description.addAll(Arrays.asList(lines));
		return description;
	}

	private void init() {
		this.nightlyEffects = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE, PotionEffectType.NIGHT_VISION};
	}

}

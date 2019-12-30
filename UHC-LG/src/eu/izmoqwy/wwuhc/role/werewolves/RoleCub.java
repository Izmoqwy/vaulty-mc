/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.role.werewolves;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class RoleCub extends RoleWerewolf {

	public RoleCub() {
		super("Louveteau", createDescription(
				"Vous disposez d'une mère louve en qui vous avez confiance. Vous ne connaissez pas son identité mais elle connaît la votre. Une fois que vous vous trouvez à moins de 10 blocs d'elle, vous obtiendrez l'effet Speed I. Si votre mère louve venait à mourir, vous perdrez 5 coeurs permanent"
		), new MaterialData(Material.MONSTER_EGG));
	}
}

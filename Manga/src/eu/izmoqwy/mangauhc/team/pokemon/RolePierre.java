package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.potion.PotionEffectType;

public class RolePierre extends MangaRole {

	public RolePierre() {
		super("Pierre", createDescription(
				"Vous êtes Pierre, vous devez gagner avec votre équipe actuelle, " +
						"pour ce faire vous obtenez l'effet Résistance I ainsi le pouvoir de ne pas prendre de dégâts de chute."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
	}

}

package eu.izmoqwy.mangauhc.team.titans;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.potion.PotionEffectType;

public class RoleMikasa extends MangaRole {

	public RoleMikasa() {
		super("Mikasa", createDescription(
			"Vous êtes Mikasa vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez des effets Résistance I et Speed I. "
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED, PotionEffectType.DAMAGE_RESISTANCE};
	}

}

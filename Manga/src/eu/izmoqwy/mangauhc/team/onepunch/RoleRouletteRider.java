package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.potion.PotionEffectType;

public class RoleRouletteRider extends MangaRole {

	public RoleRouletteRider() {
		super("Roulette Rider", createDescription(
			"Vous êtes Roulette Rider, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez Speed I, Résistance I mais aussi Weakness I."
		));
		this.effectTypes = new PotionEffectType[]{
				PotionEffectType.SPEED,
				PotionEffectType.DAMAGE_RESISTANCE,
				PotionEffectType.WEAKNESS
		};
	}

}

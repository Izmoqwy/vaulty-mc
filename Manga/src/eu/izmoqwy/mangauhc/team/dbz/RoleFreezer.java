package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.potion.PotionEffectType;

public class RoleFreezer extends MangaRole {

	public RoleFreezer() {
		super("Freezer", createDescription(
				"Vous êtes Freezer vous devez gagner avec votre nouvelle équipe, pour ce faire vous " +
						"disposez de l'effet Force I cependant si vous arrivez à tuer Goku vous obtenez Force II."
		), true);
		this.effectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
	}

}

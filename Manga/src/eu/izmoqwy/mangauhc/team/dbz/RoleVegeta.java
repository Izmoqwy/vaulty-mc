package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.potion.PotionEffectType;

public class RoleVegeta extends MangaRole {

	public RoleVegeta() {
		super("Vegeta", createDescription(
				"Vous êtes Vegeta vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez de la commande ‘/mg spv’.",
				"Vous ne pourrez plus bouger pendant 10 secondes ensuite vous vous transformerez en Super Saiyan qui vous donnes Speed I ainsi que Résistance I."
		));
		this.transformEffectTypes = new PotionEffectType[]{PotionEffectType.SPEED, PotionEffectType.DAMAGE_RESISTANCE};
	}

}

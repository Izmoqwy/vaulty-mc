package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RoleGoku extends MangaRole {

	public RoleGoku() {
		super("Goku", createDescription(
				"Vous êtes Goku vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez de la commande ‘/mg spg’.",
				"Vous ne pourrez plus bouger pendant 10 secondes ensuite vous vous transformerez en Super Savant qui vous donne Force I, Speed I ainsi que 2 coeurs en plus."
		));
		this.transformEffectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE, PotionEffectType.SPEED};
	}

	@Override
	public void onTransform(Player player) {
		super.onTransform(player);
		player.setMaxHealth(player.getMaxHealth() + 4);
	}

}

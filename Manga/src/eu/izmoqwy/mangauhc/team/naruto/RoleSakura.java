package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RoleSakura extends MangaRole {

	public RoleSakura() {
		super("Sakura", createDescription(
				"Vous êtes Sakura, vous devez gagner avec votre équipe actuelle, pour ce faire vous avez 2 coeurs en plus ainsi que Force I."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
	}

	@Override
	public void onRoleGive(Player player) {
		super.onRoleGive(player);
		player.setMaxHealth(player.getMaxHealth() + 4);
	}

}

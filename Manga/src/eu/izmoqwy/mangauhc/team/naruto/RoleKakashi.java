package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleKakashi extends MangaRole {

	public RoleKakashi() {
		super("Kakashi", createDescription(
				"Vous êtes Kakashi, vous devez gagner avec votre équipe actuelle, pour ce faire vous avez Speed I ainsi que 3 enderpearls."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED};
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.ENDER_PEARL, 3)
		};
	}

}

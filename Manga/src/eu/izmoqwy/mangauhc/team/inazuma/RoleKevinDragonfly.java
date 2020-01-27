package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleKevinDragonfly extends MangaRole {

	public RoleKevinDragonfly() {
		super("Kevin Dragonfly", createDescription(
				"Vous êtes Kevin Dragonfly, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez l'effet Force I ainsi qu'un livre Sharpness III."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3)
						.toItemStack()
		};
	}

}

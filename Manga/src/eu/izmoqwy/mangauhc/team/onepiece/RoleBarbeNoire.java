package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleBarbeNoire extends MangaRole {

	public RoleBarbeNoire() {
		super("Barbe Noire", createDescription(
				"Vous êtes Barbe Noire vous devez gagner avec votre nouvelle équipe pour ce faire vous disposez de l'effet Force I, " +
						"d'un livre Sharpness III ainsi qu’un livre Protection III."
		), true);
		this.effectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3)
						.toItemStack(),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
						.toItemStack()
		};
	}

}

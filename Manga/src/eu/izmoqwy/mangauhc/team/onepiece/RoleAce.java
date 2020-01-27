package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleAce extends MangaRole {

	public RoleAce() {
		super("Ace", createDescription(
				"Vous êtes Ace vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Fire Aspect I, vous ne prenez aucun dégâts de feux."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.FIRE_RESISTANCE};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.FIRE_ASPECT, 1)
						.toItemStack()
		};
	}

}

package eu.izmoqwy.mangauhc.team.fairy;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleErza extends MangaRole {

	public RoleErza() {
		super("Erza", createDescription(
				"Vous êtes Erza vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Protection III, Sharpness III ainsi que l’effet Résistance I."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3).addBookEnchant(Enchantment.DAMAGE_ALL, 3).toItemStack()
		};
	}

}

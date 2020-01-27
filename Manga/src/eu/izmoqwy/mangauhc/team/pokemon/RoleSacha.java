package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleSacha extends MangaRole {

	public RoleSacha() {
		super("Sacha", createDescription(
				"Vous êtes Sacha, vous devez gagner avec votre équipe actuelle, pour ce faire vous avez l'effet Speed I ainsi qu'un livre Sharpness III ou Protection III. " +
						"Enfin vous disposez un livre Power 2."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3)
						.addBookEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
						.toItemStack(),
				new ItemBuilder(Enchantment.ARROW_DAMAGE, 2)
						.toItemStack()
		};
	}

}

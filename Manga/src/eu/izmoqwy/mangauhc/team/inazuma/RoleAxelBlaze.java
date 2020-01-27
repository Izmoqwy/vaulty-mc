package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleAxelBlaze extends MangaRole {

	public RoleAxelBlaze() {
		super("Axel Blaze", createDescription(
				"Vous êtes Axel Blaze, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez un livre Fire Aspect I, Flame I."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.ARROW_FIRE, 1).
						addBookEnchant(Enchantment.FIRE_ASPECT, 1)
						.toItemStack()
		};
	}

}

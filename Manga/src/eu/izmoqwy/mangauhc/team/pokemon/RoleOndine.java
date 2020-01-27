package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleOndine extends MangaRole {

	public RoleOndine() {
		super("Ondine", createDescription(
				"Vous êtes Ondine, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez un livre Depth Strider III ainsi qu’un livre Fire Protection IV."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DEPTH_STRIDER, 3)
						.toItemStack(),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
						.toItemStack()
		};
	}

}

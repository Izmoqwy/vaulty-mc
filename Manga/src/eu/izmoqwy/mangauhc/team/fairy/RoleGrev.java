package eu.izmoqwy.mangauhc.team.fairy;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleGrev extends MangaRole {

	public RoleGrev() {
		super("Grev", createDescription(
			"Vous êtes Grev vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Depth Strider III , ainsi que Résistance I lorsque vous êtes en contact avec l’eau."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DEPTH_STRIDER, 3).toItemStack()
		};
	}

}

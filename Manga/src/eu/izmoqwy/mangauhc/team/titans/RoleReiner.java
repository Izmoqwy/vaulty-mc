package eu.izmoqwy.mangauhc.team.titans;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleReiner extends MangaRole {

	public RoleReiner() {
		super("Reiner", createDescription(
			"Vous êtes Reiner vous devez gagner avec votre nouvelle équipe pour ce faire vous disposez d’un livre Sharpness III, un livre Protection III et de 5 livres."
		), true);
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).toItemStack(),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack(),
				new ItemStack(Material.BOOK, 5)
		};
	}

}

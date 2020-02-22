package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleGenos extends MangaRole {

	public RoleGenos() {
		super("Genos", createDescription(
				"Vous êtes Genos, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez 3 TNTs, 1 potion de régénération, 2 potions de soin ainsi qu'un livre Protection III."
		));
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.TNT, 3),
				new ItemStack(Material.POTION, 1, (short) 16460),
				new ItemStack(Material.POTION, 2, (short) 16453),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack()
		};
	}

}

package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleGohan extends MangaRole {

	public RoleGohan() {
		super("Gohan", createDescription(
				"Vous êtes Gohan vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d'un livre Sharpness III " +
						"ainsi que 2 enderpearls cependant si Goku meurt vous obtiendrez Force I."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).toItemStack(),
				new ItemStack(Material.ENDER_PEARL, 3)
		};
	}

}

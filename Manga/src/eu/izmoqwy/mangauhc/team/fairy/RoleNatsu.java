package eu.izmoqwy.mangauhc.team.fairy;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class RoleNatsu extends MangaRole {

	public RoleNatsu() {
		super("Natsu", createDescription(
				"Vous êtes Natsu vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Fire Aspect I et de Force I lorsque vous êtes en contact avec le feu."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.FIRE_ASPECT, 1).toItemStack()
		};
	}

}

package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@Getter
public class RoleNami extends MangaRole {

	private ItemStack locatorItem;
	@Setter
	private int remaining = 2;

	public RoleNami() {
		super("Nami", createDescription(
				"Vous êtes Nami vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Depth Strider II " +
						"ainsi qu’une boussole qui vous permet de localiser un ennemi aléatoirement, utilisable deux fois dans la partie."
		));
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DEPTH_STRIDER, 2)
						.toItemStack(),
				locatorItem = new ItemBuilder(Material.EMPTY_MAP)
						.name("§5Localisateur extrême")
						.toItemStack()
		};
	}

}

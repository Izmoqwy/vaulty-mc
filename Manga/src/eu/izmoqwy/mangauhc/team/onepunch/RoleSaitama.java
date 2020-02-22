package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class RoleSaitama extends MangaRole {

	private ItemStack punchItem;
	@Setter
	private int remaining = 3;

	public RoleSaitama() {
		super("Saitama", createDescription(
				"Vous êtes Saitama, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez item nommé \"poing\" qui enlève la moitié de la vie d'un joueur " +
						"quand vous lui cliquez dessus, utilisable 3 fois."
		));
		this.startingContent = new ItemStack[]{
				punchItem = new ItemBuilder(Material.BRICK)
						.name("§5Poing")
						.toItemStack()
		};
	}

}

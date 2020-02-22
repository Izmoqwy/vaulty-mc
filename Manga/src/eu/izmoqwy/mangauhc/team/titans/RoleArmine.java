package eu.izmoqwy.mangauhc.team.titans;

import eu.izmoqwy.mangauhc.team.MangaRole;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class RoleArmine extends MangaRole {

	@Setter
	private int remaining = 2;

	public RoleArmine() {
		super("Armine", createDescription(
			"Vous êtes Armine vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez de 5 livres ainsi que la commande ‘/mg effect’, qui permet la " +
					"distributions d’un effet aléatoire à l’un de vos équipiers pour une durée de 5 minutes, vous pouvez l’utilisez deux fois dans la partie."
		));
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.BOOK, 5)
		};
	}

}

package eu.izmoqwy.mangauhc.team.fairy;

import eu.izmoqwy.mangauhc.team.MangaRole;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class RoleZelephNoire extends MangaRole {

	@Setter
	private boolean usedInvisibility;

	public RoleZelephNoire() {
		super("Zeleph Noire", createDescription(
				"Vous êtes Zeleph Noire vous devez gagner avec votre nouvelle équipe pour ce faire vous disposez de 5 bibliothèques et de la commande ‘/mg inv’ " +
						"qui vous permet de devenir invisible pendant 5 minutes, une seule fois.",
				"Si vous parvenez à tuer Natsu, vous obtiendrez un effet aléatoire entre Speed I, Jump Boost I, Force I ou Résistance I."
		), true);
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.BOOKSHELF, 5)
		};
	}

}

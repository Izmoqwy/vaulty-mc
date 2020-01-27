package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleJiraya extends MangaRole {

	public RoleJiraya() {
		super("jiraya", createDescription(
				"Vous êtes Jiraya, vous devez gagner avec votre équipe actuelle, pour ce faire vous avez Résistance I ainsi que 5 livres."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.BOOK, 5)
		};
	}

}

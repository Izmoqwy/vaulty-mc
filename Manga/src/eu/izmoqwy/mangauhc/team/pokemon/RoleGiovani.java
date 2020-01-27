package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.team.MangaRole;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleGiovani extends MangaRole {

	public RoleGiovani() {
		super("Giovani", createDescription(
				"Vous êtes Giovani, vous devez gagner avec votre nouvelle équipe, pour ce faire vous obtenez l'effet Force I ainsi que 3 potions de soin."
		), true);
		this.effectTypes = new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE};
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.POTION, 3, (short) 16453)
		};
	}

}

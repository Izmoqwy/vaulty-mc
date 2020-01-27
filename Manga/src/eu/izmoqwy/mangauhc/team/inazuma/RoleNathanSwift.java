package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleNathanSwift extends MangaRole {

	public RoleNathanSwift() {
		super("Nathan Swift", createDescription(
				"Vous êtes Nathan Swift, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez l'effet Speed I ainsi qu'un livre Depth strider II."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DEPTH_STRIDER, 2)
						.toItemStack()
		};
	}

}

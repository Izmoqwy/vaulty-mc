package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleMarkEvans extends MangaRole {

	public RoleMarkEvans() {
		super("Mark Evans", createDescription(
				"Vous êtes Mark Evans, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez l'effet Résistance I ainsi qu'un livre Protection III."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
						.toItemStack()
		};
	}

}

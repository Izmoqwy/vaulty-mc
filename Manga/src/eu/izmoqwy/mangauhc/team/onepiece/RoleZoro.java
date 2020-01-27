package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleZoro extends MangaRole {

	public RoleZoro() {
		super("Zoro", createDescription(
				"Vous êtes Zoro vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un livre Sharpness III et de 3 épées en fer."
		));
		this.effectTypes = new PotionEffectType[]{};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3)
						.toItemStack(),
				new ItemStack(Material.IRON_SWORD, 3)
		};
	}

}

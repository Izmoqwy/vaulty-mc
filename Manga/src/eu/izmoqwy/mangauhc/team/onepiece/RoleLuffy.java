package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleLuffy extends MangaRole {

	public RoleLuffy() {
		super("Luffy", createDescription(
				"Vous êtes Luffy vous devez gagner avec votre équipe actuelle, pour ce faire vous disposez d’un arc Power III et de l’effet Speed I."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Material.BOW)
						.addEnchant(Enchantment.ARROW_DAMAGE, 3, false)
						.toItemStack()
		};
	}

}

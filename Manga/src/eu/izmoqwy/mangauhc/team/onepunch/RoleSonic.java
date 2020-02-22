package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleSonic extends MangaRole {

	public RoleSonic() {
		super("Sonic", createDescription(
				"Vous êtes Sonic, vous devez gagner avec votre nouvelle équipe, pour ce faire vous obtenez l'effet Speed I, 3 enderpearls ainsi qu'un livre Sharpness III."
		), true);
		this.effectTypes = new PotionEffectType[]{PotionEffectType.SPEED};
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.ENDER_PEARL, 3),
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).toItemStack()
		};
	}

}

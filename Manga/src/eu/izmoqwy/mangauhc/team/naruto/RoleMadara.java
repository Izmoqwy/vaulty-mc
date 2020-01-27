package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RoleMadara extends MangaRole {

	public RoleMadara() {
		super("Madara", createDescription(
				"Vous êtes Madara vous devez gagner avec votre nouvelle équipe, pour ce faire vous avez un livre Sharpness III " +
						"et qu'un livre Protection III ainsi que 2 coeur en" + " plus."
		), true);
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).toItemStack(),
				new ItemBuilder(Enchantment.PROTECTION_ENVIRONMENTAL, 3).toItemStack()
		};
	}

	@Override
	public void onRoleGive(Player player) {
		super.onRoleGive(player);
		player.setMaxHealth(player.getMaxHealth() + 2);
	}

}

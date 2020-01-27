package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class RoleJudeSharpe extends MangaRole {

	public RoleJudeSharpe() {
		super("Jude Sharpe", createDescription(
				"Vous êtes Jude Sharpe, vous devez gagner avec votre nouvelle équipe, pour ce faire vous obtenez l'effet Night Vision ainsi que 5 coeur en plus.",
				"Enfin vous disposez d'un livre Sharpness III, Protection III."
		), true);
		this.effectTypes = new PotionEffectType[]{PotionEffectType.NIGHT_VISION};
		this.startingContent = new ItemStack[]{
				new ItemBuilder(Enchantment.DAMAGE_ALL, 3).
						addBookEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
						.toItemStack()
		};
	}

	@Override
	public void onRoleGive(Player player) {
		super.onRoleGive(player);
		player.setMaxHealth(player.getMaxHealth() + 10);
	}

}

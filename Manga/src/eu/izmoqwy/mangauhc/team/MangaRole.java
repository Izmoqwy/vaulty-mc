package eu.izmoqwy.mangauhc.team;

import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

@Getter
@EqualsAndHashCode
public class MangaRole {

	@Setter
	private MangaTeam parent;
	private final String name;
	private final String[] description;
	private final boolean wicked;

	@Setter
	private boolean frozen, transformed;

	protected ItemStack[] startingContent;
	protected PotionEffectType[] effectTypes, transformEffectTypes;

	public MangaRole(String name, String[] description) {
		this(name, description, false);
	}

	public MangaRole(String name, String[] description, boolean wicked) {
		this.name = name;
		this.description = description;
		this.wicked = wicked;
	}

	protected static String[] createDescription(String... lines) {
		return lines;
	}

	public void onRoleGive(Player player) {
		if (effectTypes != null) {
			for (PotionEffectType effectType : effectTypes) {
				PlayerUtil.giveEffect(player, effectType, (short) 0, (short) 20_000, true);
			}
		}
	}

	public void onTransform(Player player) {
		if (transformEffectTypes != null) {
			for (PotionEffectType effectType : transformEffectTypes) {
				PlayerUtil.giveEffect(player, effectType, (short) 0, (short) 20_000, true);
			}
		}
		this.transformed = true;
	}

	public boolean isApplicable(Class<? extends MangaRole> aClass) {
		return getClass().equals(aClass);
	}

}

package eu.izmoqwy.uhc.game;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@Getter
public enum PreMadeTeam {

	RED("Rouge", ChatColor.RED, DyeColor.RED), BLUE("Bleue", ChatColor.BLUE, DyeColor.BLUE), GREEN("Verte", ChatColor.GREEN, DyeColor.GREEN),
	YELLOW("Jaune", ChatColor.YELLOW, DyeColor.YELLOW), AQUA("Bleue claire", ChatColor.AQUA, DyeColor.LIGHT_BLUE), ORANGE("Orange", ChatColor.GOLD, DyeColor.ORANGE),
	PURPLE("Violette", ChatColor.DARK_PURPLE, DyeColor.PURPLE), PINK("Rose", ChatColor.LIGHT_PURPLE, DyeColor.PINK), GRAY("Grise", ChatColor.GRAY, DyeColor.GRAY),
	DARK_GREEN("Verte foncée", ChatColor.DARK_GREEN, DyeColor.GREEN), CYAN("Cyan", ChatColor.DARK_AQUA, DyeColor.CYAN),
	DARK_GRAY("Grise foncée", ChatColor.DARK_GRAY, DyeColor.BLACK);

	private String name;
	private ChatColor color;
	private DyeColor dyeColor;

	PreMadeTeam(String name, ChatColor color, DyeColor dyeColor) {
		this.name = name;
		this.color = color;
		this.dyeColor = dyeColor;
	}

	public static PreMadeTeam next(PreMadeTeam previous) {
		if (previous == null)
			return RED;

		int index = previous.ordinal() + 1;
		if (index >= values().length)
			return null;

		return values()[index];
	}

}

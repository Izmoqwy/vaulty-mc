package eu.izmoqwy.mangauhc;

import eu.izmoqwy.mangauhc.commands.MangaCommand;
import eu.izmoqwy.mangauhc.game.MangaGameType;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MangaUHC extends JavaPlugin {

	public static final String PREFIX = "§6§lMG §f§l‖ ";

	@Getter
	private static MangaUHC instance;

	@Override
	public void onEnable() {
		instance = this;
		GameManager.registerGameType(MangaGameType.class, new MangaGameType());

		ServerUtil.registerCommands(new MangaCommand());
	}

	@Override
	public void onDisable() {
		GameManager.unregisterGameType(MangaGameType.class);
	}

}

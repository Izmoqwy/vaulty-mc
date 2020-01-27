package eu.izmoqwy.moleuhc;

import eu.izmoqwy.moleuhc.game.MoleGameType;
import eu.izmoqwy.uhc.game.GameManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class MoleUHC extends JavaPlugin {

	@Getter
	private static MoleUHC instance;

	@Override
	public void onEnable() {
		instance = this;
		GameManager.registerGameType(MoleGameType.class, new MoleGameType());
	}

	@Override
	public void onDisable() {
		GameManager.unregisterGameType(MoleGameType.class);
	}

}

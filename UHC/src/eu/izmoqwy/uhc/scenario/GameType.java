/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario;

import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.UHCGame;
import eu.izmoqwy.uhc.game.WaitingRoom;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

@Getter
public abstract class GameType extends Scenario {

	protected GameComposer defaultComposer;
	private final Class<? extends GameComposer> defaultComposerClass;

	public GameType(String name, String description, Material icon, GameComposer defaultComposer, Class<? extends GameComposer> defaultComposerClass) {
		super(name, description, icon);
		this.defaultComposer = defaultComposer;
		this.defaultComposerClass = defaultComposerClass;
	}

	public abstract void startGame(UHCGame game);

	public abstract void stopGame();

	public abstract void checkForWin();

	public void onGameCreate(WaitingRoom waitingRoom) {
	}

	public String getPrefix() {
		return VaultyUHC.PREFIX;
	}

	public static boolean isOnline(OfflinePlayer player) {
		return player.isOnline()
				&& GameManager.get.getCurrentGame() != null && GameManager.get.getCurrentGame().getOnlinePlayers().contains(player.getPlayer())
				&& VaultyUHC.getUhcServer().getOnlinePlayers().contains(player.getPlayer());
	}

}

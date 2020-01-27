package eu.izmoqwy.moleuhc.game;

import eu.izmoqwy.uhc.game.obj.UHCGame;
import eu.izmoqwy.uhc.scenario.GameType;
import org.bukkit.Material;

public class MoleGameType extends GameType {

	public MoleGameType() {
		super("Taupe Gun", "Sortez l'artillerie lourde", Material.EXPLOSIVE_MINECART, MoleComposer.defaultComposer, MoleComposer.class);
	}

	@Override
	public void startGame(UHCGame game) {

	}

	@Override
	public void stopGame() {

	}

	@Override
	public void checkForWin() {

	}

}

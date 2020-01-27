/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.obj.UHCGame;
import lombok.Getter;

public abstract class UHCEvent {

	@Getter
	private UHCGame game;

	public UHCEvent() {
		this.game = GameManager.get.getCurrentGame();
	}
}

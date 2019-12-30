/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario;

import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.WaitingRoom;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

@Getter
public abstract class Scenario {

	private final String name;
	private final String description;
	private final Material icon;

	public Scenario(String name, String description, Material icon) {
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	public List<String> checkComposer(GameComposer gameComposer, WaitingRoom waitingRoom) {
		return null;
	}

	public void onStartGame(GameManager gameManager) {}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Scenario)) return false;
		Scenario scenario = (Scenario) o;
		return Objects.equals(name, scenario.name) &&
				Objects.equals(description, scenario.description) &&
				icon == scenario.icon;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, description, icon);
	}
}

/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.tasks;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.game.WWComposer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomDayCycle extends BukkitRunnable {

	private WWGameType gameType;
	private World world;
	private double cycleMultiplier;

	private long tickIndex = 0;
	@Getter
	private DayCycle dayCycle;

	public CustomDayCycle(WWGameType gameType, World world, int cycleLength) {
		this.gameType = gameType;
		this.world = world;
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setTime(0);
		gameType.onDayCycle(dayCycle = DayCycle.DAY);
		this.cycleMultiplier = 10d / (cycleLength / 60d);
	}

	@Override
	public void run() {
		long time = world.getTime();
		DayCycle newDayCycle;
		if (time <= 12000) {
			newDayCycle = DayCycle.DAY;
		}
		else {
			newDayCycle = DayCycle.NIGHT;
		}
		if (newDayCycle != dayCycle) {
			dayCycle = newDayCycle;
			gameType.onDayCycle(dayCycle);
		}

		double tickDelay = 1d / cycleMultiplier;
		if (cycleMultiplier < 1d) {
			if (tickIndex >= tickDelay) {
				tickIndex = 0;
				world.setTime(time + 1);
			}
		}
		else if (cycleMultiplier > 1d) {
			world.setTime((long) (time + cycleMultiplier));
		}
		else if (cycleMultiplier == 1d) {
			world.setTime(time + 1);
		}
		this.tickIndex++;
	}
}

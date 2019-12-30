/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.world;

import eu.izmoqwy.uhc.VaultyUHC;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

public class UHCWorldManager {

	@Getter
	private static World uhcWorld;

	public static final UHCWorldManager get = new UHCWorldManager();

	@Getter
	private UHCWorldPreGenerator preGenerator;

	private UHCWorldManager() {
		this.preGenerator = new UHCWorldPreGenerator(new File(VaultyUHC.getInstance().getDataFolder(), "worlds"));
	}

	public boolean reset() {
		World world = null;
		try {
			world = preGenerator.getPreloadedWorld("UHC");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		if (world == null)
			return false;

		world.setGameRuleValue("doDayCycle", "false");
		world.setTime(6000);
		prepareWorld(world);
		uhcWorld = world;
		return true;
	}

	public void prepareWorld(World world) {
		final int radius = 16;
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				world.getBlockAt(x, 230, y).setType(Material.BARRIER);
				world.getBlockAt(x, 234, y).setType(Material.BARRIER);

				if (Math.abs(x) == radius || Math.abs(y) == radius) {
					world.getBlockAt(x, 231, y).setType(Material.BARRIER);
					world.getBlockAt(x, 232, y).setType(Material.BARRIER);
					world.getBlockAt(x, 233, y).setType(Material.BARRIER);
				}
			}
		}
	}

}

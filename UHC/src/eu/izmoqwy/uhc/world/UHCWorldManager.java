/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.world;

import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

import java.io.File;
import java.io.IOException;

public class UHCWorldManager implements Listener {

	@Getter
	private static World uhcWorld;

	public static final UHCWorldManager get = new UHCWorldManager();

	@Getter
	private UHCWorldPreGenerator preGenerator;

	private UHCWorldManager() {
		this.preGenerator = new UHCWorldPreGenerator(new File(VaultyUHC.getInstance().getDataFolder(), "worlds"));
		ServerUtil.registerListeners(VaultyUHC.getInstance(), this);
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.isNewChunk() && (event.getWorld().getName().startsWith("temp_world"))) {
			final int chunkX = event.getChunk().getX(), chunkZ = event.getChunk().getZ();
			if (chunkX >= -6 && chunkX <= 6 && chunkZ >= -6 && chunkZ <= 6) {
				for (int x = 0; x < 16; x++) {
					for (int z = 0; z < 16; z++) {
						event.getChunk().getBlock(x, event.getChunk().getWorld().getHighestBlockYAt(x, z), z).setBiome(Biome.ROOFED_FOREST);
					}
				}
			}
		}
	}

}

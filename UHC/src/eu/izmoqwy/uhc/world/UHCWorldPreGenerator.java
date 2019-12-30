/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.world;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.vaulty.nms.NMS;
import eu.izmoqwy.vaulty.utils.MathUtil;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import eu.izmoqwy.vaulty.utils.TimeUtil;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.spigotmc.AsyncCatcher;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UHCWorldPreGenerator {

	private File folder;
	private List<String> worlds = Lists.newArrayList();

	private Thread generatingThread;
	private Logger logger = Logger.getLogger("UHCWorld");

	@Getter
	private boolean working = false;

	public static boolean running = false;

	public UHCWorldPreGenerator(File folder) {
		Preconditions.checkArgument(folder.exists() || folder.mkdirs());

		this.folder = folder;
		File[] files = folder.listFiles();
		if (files == null)
			return;

		for (File file : files) {
			if (isValidWorld(file))
				worlds.add(file.getAbsolutePath());
		}
		VaultyUHC.getInstance().getLogger().info("Nombre de mondes prêts: " + worlds.size());
	}

	public boolean isValidWorld(File folder) {
		if (!folder.isDirectory())
			return false;

		File levelDat = new File(folder, "level.dat");
		File uidDat = new File(folder, "uid.dat");
		File regionFolder = new File(folder, "region/");
		return levelDat.exists() && levelDat.isFile() && uidDat.exists() && uidDat.isFile() && regionFolder.exists() && regionFolder.isDirectory();
	}

	private File peekWorld() {
		if (worlds.isEmpty())
			return null;
		return new File(worlds.remove(0));
	}

	public World getPreloadedWorld(String name) throws IOException {
		File worldFile = peekWorld();
		if (worldFile == null)
			return null;

		WorldCreator worldCreator = new WorldCreator(name)
				.environment(World.Environment.NORMAL)
				.type(WorldType.NORMAL)
				.generateStructures(true);

		ServerUtil.removeWorld(worldCreator.name());
		ServerUtil.removeWorld(worldCreator.name() + "_nether");
		ServerUtil.removeWorld(worldCreator.name() + "_the_end");

		FileUtils.copyDirectory(worldFile, new File(Bukkit.getWorldContainer(), name));
		FileUtils.deleteDirectory(worldFile);

		return worldCreator.createWorld();
	}

	private void createWorld() {
		WorldCreator worldCreator = new WorldCreator("temp_world" + (worlds.size() + 1))
				.environment(World.Environment.NORMAL)
				.type(WorldType.NORMAL)
				.generateStructures(true);
		try {
			ServerUtil.removeWorld(worldCreator.name());
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		World world = worldCreator.createWorld();
		world.setAutoSave(false);
		world.setSpawnFlags(false, false);
		world.save();

		Bukkit.unloadWorld(world, true);
		Bukkit.createWorld(worldCreator);
	}

	private Map<Integer, String> replacedBiomes = Maps.newHashMap();

	private void replaceBiomes() {
		replacedBiomes.clear();

		Map<String, String> toReplace = Maps.newHashMap();
		toReplace.put("Deep Ocean", "PLAINS");
		toReplace.put("Ocean", "ROOFED_FOREST");
		toReplace.put("Jungle", "ROOFED_FOREST");
		toReplace.put("JungleHills", "ROOFED_FOREST");
		toReplace.put("JungleEdge", "ROOFED_FOREST");
		toReplace.forEach((from, to) -> {
			try {
				int replacedId = NMS.global.replaceBiome(from, to);
				if (replacedId > 0)
					replacedBiomes.put(replacedId, from);
			}
			catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	private void undoReplaceBiomes() {
		replacedBiomes.forEach((id, original) -> {
			try {
				NMS.global.setBiome(id, original);
			}
			catch (NoSuchFieldException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	public void preGenerate(int amount) {
		if (!working) {
			working = true;
			replaceBiomes();
		}

		final long start = System.currentTimeMillis();
		final int remaining = amount - 1;

		logger.info("Création du monde à générer.... (Etape 1/3)");
		createWorld();
		logger.info("Monde à générer créé (Etape 1/3)");

		final boolean asyncCatcher = AsyncCatcher.enabled;
		AsyncCatcher.enabled = false;
		(this.generatingThread = new Thread(() -> {
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			final World world = Bukkit.getWorld("temp_world" + (worlds.size() + 1));
			world.setAutoSave(false);
			try {
				Thread.sleep(2500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			running = true;
			world.save();

			logger.info("Pré-chargement des chunks.... (Etape 2/3)");
			final WorldFiller worldFiller = new WorldFiller(world, -750, 750, -750, 750);
			worldFiller.generateChunks();
			logger.info("Etape 2/3 terminée");

			logger.info("Déplacement du monde (Etape 3/3)");
			Bukkit.unloadWorld(world, true);
			try {
				move(world);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			logger.info("Monde déplacé avec succès (Etape 3/3)");

			long duration = System.currentTimeMillis() - start;
			logger.info("Génération terminée. Temps total: " + TimeUtil.fromSeconds((int) (duration / 1000)) + " (" + duration + "ms)");
			AsyncCatcher.enabled = asyncCatcher;

			if (remaining < 1) {
				working = false;
				undoReplaceBiomes();
				return;
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(VaultyUHC.getInstance(), () -> preGenerate(remaining));
		})).start();
	}

	private void move(World world) throws IOException {
		File worldFile = new File(Bukkit.getWorldContainer(), world.getName()), outFile = new File(folder, "preloaded" + MathUtil.getRandomInRange(1000, 9500));
		while (outFile.exists())
			outFile = new File(folder, outFile.getName() + "_1");
		FileUtils.copyDirectory(worldFile, outFile);
		FileUtils.deleteDirectory(worldFile);
		worlds.add(outFile.getAbsolutePath());
	}

	public void cancelIfAny() {
		if (generatingThread != null)
			generatingThread.interrupt();
		running = false;
	}

}

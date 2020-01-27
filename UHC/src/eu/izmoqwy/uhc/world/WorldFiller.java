/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.world;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ResourceBundle;

@Getter
public class WorldFiller {

	private final World world;
	private final int lowerX, lowerZ, upperX, upperZ;

	private Thread thread;

	public WorldFiller(World world, int x1, int x2, int z1, int z2) {
		this.world = world;

		this.lowerX = Math.min(x1, x2);
		this.upperX = Math.max(x1, x2);
		this.lowerZ = Math.min(z1, z2);
		this.upperZ = Math.max(z1, z2);
	}

	@SuppressWarnings("SynchronizeOnNonFinalField")
	public void generateChunks() {
		this.thread = new Thread();
		this.thread.start();
		try {
			this.thread.join();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		synchronized (this.thread) {
			final World w = getWorld();
			int startingX = getLowerX() & 0xFFFFFFF0, endingX = getUpperX() & 0xFFFFFFF0;
			int startingZ = getLowerZ() & 0xFFFFFFF0, endingZ = getUpperZ() & 0xFFFFFFF0;
			int current = 0;

			int max = ((Math.abs(startingX) + Math.abs(endingX)) / 16 + 1) * ((Math.abs(startingZ) + Math.abs(endingZ)) / 16 + 1);
			for (int x = startingX; x <= endingX; x += 16) {
				for (int z = startingZ; z <= endingZ; z += 16) {
					if (!UHCWorldPreGenerator.running) {
						this.thread.interrupt();
						return;
					}

					final Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
					chunk.load(true);
					chunk.unload(true, true);
					if (current % 100 == 0) {
						try {
							Thread.sleep(10L);
						}
						catch (InterruptedException e2) {
							e2.printStackTrace();
						}
					}

					if (current % 5000 == 0) {
						try {
							while (true) {
								try {
									getWorld().save();
									break;
								}
								catch (Exception ex) {
									ex.printStackTrace();
									log("Nouvelle tentative de sauvegarde dans 3 secondes.");
									try {
										Thread.sleep(3000);
									}
									catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							Chunk[] arrayOfChunk;
							for (int j = (arrayOfChunk = w.getLoadedChunks()).length, i = 0; i < j; ++i) {
								final Chunk c = arrayOfChunk[i];
								c.unload(true, true);
							}
						}
						catch (Exception ignored) {
						}

						try {
							ResourceBundle.clearCache();
						}
						catch (Exception e3) {
							e3.printStackTrace();
						}
						try {
							this.thread = new Thread();
							this.thread.start();
							try {
								this.thread.join();
							}
							catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (current % 1000 != 0)
								break;
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					float memoryUsage = getMemoryUsage();
					float progress = Math.round(current * 100f / max * 10f) / 10f;
					if (current % 50 == 0)
						log("en cours (" + current + "/" + max + " - " + progress + "%) [RAM: " + memoryUsage + "%]");


					current++;
					if (memoryUsage > 90) {
						while (getMemoryUsage() > 70) {
							try {
								log("en attente de mémoire (" + current + "/" + max + " - " + progress + "%) [RAM: " + memoryUsage + "%]");
								System.gc();
								Thread.sleep(5000L);
							}
							catch (InterruptedException e2) {
								e2.printStackTrace();
							}
						}
						try {
							Thread.sleep(3000L);
						}
						catch (InterruptedException e2) {
							e2.printStackTrace();
						}
					}
				}
			}
		}
	}

	private void log(String s) {
		Bukkit.getLogger().info("Génération: " + s);
	}

	private float getMemoryUsage() {
		long total = Runtime.getRuntime().totalMemory(), free = Runtime.getRuntime().freeMemory();
		return Math.round((total - free) * 100f / total * 10f) / 10f;
	}
}

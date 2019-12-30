/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.tasks;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.game.GameManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public class TeleportingTask extends BukkitRunnable {

	private final BlockFace[] faces = new BlockFace[]{
			BlockFace.NORTH_WEST, BlockFace.NORTH, BlockFace.NORTH_EAST,
			BlockFace.WEST, BlockFace.SELF, BlockFace.EAST,
			BlockFace.SOUTH_WEST, BlockFace.SOUTH, BlockFace.SOUTH_EAST
	};

	private Queue<Map.Entry<Player, Location>> teleportingQueue;
	private List<Block> placed = Lists.newArrayList();

	public TeleportingTask(Queue<Map.Entry<Player, Location>> teleportingQueue) {
		this.teleportingQueue = teleportingQueue;
	}

	@Override
	public void run() {
		if (teleportingQueue.isEmpty()) {
			GameManager.get.launch(this);
			cancel();
			return;
		}

		Map.Entry<Player, Location> teleportingEntry = teleportingQueue.remove();
		if (teleportingEntry == null || teleportingEntry.getKey() == null || !teleportingEntry.getKey().isOnline())
			return;

		Player player = teleportingEntry.getKey();
		Block block = teleportingEntry.getValue().getBlock();
		for (BlockFace face : faces) {
			block.getRelative(face).setType(Material.GLASS);
			player.teleport(block.getLocation().add(.5, 1.1, .5));
			placed.add(block.getRelative(face));
		}
	}

	public void post() {
		for (Block block : placed) {
			block.setType(Material.AIR);
		}
	}

}

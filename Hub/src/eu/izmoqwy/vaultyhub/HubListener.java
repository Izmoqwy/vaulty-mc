/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaultyhub;

import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.oss.ServerSlice;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.vaultyhub.moderation.Moderation;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class HubListener implements Listener {

	private boolean checkWorld(World world) {
		return world != null && world.getName().compareTo("Hub") == 0;
	}

	private boolean checkWorld(Location location) {
		return location != null && checkWorld(location.getWorld());
	}

	private boolean checkWorld(Entity entity) {
		return entity != null && checkWorld(entity.getLocation());
	}

	private void playerAction(Player player, ItemStack item) {
		if (VaultyHub.icon_currentGames.equals(item)) {
			player.sendMessage(VaultyCore.PREFIX + "§cCette fonction n'est pas encore prête.");
		}
		else if (VaultyHub.icon_join.equals(item)) {
			player.performCommand("uhc join");
		}
		else if (VaultyHub.icon_host.equals(item)) {
			player.performCommand("uhc host");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		if (event.getMessage().startsWith("*") && event.getMessage().length() > 1 && VaultyRank.get(event.getPlayer()).isEqualsOrAbove(Rank.HELPER)) {
			event.setCancelled(true);

			Bukkit.getLogger().info("[Staff] " + event.getPlayer().getName() + ": " + event.getMessage().substring(1).trim());
			Rank rank = VaultyRank.get(event.getPlayer());
			String message = "§7[Staff] " + rank.getFullName() + event.getPlayer().getName() + "§8: §b" + event.getMessage().substring(1).trim();
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (VaultyRank.get(player).isBelow(Rank.HELPER))
					continue;
				player.sendMessage(message);
			}
			return;
		}

		if (Moderation.getModeration().isMuted(event.getPlayer())) {
			event.setCancelled(true);

			Moderation.getModeration().sendMuteMessages(event.getPlayer(), VaultyCore.PREFIX + "§cVous êtes toujours muet.");
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerLogin(PlayerLoginEvent event) {
		if (Moderation.getModeration().isBanned(event.getPlayer())) {
			event.setResult(PlayerLoginEvent.Result.KICK_BANNED);
			event.setKickMessage(Moderation.getModeration().getBanMessage(event.getPlayer()));
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (!checkWorld(event.getPlayer()))
			return;

		if (event.getTo().getY() <= 10)
			VaultyHub.teleport(event.getPlayer());
	}

	private List<String> forbiddenCommands = Arrays.asList("me", "minecraft:me", "tell", "minecraft:tell");

	@EventHandler
	public void onPlayerPreProcessCommand(PlayerCommandPreprocessEvent event) {
		String command = event.getMessage().split(" ")[0].replace("/", "");
		if (forbiddenCommands.contains(command)) {
			event.getPlayer().sendMessage(VaultyCore.PREFIX + "§cCette commande est désactivée.");
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!checkWorld(event.getWhoClicked()))
			return;

		event.setCancelled(true);
		if (event.getClick() == ClickType.CREATIVE)
			return;

		Player player = (Player) event.getWhoClicked();
		if (event.getClickedInventory() == player.getInventory()) {
			playerAction(player, event.getCurrentItem());
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		VaultyHub.updateTabList(false);

		Player player = event.getPlayer();
		ServerSlice server = OSS.getServer(player);
		if (server.isDefault()) {
			server.broadcastMessage("§7[§2+§7] §a" + player.getName());
			VaultyHub.teleport(player);
			VaultyHub.giveInventory(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		VaultyHub.updateTabList(true);

		Player player = event.getPlayer();
		ServerSlice server = OSS.getServer(player);
		if (server.isDefault()) {
			server.broadcastMessage("§7[§4-§7] §c" + player.getName());
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (checkWorld(event.getFrom())) {
			PlayerUtil.clearInventory(event.getPlayer());
		}
	}

	@EventHandler
	public void onGameModeUpdate(PlayerGameModeChangeEvent event) {
		if (checkWorld(event.getPlayer()) && event.getNewGameMode() != GameMode.ADVENTURE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMobSpawn(EntitySpawnEvent event) {
		if (checkWorld(event.getLocation()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (checkWorld(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (checkWorld(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!checkWorld(event.getPlayer()))
			return;

		if (event.getAction() == Action.PHYSICAL)
			event.setCancelled(true);
		else
			playerAction(event.getPlayer(), event.getItem());
	}

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (checkWorld(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (checkWorld(event.getPlayer()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (checkWorld(event.getEntity()))
			event.setCancelled(true);
	}

	@EventHandler
	public void onFoodChange(FoodLevelChangeEvent event) {
		if (checkWorld(event.getEntity()))
			event.setCancelled(true);
	}

}

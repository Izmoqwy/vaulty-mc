/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event;

import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.event.entity.EntityKilledUHCEvent;
import eu.izmoqwy.uhc.event.player.*;
import eu.izmoqwy.uhc.event.registration.UHCEventManager;
import eu.izmoqwy.uhc.event.world.WorldBlockBreakUHCEvent;
import eu.izmoqwy.uhc.event.world.WorldBlockPlaceUHCEvent;
import eu.izmoqwy.uhc.game.*;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.VaultyCore;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.utils.MathUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MinecraftFont;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static eu.izmoqwy.uhc.game.GameState.*;

public class UHCBukkitListener implements Listener {

	@EventHandler
	public void onServerListPing(ServerListPingEvent event) {
		String line1 = "§aVaulty", line2;
		if (GameManager.get.getGameState() == null)
			line2 = "§cAucune partie n'est en cours";
		else if (GameManager.get.getGameState() == COMPOSING)
			line2 = "§bUne partie est en préparation";
		else
			line2 = "§aUne partie est en cours";

		if (UHCWorldManager.get.getPreGenerator().isWorking())
			line2 = "§4Tâche administrative en cours";
		event.setMotd(centerMOTD(line1, line1) + "\n" + centerMOTD("A_" + line2, "§f▚ " + line2));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event) {
		if (UHCWorldManager.get.getPreGenerator().isWorking()) {
			event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("§4Tâche administrative en cours");
		}
	}

	private String centerMOTD(String width, String text) {
		int spaces = (int) Math.floor((241 - MinecraftFont.Font.getWidth(ChatColor.stripColor(width))) / 8d);
		return StringUtils.repeat(" ", spaces) + text;
	}

	private boolean notInGame(Entity entity) {
		if (!(entity instanceof Player))
			return true;

		UHCGame currentGame = GameManager.get.getCurrentGame();
		return currentGame == null || !currentGame.getOnlinePlayers().contains(entity);
	}

	private boolean checkGameState(Entity entity, GameState... gameStates) {
		if (!(entity instanceof Player))
			return false;

		if (ArrayUtils.contains(gameStates, GameManager.get.getGameState())) {
			Player player = (Player) entity;
			GameManager gameManager = GameManager.get;
			if (gameManager.getCurrentGame() != null)
				return gameManager.getCurrentGame().getOnlinePlayers().contains(player) || gameManager.getCurrentGame().getOnlineSpectators().contains(player);
			if (gameManager.getWaitingRoom() != null)
				return gameManager.getWaitingRoom().getAllPlayers().contains(player);
		}
		return false;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (mayJoin(player))
			OSS.send(player, VaultyUHC.getUhcServer());
	}

	public boolean mayJoin(Player player) {
		GameManager gameManager = GameManager.get;
		if (gameManager.getGameState() == COMPOSING && gameManager.isCurrentHost(player)) {
			return true;
		}
		else
			return gameManager.getGameState() == GameState.PLAYING && gameManager.getCurrentGame().getGhosts().containsKey(player.getUniqueId()) && !gameManager.getKicked().contains(player.getUniqueId());
	}

	public void onServerJoin(Player player) {
		GameManager gameManager = GameManager.get;
		if (gameManager.isCurrentHost(player) && gameManager.getGameState() == COMPOSING) {
			gameManager.getWaitingRoom().addPlayer(player, GameActor.PLAYER, true);
			player.sendMessage(VaultyCore.PREFIX + "§eVous êtes toujours entrain de composer votre partie.");
		}
		else if (gameManager.getGameState() == GameState.PLAYING) {
			UHCGame game = gameManager.getCurrentGame();
			if (game.getGhosts().containsKey(player.getUniqueId())) {
				gameManager.getGameLoop().getCurrentScoreboard().addPlayer(player);
				player.sendMessage(" ");
				if (game.reconnect(player)) {
					player.sendMessage(VaultyUHC.PREFIX + "§aVous êtes de retour dans la partie.");
					PlayerReconnectUHCEvent uhcEvent = new PlayerReconnectUHCEvent(player);
					UHCEventManager.fireEvent(uhcEvent);
				}
				else {
					player.sendMessage(VaultyUHC.PREFIX + "§cVous avez été tué lorsque vous étiez déconnecté.");
				}
				player.updateInventory();
				player.sendMessage(" ");
			}
		}
	}

	public void onServerQuit(Player player) {
		GameManager gameManager = GameManager.get;
		if (gameManager.getWaitingRoom() != null && gameManager.getWaitingRoom().getAllPlayers().contains(player)) {
			gameManager.getWaitingRoom().removePlayer(player);
			return;
		}

		UHCGame currentGame = gameManager.getCurrentGame();
		if (currentGame == null)
			return;

		if (currentGame.getOnlinePlayers().contains(player)) {
			currentGame.turnToGhost(player);
			PlayerDisconnectUHCEvent uhcEvent = new PlayerDisconnectUHCEvent(player);
			UHCEventManager.fireEvent(uhcEvent);
		}
		else currentGame.getOnlineSpectators().remove(player);
		gameManager.getGameLoop().getCurrentScoreboard().removePlayer(player);
	}

	/*
	Inventaire
	 */

	private void playerAction(Player player, ItemStack item) {
		if (WaitingRoom.ComposingItems.CONFIG.equals(item)) {
			GameManager.get.getWaitingRoom().getComposerGUI().open(player);
		}
		else if (WaitingRoom.ComposingItems.START.equals(item)) {
			player.performCommand("composer finish");
		}
		else if (WaitingRoom.WaitingItems.TOGGLE_SPECTATOR.equals(item)) {
			GameManager.get.getWaitingRoom().addPlayer(player, GameActor.SPECTATOR, false);
			player.getInventory().setItem(8, WaitingRoom.WaitingItems.TOGGLE_PLAYER);
			player.sendMessage(VaultyCore.PREFIX + "§eVous êtes désormais §7Spectateur§e.");
		}
		else if (WaitingRoom.WaitingItems.TOGGLE_PLAYER.equals(item)) {
			if (GameManager.get.getWaitingRoom().getPlayers().size() >= GameManager.get.getCurrentComposer().getMaxPlayers()) {
				player.sendMessage(VaultyCore.PREFIX + "§cLa partie a atteint son maximum de joueurs.");
				return;
			}

			GameManager.get.getWaitingRoom().addPlayer(player, GameActor.PLAYER, false);
			player.getInventory().setItem(8, WaitingRoom.WaitingItems.TOGGLE_SPECTATOR);
			player.sendMessage(VaultyCore.PREFIX + "§eVous êtes désormais §aJoueur§e.");
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (checkGameState(event.getWhoClicked(), COMPOSING, STARTING, TELEPORTING)) {
			Player player = (Player) event.getWhoClicked();
			if (GameManager.get.getWaitingRoom() != null && GameManager.get.getWaitingRoom().getComposerGUI().getEditingInventory() == player)
				return;

			event.setCancelled(true);
			if (player.getInventory().equals(event.getClickedInventory())) {
				if (event.getCurrentItem() == null || checkGameState(player, COMPOSING)) {
					playerAction(player, event.getCurrentItem());
				}
			}
		}
		else if (checkGameState(event.getWhoClicked(), PLAYING)) {
			PlayerInventoryClickUHCEvent uhcEvent = new PlayerInventoryClickUHCEvent(event);
			UHCEventManager.fireEvent(uhcEvent);
			if (uhcEvent.isCancelled())
				uhcEvent.setCancelled(true);
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (checkGameState(event.getPlayer(), COMPOSING, STARTING, TELEPORTING)) {
			event.setCancelled(true);
			if (event.getAction() == Action.PHYSICAL || event.getItem() == null || !checkGameState(event.getPlayer(), COMPOSING))
				return;
			playerAction(event.getPlayer(), event.getItem());
		}
	}

	/*
	Jeu
	 */

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (checkGameState(event.getPlayer(), TELEPORTING) && isActualMove(event)) {
			event.getPlayer().teleport(event.getFrom());
		}
	}

	private boolean isActualMove(PlayerMoveEvent event) {
		return MathUtil.getDistanceXZ(event.getFrom(), event.getTo()) > 0.05;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onHealthRegen(EntityRegainHealthEvent event) {
		if (notInGame(event.getEntity()))
			return;

		PlayerRegenUHCEvent uhcEvent = new PlayerRegenUHCEvent(event);
		UHCEventManager.fireEvent(uhcEvent);
		event.setCancelled(uhcEvent.isCancelled());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (checkGameState(event.getEntity(), COMPOSING, STARTING, TELEPORTING, ENDED))
			event.setFoodLevel(20);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onAnyDamage(EntityDamageEvent event) {
		if (checkGameState(event.getEntity(), COMPOSING, STARTING, TELEPORTING, ENDED)) {
			event.setCancelled(true);
			return;
		}
		if (notInGame(event.getEntity())) {
			if (event.getEntity().hasMetadata("uhc-ghost") && GameManager.get.getCurrentGame() != null && GameManager.get.getCurrentGame().isInvincibility()) {
				event.setCancelled(true);
			}
			return;
		}

		PlayerAnyDamageUHCEvent uhcEvent = new PlayerAnyDamageUHCEvent(event);
		UHCEventManager.fireEvent(uhcEvent);
		if (uhcEvent.isCancelled())
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		if (!notInGame(event.getDamager())) {
			Player damager = (Player) event.getDamager();
			if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
				for (PotionEffect effect : damager.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
						int level = effect.getAmplifier() + 1;
						double initialDamage = event.getDamage() / (1.3 * level + 1);
						double difference = event.getDamage() - initialDamage;
						event.setDamage(initialDamage + (difference * 0.45));
						break;
					}
				}
			}
		}

		if (notInGame(event.getEntity())) {
			if (event.getEntity().hasMetadata("uhc-ghost") && GameManager.get.getCurrentGame() != null && !UHCWorldManager.getUhcWorld().getPVP()) {
				event.setCancelled(true);
			}
			return;
		}

		Player player = (Player) event.getEntity();
		if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			for (PotionEffect effect : player.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.DAMAGE_RESISTANCE)) {
					int level = effect.getAmplifier() + 1;
					double initialDamage = event.getDamage() / (0.2 * level);
					double difference = event.getDamage() - initialDamage;
					event.setDamage(initialDamage + (difference * 0.55));
					break;
				}
			}
		}

		EntityDamageEvent.DamageCause cause = event.getCause();
		if (cause == EntityDamageEvent.DamageCause.PROJECTILE) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player) {
				PlayerDamagePlayerUHCEvent uhcEvent = new PlayerDamagePlayerUHCEvent((Player) projectile.getShooter(), player, cause, event.getDamage());
				UHCEventManager.fireEvent(uhcEvent);
				event.setDamage(uhcEvent.getDamage());
				if (uhcEvent.isCancelled())
					event.setCancelled(true);
			}
		}
		else if (cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.THORNS) {
			if (!(event.getDamager() instanceof Player))
				return;

			PlayerDamagePlayerUHCEvent uhcEvent = new PlayerDamagePlayerUHCEvent((Player) event.getDamager(), player, cause, event.getDamage());
			UHCEventManager.fireEvent(uhcEvent);
			event.setDamage(uhcEvent.getDamage());
			if (uhcEvent.isCancelled())
				event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.getInventory().getType() == InventoryType.MERCHANT) {
			Inventory inventory = event.getInventory();
			if (inventory.getHolder() != null && inventory.getHolder() instanceof LivingEntity && ((LivingEntity) inventory.getHolder()).hasMetadata("uhc-ghost")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (notInGame(event.getEntity()))
			return;

		UHCEventManager.fireEvent(new PlayerDeathUHCEvent(event));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Player)
			return;

		UHCEventManager.fireEvent(new EntityKilledUHCEvent(event));
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onGhostKill(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof LivingEntity))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		if (entity.getHealth() - event.getFinalDamage() > 0)
			return;

		if (entity.hasMetadata("uhc-ghost") && GameManager.get.getCurrentGame() != null) {
			List<MetadataValue> values = entity.getMetadata("uhc-ghost");
			if (values.size() == 1) {
				Object value = values.get(0).value();
				if (!(value instanceof UUID))
					return;

				UUID uuid = (UUID) value;
				UHCGame game = GameManager.get.getCurrentGame();
				UHCGhost ghost = game.getGhosts().get(uuid);
				if (ghost != null && ghost.getGhostEntity() == entity) {
					GhostKilledUHCEvent uhcEvent = new GhostKilledUHCEvent(uuid, event);
					UHCEventManager.fireEvent(uhcEvent);
					if (uhcEvent.isCancelled()) {
						event.setCancelled(true);
						return;
					}

					ghost.drop(entity.getLocation());
					game.getGhosts().remove(uuid);
					game.eliminatePlayer(uuid);
					game.getKilledGhost().add(uuid);
					game.playerDeathSound();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPortalUse(PlayerPortalEvent event) {
		if (checkGameState(event.getPlayer(), PLAYING)) {
			switch (event.getCause()) {
				case NETHER_PORTAL:
					if (!GameManager.get.getCurrentComposer().isNetherEnabled())
						event.setCancelled(true);
					break;
				case END_PORTAL:
					if (!GameManager.get.getCurrentComposer().isEndEnabled())
						event.setCancelled(true);
					break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if (checkGameState(event.getPlayer(), COMPOSING, STARTING, TELEPORTING, ENDED)) {
			event.setCancelled(true);
			return;
		}
		else if (!(event.getItemDrop() instanceof ItemStack))
			return;

		if (notInGame(event.getPlayer()))
			return;

		PlayerDropOrPickupUHCEvent uhcEvent = new PlayerDropOrPickupUHCEvent(event.getPlayer(), (ItemStack) event.getItemDrop(), true);
		UHCEventManager.fireEvent(uhcEvent);
		uhcEvent.setCancelled(uhcEvent.isCancelled());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPickup(PlayerPickupItemEvent event) {
		if (checkGameState(event.getPlayer(), COMPOSING, STARTING, TELEPORTING, ENDED)) {
			event.setCancelled(true);
			return;
		}
		else if (!(event.getItem() instanceof ItemStack))
			return;

		if (notInGame(event.getPlayer()))
			return;

		PlayerDropOrPickupUHCEvent uhcEvent = new PlayerDropOrPickupUHCEvent(event.getPlayer(), (ItemStack) event.getItem(), false);
		UHCEventManager.fireEvent(uhcEvent);
		uhcEvent.setCancelled(uhcEvent.isCancelled());
	}

	/*
	Monde
	 */
	private Map<BlockBreakEvent, Map.Entry<ItemStack, Integer>> customBlockDrop = Maps.newHashMap();

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockBreakLow(BlockBreakEvent event) {
		if (checkGameState(event.getPlayer(), COMPOSING, STARTING, TELEPORTING, ENDED)) {
			event.setCancelled(true);
			return;
		}
		if (notInGame(event.getPlayer())) {
			return;
		}

		WorldBlockBreakUHCEvent uhcEvent = new WorldBlockBreakUHCEvent(event);
		UHCEventManager.fireEvent(uhcEvent);
		event.setCancelled(uhcEvent.isCancelled());
		if (!uhcEvent.isCancelled() && uhcEvent.getCustomDrop() != null) {
			customBlockDrop.put(event, new AbstractMap.SimpleEntry<>(uhcEvent.getCustomDrop(), uhcEvent.getExpToDrop()));
		}
		else
			event.setExpToDrop(uhcEvent.getExpToDrop());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreakHighest(BlockBreakEvent event) {
		if (!customBlockDrop.containsKey(event))
			return;

		if (!event.isCancelled() && event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			Block block = event.getBlock();
			block.setType(Material.AIR);
			block.getWorld().dropItemNaturally(block.getLocation().add(.5, 0, .5), customBlockDrop.get(event).getKey());
			block.getWorld().spawn(block.getLocation(), ExperienceOrb.class).setExperience(customBlockDrop.get(event).getValue());
		}
		customBlockDrop.remove(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (checkGameState(event.getPlayer(), COMPOSING, STARTING, TELEPORTING, ENDED)) {
			event.setCancelled(true);
			return;
		}
		if (notInGame(event.getPlayer())) {
			return;
		}

		WorldBlockPlaceUHCEvent uhcEvent = new WorldBlockPlaceUHCEvent(event);
		UHCEventManager.fireEvent(uhcEvent);
		event.setCancelled(uhcEvent.isCancelled());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChatMessage(AsyncPlayerChatEvent event) {
		if (checkGameState(event.getPlayer(), PLAYING) && !GameManager.get.getCurrentComposer().isChatMessages()) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(GameManager.get.getCurrentComposer().getGameType().getPrefix() + "§cLes messages sont désactivés pour cette partie.");
		}
	}

}

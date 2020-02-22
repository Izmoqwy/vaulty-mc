/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.obj;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.event.player.PlayerAnyDamageUHCEvent;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UHCGame implements UHCListener {

	private Player lastEliminated;

	private List<Player> onlinePlayers, onlineSpectators;

	private Map<UUID, UHCGhost> ghosts;
	private List<UUID> killedGhost;

	@Setter
	private boolean teleporting, invincibility;

	public UHCGame() {
		onlinePlayers = Lists.newArrayList();
		onlineSpectators = Lists.newArrayList();
		ghosts = Maps.newHashMap();
		killedGhost = Lists.newArrayList();
	}

	public GameComposer getGameComposer() {
		return GameManager.get.getCurrentComposer();
	}

	public void playerDeathSound() {
		for (Player onlinePlayer : onlinePlayers) {
			onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_DEATH, 1f, 1f);
		}
	}

	private void eliminatePlayer(Player player) {
		if (onlinePlayers.remove(player)) {
			lastEliminated = player;
			eliminatePlayer(player.getUniqueId());
			playerDeathSound();
		}
	}

	public void eliminatePlayer(UUID uuid) {
		if (getGameComposer() != null)
			getGameComposer().getGameType().checkForWin();
	}

	public void broadcast(String message, boolean prefix) {
		String finalMessage = prefix ? getPrefix() + message : message;
		onlinePlayers.forEach(player -> player.sendMessage(finalMessage));
		onlineSpectators.forEach(spec -> spec.sendMessage(finalMessage));
	}

	private String getPrefix() {
		if (getGameComposer() != null)
			return getGameComposer().getGameType().getPrefix();
		return VaultyUHC.PREFIX;
	}

	public void broadcast(String message) {
		broadcast(message != null ? message : " ", message != null);
	}

	public void spectate(Player player) {
		eliminatePlayer(player);
		onlineSpectators.add(player);

		PlayerUtil.reset(player);
		GameManager.get.teleportToMiddle(player);
		player.setGameMode(GameMode.SPECTATOR);
	}

	public void turnToGhost(Player player) {
		Preconditions.checkNotNull(player);
		if (!onlinePlayers.remove(player))
			return;

		Villager ghost = (Villager) UHCWorldManager.getUhcWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
		ghost.setCustomName("§c" + player.getName());
		ghost.setCustomNameVisible(true);
		ghost.setRemoveWhenFarAway(false);
		ghost.setProfession(Villager.Profession.PRIEST);
		ghost.setMaxHealth(player.getMaxHealth());
		ghost.setHealth(player.getHealth());
		ghost.setFireTicks(player.getFireTicks());

		ghost.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 99, false, false));
		ghost.setMetadata("uhc-ghost", new FixedMetadataValue(VaultyUHC.getInstance(), player.getUniqueId()));
		ghosts.put(player.getUniqueId(), UHCGhost.from(ghost, player));
		getGameComposer().getGameType().checkForWin();
	}

	public boolean reconnect(Player player) {
		Preconditions.checkNotNull(player);
		UUID uuid = player.getUniqueId();
		if (killedGhost.contains(uuid)) {
			killedGhost.remove(uuid);
			spectate(player);
			return false;
		}
		if (!ghosts.containsKey(uuid))
			return false;

		UHCGhost ghost = ghosts.remove(uuid);
		LivingEntity ghostEntity = ghost.getGhostEntity();
		player.teleport(ghostEntity.getLocation());
		player.setMaxHealth(ghostEntity.getMaxHealth());
		player.setHealth(ghostEntity.getHealth());
		player.setFallDistance(ghostEntity.getFallDistance());
		player.setFireTicks(ghostEntity.getFireTicks());
		ghostEntity.removeMetadata("uhc-ghost", VaultyUHC.getInstance());
		ghostEntity.damage(ghostEntity.getMaxHealth() + .1);
		ghostEntity.remove();
		ghost.apply(player);

		player.setGameMode(GameMode.SURVIVAL);

		onlinePlayers.add(player);
		return true;
	}

	@UHCEventHandler
	public void onAnyDamage(PlayerAnyDamageUHCEvent event) {
		if (invincibility)
			event.setCancelled(true);
	}

	public int getDuration() {
		return GameManager.get.getGameLoop() != null ? GameManager.get.getGameLoop().getElapsedTime() : 0;
	}

	public List<UUID> getLivingPlayers() {
		List<UUID> livingPlayers = Lists.newArrayList(ghosts.keySet());
		livingPlayers.addAll(onlinePlayers.stream().map(Entity::getUniqueId).collect(Collectors.toList()));
		return livingPlayers;
	}

}

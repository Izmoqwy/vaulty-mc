/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.gui.composer.ComposerGUI;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.nms.NMS;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.scoreboard.VaultyScoreboard;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

@Getter
public class WaitingRoom {

	@Getter(AccessLevel.NONE)
	private GameManager gameManager;
	private ComposerGUI composerGUI;

	private VaultyScoreboard scoreboard;
	private List<Player> players = Lists.newArrayList(),
			spectators = Lists.newArrayList();

	public WaitingRoom(GameManager gameManager) {
		this.gameManager = gameManager;
		this.composerGUI = new ComposerGUI(gameManager.getCurrentComposer());

		scoreboard = new VaultyScoreboard("uhc-waitingroom", gameManager.getCurrentComposer().getGameTitle());
		scoreboard.reset(11);
		scoreboard.setLine(0, " ");
		scoreboard.setLine(1, "§e§lPartie:");
		scoreboard.setLine(2, "Mode: §a" + gameManager.getCurrentComposer().getGameType().getName());
		scoreboard.setLine(3, "Hôte: §a" + Bukkit.getOfflinePlayer(gameManager.getCurrentComposer().getGameHost()).getName());
		scoreboard.setLine(4, " ");
		scoreboard.setLine(5, "§e§lJoueurs:");
		updatePlayerCount();
		scoreboard.setLine(9, " ");;
		scoreboard.setLine(10, "§e§m═§r §6§lvaul", "§6§lty.minesr.com");
	}

	public void addPlayer(Player player, GameActor actorType, boolean forceTeleport) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(actorType);
		Preconditions.checkArgument(gameManager.ableToJoin());

		if (actorType == GameActor.PLAYER && !players.contains(player)) {
			spectators.remove(player);
			players.add(player);
		}
		else if (actorType == GameActor.SPECTATOR && !spectators.contains(player)) {
			players.remove(player);
			spectators.add(player);
		}
		if (forceTeleport || player.getWorld() != UHCWorldManager.getUhcWorld())
			sendToWaitingRoom(player);
		scoreboard.addPlayer(player);
		updatePlayerCount();
	}

	public void removePlayer(Player player) {
		Preconditions.checkNotNull(player);

		if (players.contains(player)) {
			players.remove(player);
			if (GameManager.get.isStarting() && GameManager.get.invalidComposition()) {
				broadcast("§cUn joueur a quitté et la composition est désormais invalide, lancement annulé !");
				GameManager.get.cancelStarting();
			}
		}
		else spectators.remove(player);
		scoreboard.removePlayer(player);

		if (getComposerGUI().getEditingInventory() == player)
			getComposerGUI().setEditingInventory(null);
		updatePlayerCount();
	}

	public List<Player> getAllPlayers() {
		List<Player> players = Lists.newArrayList(this.players);
		players.addAll(spectators);
		return players;
	}

	public void broadcast(String message) {
		broadcast(message, true);
	}

	public void broadcast(String message, boolean prefix) {
		String finalMessage = prefix ? VaultyUHC.PREFIX + message : message;
		getAllPlayers().forEach(player -> player.sendMessage(finalMessage));
	}

	public void sendToWaitingRoom(Player player) {
		Preconditions.checkNotNull(player);

		OSS.send(player, VaultyUHC.getUhcServer());
		gameManager.teleportToMiddle(player);
		resetInventory(player);
	}

	public void resetInventory(Player player) {
		Preconditions.checkNotNull(player);

		PlayerUtil.clearInventory(player);
		PlayerInventory inventory = player.getInventory();
		if (gameManager.isCurrentHost(player)) {
			inventory.setItem(0, ComposingItems.CONFIG);
			inventory.setItem(1, ComposingItems.START);
		}
		if (spectators.contains(player))
			inventory.setItem(8, WaitingItems.TOGGLE_PLAYER);
		else
			inventory.setItem(8, WaitingItems.TOGGLE_SPECTATOR);
	}

	public void updatePlayerCount() {
		scoreboard.setLine(6, "État: " + (gameManager.getWhitelist() != null && gameManager.getWhitelist().isEnabled() ? "§dWhitelist" : "§aOuvert"));
		scoreboard.setLine(7, "Attente: ", "§a" + players.size() + "/" + gameManager.getCurrentComposer().getMaxPlayers());
		scoreboard.setLine(8, "Spectateurs: ", "§7" + spectators.size());
	}

	public void updateDebugInfoState() {
		boolean hiddenCoordinates = gameManager.getCurrentComposer().isHideCoordinates();
		UHCWorldManager.getUhcWorld().setGameRuleValue("reducedDebugInfo", Boolean.toString(hiddenCoordinates));
		getAllPlayers().forEach(player -> NMS.packets.forceUpdateReducedDebugInfo(player, hiddenCoordinates));
	}

	public static class ComposingItems {
		public static final ItemStack CONFIG = new ItemBuilder(Material.COMMAND)
				.name("§e§lConfigurer").appendLore("§7Changez les paramètres de partie, gérez", "§7les scénarios et modifiez l'inventaire")
				.toItemStack(), START = new ItemBuilder(Material.REDSTONE)
				.name("§a§lLancer").appendLore("§7La partie est prête ? Lancez la !")
				.toItemStack();
	}

	public static class WaitingItems {
		public static final ItemStack TOGGLE_PLAYER = new ItemBuilder(SkullType.SKELETON)
				.name("§6Acteur de jeu").appendLore("§eVous êtes §7Spectateur§e.", " ", "§7§oCliquez pour devenir joueur")
				.toItemStack(), TOGGLE_SPECTATOR = new ItemBuilder(SkullType.PLAYER)
				.name("§6Acteur de jeu").appendLore("§eVous êtes §aJoueur§e.", " ", "§7§oCliquez pour devenir spectateur")
				.toItemStack();
	}
}

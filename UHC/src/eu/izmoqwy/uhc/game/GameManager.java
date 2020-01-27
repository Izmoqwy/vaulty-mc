/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.event.registration.UHCEventManager;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.game.obj.UHCGame;
import eu.izmoqwy.uhc.game.obj.UHCTeamGame;
import eu.izmoqwy.uhc.game.obj.WaitingRoom;
import eu.izmoqwy.uhc.game.obj.Whitelist;
import eu.izmoqwy.uhc.game.tasks.GameLoop;
import eu.izmoqwy.uhc.game.tasks.StartingTask;
import eu.izmoqwy.uhc.game.tasks.TeleportingTask;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.uhc.scenario.GameTypeDetails;
import eu.izmoqwy.uhc.scenario.Scenario;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.utils.MathUtil;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GameManager {

	@Getter
	private static LinkedHashMap<Class<? extends GameType>, GameTypeDetails> availableGameTypes = Maps.newLinkedHashMap();
	@Getter
	private static List<Scenario> availableScenarios = Lists.newArrayList();

	public static void registerGameType(Class<? extends GameType> gameTypeClass, GameTypeDetails gameTypeDetails) {
		availableGameTypes.put(gameTypeClass, gameTypeDetails);
	}

	public static void unregisterGameType(Class<? extends GameType> gameTypeClass) {
		availableGameTypes.remove(gameTypeClass);
	}

	public static void registerScenarios(Scenario... scenarios) {
		Collections.addAll(availableScenarios, scenarios);
	}

	@SafeVarargs
	public static void unregisterScenarios(Class<? extends Scenario>... scenarioClasses) {
		Scenario[] scenarios = availableScenarios.toArray(new Scenario[0]);
		for (Class<? extends Scenario> scenarioClass : scenarioClasses) {
			for (Scenario scenario : scenarios) {
				if (scenario.getClass().equals(scenarioClass)) {
					availableScenarios.remove(scenario);
					break;
				}
			}
		}
	}

	public static final GameManager get = new GameManager();

	/*

	 */

	@Getter(AccessLevel.NONE)
	private StartingTask startingTask;
	private GameLoop gameLoop;

	private WaitingRoom waitingRoom;
	private Whitelist whitelist;

	private GameState gameState;
	private GameComposer currentComposer;
	private UHCGame currentGame;

	private List<UUID> kicked = Lists.newArrayList();

	public void createGame(Player host, Class<? extends GameType> gameTypeClass) throws IllegalAccessException, InstantiationException {
		Preconditions.checkArgument(gameState == null);
		Preconditions.checkNotNull(gameTypeClass);
		Preconditions.checkNotNull(host);

		GameType gameType = gameTypeClass.newInstance();
		Preconditions.checkNotNull(gameType);

		currentComposer = gameType.getDefaultComposer().copy();
		currentComposer.setGameType(gameType);
		currentComposer.setGameHost(host.getUniqueId());

		waitingRoom = new WaitingRoom(this);
		whitelist = new Whitelist(true, Lists.newArrayList(host.getUniqueId()));

		gameType.onGameCreate(waitingRoom);
		gameState = GameState.COMPOSING;
	}

	public void startGame() {
		gameState = GameState.STARTING;
		startingTask = new StartingTask(this);
		startingTask.runTaskTimer(VaultyUHC.getInstance(), 0, 20);
	}

	public void teleport() {
		gameState = GameState.TELEPORTING;
		startingTask = null;

		World world = UHCWorldManager.getUhcWorld();
		world.setGameRuleValue("doDaylightCycle", Boolean.toString(!currentComposer.isEternalDay()));
		world.setTime(currentComposer.isEternalDay() ? 6000 : 0);

		waitingRoom.getAllPlayers().forEach(HumanEntity::closeInventory);

		UHCGame game;
		if (currentComposer instanceof TeamGameComposer) {
			TeamGameComposer teamGameComposer = (TeamGameComposer) currentComposer;
			Map<PreMadeTeam, List<OfflinePlayer>> teams = Maps.newHashMap(waitingRoom.getTeams());

			List<OfflinePlayer> teamed = Lists.newArrayList();
			teams.values().forEach(teamed::addAll);

			List<OfflinePlayer> unTeamed = waitingRoom.getPlayers().stream().filter(player -> !teamed.contains(player)).collect(Collectors.toList());
			Collections.shuffle(unTeamed);

			for (PreMadeTeam team : teams.keySet()) {
				while (!unTeamed.isEmpty() && teams.get(team).size() < teamGameComposer.getTeamSize()) {
					teams.get(team).add(unTeamed.remove(0));
				}
			}

			game = new UHCTeamGame(teams);
		}
		else
			game = new UHCGame();

		game.setTeleporting(true);
		game.getOnlinePlayers().addAll(waitingRoom.getPlayers());
		waitingRoom.getSpectators().forEach(game::spectate);
		waitingRoom.getScoreboard().destroy();
		waitingRoom = null;

		UHCEventManager.register(game);
		currentGame = game;

		gameLoop = new GameLoop(game);
		gameLoop.runTaskTimer(VaultyUHC.getInstance(), 0, 20);

		for (Player player : currentGame.getOnlinePlayers()) {
			player.setGameMode(GameMode.SURVIVAL);
			PlayerUtil.reset(player);
		}

		int radius = (int) ((currentComposer.getBorderInitialSize() * .85) / 2);
		int totalPoints = game.getOnlinePlayers().size();

		Queue<Map.Entry<Player, Location>> teleportingQueue = Queues.newArrayDeque();
		if (totalPoints > 0) {
			for (int i = 1; i <= totalPoints; i++) {
				Map.Entry<Integer, Integer> point = MathUtil.getPointOnCircle(radius, i, totalPoints);
				Location location = new Location(UHCWorldManager.getUhcWorld(), point.getKey(), 250, point.getValue());
				teleportingQueue.add(new AbstractMap.SimpleEntry<>(game.getOnlinePlayers().get(i - 1), location));
			}
		}
		new TeleportingTask(teleportingQueue).runTaskTimer(VaultyUHC.getInstance(), 0, 2);
	}

	public void teleportToMiddle(Player player) {
		Preconditions.checkNotNull(player);
		if (OSS.getServer(player) != VaultyUHC.getUhcServer())
			OSS.send(player, VaultyUHC.getUhcServer());
		if (player.isDead())
			player.spigot().respawn();
		player.teleport(new Location(UHCWorldManager.getUhcWorld(), 0, 231.1, 0));
	}

	public void randomTeleport(Player player, float range, int random) {
		Preconditions.checkNotNull(player);
		Preconditions.checkArgument(random > 0);
		Preconditions.checkArgument(range > .2 && range < 1);

		if (gameState != GameState.PLAYING || !currentGame.getOnlinePlayers().contains(player))
			return;

		if (OSS.getServer(player) != VaultyUHC.getUhcServer())
			OSS.send(player, VaultyUHC.getUhcServer());
		if (player.isDead())
			player.spigot().respawn();

		WorldBorder worldBorder = UHCWorldManager.getUhcWorld().getWorldBorder();
		Map.Entry<Integer, Integer> coordinates = MathUtil.getPointOnCircle((int) ((worldBorder.getSize() * range) / 2), MathUtil.getRandomInRange(1, random), random);
		player.teleport(UHCWorldManager.getUhcWorld().getHighestBlockAt(coordinates.getKey(), coordinates.getValue()).getLocation().add(0, 1.1, 0));
	}

	public void launch(TeleportingTask teleportingTask) {
		new BukkitRunnable() {
			private int timer = 0;

			@Override
			public void run() {
				if (timer == 5) {
					currentGame.setTeleporting(false);
					gameState = GameState.PLAYING;

					for (Player player : currentGame.getOnlinePlayers()) {
						player.playSound(player.getLocation(), Sound.GLASS, 1, 1);
						if (currentComposer.getStartingInventoryContents() != null) {
							player.getInventory().setContents(currentComposer.getStartingInventoryContents());
						}
						if (currentComposer.getStartingInventoryArmorContents() != null) {
							player.getInventory().setArmorContents(currentComposer.getStartingInventoryArmorContents());
						}
					}
					teleportingTask.post();

					currentComposer.getGameType().startGame(currentGame);
					currentComposer.getGameType().onStartGame(GameManager.this);
					currentComposer.getScenarios().forEach(scenario -> scenario.onStartGame(GameManager.this));
					handleUHCListeners(currentComposer.getGameType(), currentComposer.getScenarios(), true);
					cancel();
					return;
				}
				currentGame.getOnlinePlayers().forEach(player -> PlayerUtil.sendTitle(player, "§a" + (5 - timer), null, 10));
				timer++;
			}
		}.runTaskTimer(VaultyUHC.getInstance(), 0, 20);
	}

	private void handleUHCListeners(GameType gameType, List<Scenario> scenarios, boolean register) {
		List<UHCListener> toHandle = Lists.newArrayList();
		if (gameType instanceof UHCListener)
			toHandle.add((UHCListener) gameType);
		for (Scenario scenario : scenarios) {
			if (scenario instanceof UHCListener)
				toHandle.add((UHCListener) scenario);
		}
		if (toHandle.isEmpty())
			return;

		if (register)
			UHCEventManager.register(toHandle.toArray(new UHCListener[0]));
		else
			UHCEventManager.unregister(toHandle.toArray(new UHCListener[0]));
	}

	public boolean isStarting() {
		return startingTask != null;
	}

	public void cancelStarting() {
		if (startingTask == null)
			return;
		waitingRoom.getAllPlayers().forEach(player -> {
			PlayerUtil.sendTitle(player, "§cLancement annulé !", null, 30);
			player.setLevel(0);
		});
		waitingRoom.broadcast("§cLancement annulé.");
		startingTask.cancel();
		startingTask = null;
		gameState = GameState.COMPOSING;
	}

	public boolean ableToJoin() {
		return gameState == GameState.COMPOSING && currentComposer != null && startingTask == null && gameLoop == null && currentGame == null;
	}

	public boolean isCurrentHost(Player player) {
		return gameState != null && currentComposer.getGameHost().equals(player.getUniqueId());
	}

	public boolean invalidComposition() {
		return currentComposer == null || waitingRoom == null || currentComposer.getGameType() == null || currentComposer.getGameHost() == null || getCompositionStackTrace() != null;
	}

	public List<String> getCompositionStackTrace() {
		List<String> stacktrace = Lists.newArrayList();

		if (waitingRoom.getPlayers().size() < 2) {
			stacktrace.add("Il doit y avoir au moins 2 joueurs.");
		}

		if (currentComposer.getPvpStartsAt() <= currentComposer.getInvincibilityStopsAt()) {
			stacktrace.add("Le PvP ne peut pas être activé avant la fin de l'invincibilité");
		}

		if (currentComposer.getBorderShrinksAt() <= currentComposer.getInvincibilityStopsAt()) {
			stacktrace.add("La bordure ne peut pas bouger avant la fin de l'invincibilité");
		}

		if (waitingRoom.getComposerGUI().getEditingInventory() != null) {
			stacktrace.add("L'inventaire doit être sauvegardé avant de lancer");
		}

		List<String> gameTypeStacktrace = currentComposer.getGameType().checkComposer(currentComposer, waitingRoom);
		if (gameTypeStacktrace != null && !gameTypeStacktrace.isEmpty())
			stacktrace.addAll(gameTypeStacktrace);

		for (Scenario scenario : currentComposer.getScenarios()) {
			List<String> scenarioStacktrace = scenario.checkComposer(currentComposer, waitingRoom);
			if (scenarioStacktrace != null && !scenarioStacktrace.isEmpty())
				stacktrace.addAll(scenarioStacktrace);
		}

		return stacktrace.isEmpty() ? null : stacktrace;
	}

	public void end() {
		gameState = GameState.ENDED;

		if (startingTask != null)
			startingTask = null;
		if (currentComposer != null && currentGame != null) {
			currentComposer.getGameType().stopGame();
			handleUHCListeners(currentComposer.getGameType(), currentComposer.getScenarios(), false);
		}
		if (gameLoop != null) {
			gameLoop.destroyScoreboards();
			gameLoop.cancel();
			gameLoop = null;
		}
		waitingRoom = null;
		currentGame = null;
		currentComposer = null;
		kicked = Lists.newArrayList();

		Bukkit.getScheduler().runTaskLater(VaultyUHC.getInstance(), () -> {
			Lists.newArrayList(VaultyUHC.getUhcServer().getOnlinePlayers()).forEach(player -> OSS.send(player, OSS.getMainServer()));
			gameState = null;
		}, 20 * 20);
	}

}

package eu.izmoqwy.mangauhc.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.mangauhc.MangaUHC;
import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.mangauhc.team.MangaTeam;
import eu.izmoqwy.mangauhc.team.dbz.*;
import eu.izmoqwy.mangauhc.team.inazuma.TeamInazuma;
import eu.izmoqwy.mangauhc.team.naruto.RoleMadara;
import eu.izmoqwy.mangauhc.team.naruto.TeamNaruto;
import eu.izmoqwy.mangauhc.team.onepiece.RoleNami;
import eu.izmoqwy.mangauhc.team.onepiece.TeamOnePiece;
import eu.izmoqwy.mangauhc.team.pokemon.RolePierre;
import eu.izmoqwy.mangauhc.team.pokemon.TeamPokemon;
import eu.izmoqwy.uhc.event.player.*;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.PreMadeTeam;
import eu.izmoqwy.uhc.game.obj.UHCGame;
import eu.izmoqwy.uhc.game.obj.UHCTeamGame;
import eu.izmoqwy.uhc.game.obj.WaitingRoom;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MangaGameType extends GameType implements UHCListener {

	private Map<MangaTeam, Team> bukkitTeams;
	private Map<OfflinePlayer, MangaRole> roleMap;

	private List<BukkitRunnable> tasks;
	private Map<OfflinePlayer, List<String>> unsentMessages;
	private List<OfflinePlayer> offlineDuringAnnouncement;
	private Map<OfflinePlayer, List<Consumer<Player>>> reconnectConsumers;
	private UHCGame game;

	@Getter
	private boolean announced;
	@Getter
	private Map<OfflinePlayer, Team> badBoys;
	private Team badBoyTeam1, badBoyTeam2;

	public MangaGameType() {
		super("Manga UHC", "Sortez l'artillerie lourde", Material.EXPLOSIVE_MINECART, MangaComposer.defaultComposer, MangaComposer.class);
	}

	/*
	Game methods
	 */
	@Override
	public void startGame(UHCGame game) {
		if (this.game != null)
			return;
		this.game = game;

		bukkitTeams = Maps.newHashMap();
		roleMap = Maps.newHashMap();
		tasks = Lists.newArrayList();
		unsentMessages = Maps.newHashMap();
		offlineDuringAnnouncement = Lists.newArrayList();
		reconnectConsumers = Maps.newHashMap();
		badBoys = Maps.newHashMap();

		MangaComposer mangaComposer = (MangaComposer) game.getGameComposer();
		List<MangaTeam> originalTeamList = Lists.newArrayList(
				new TeamDBZ(),
				new TeamNaruto(),
				new TeamInazuma(),
				new TeamOnePiece(),
				new TeamPokemon()
		);
		Collections.shuffle(originalTeamList);

		List<ChatColor> remainingColors = Lists.newArrayList(ChatColor.values());
		Collections.shuffle(remainingColors);

		Scoreboard bukkitScoreboard = GameManager.get.getGameLoop().getCurrentScoreboard().getBukkitScoreboard();
		badBoyTeam1 = bukkitScoreboard.registerNewTeam("uhcteam-badboys1");
		badBoyTeam1.setPrefix(ChatColor.DARK_RED + "Méchant #1 ");
		badBoyTeam2 = bukkitScoreboard.registerNewTeam("uhcteam-badboys2");
		badBoyTeam2.setPrefix(ChatColor.DARK_RED + "Méchant #2 ");

		UHCTeamGame uhcTeamGame = (UHCTeamGame) game;
		List<PreMadeTeam> chosenTeam = uhcTeamGame.getPreMadeTeams().entrySet().stream()
				.filter(entry -> !entry.getValue().isEmpty())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		for (MangaTeam team : originalTeamList) {
			if (chosenTeam.isEmpty())
				break;
			PreMadeTeam associated = chosenTeam.remove(0);
			if (associated == null)
				break;

			team.setColor(associated.getColor());
			String teamName = team.getName().replace(' ', '_');
			Team bukkitTeam = bukkitScoreboard.registerNewTeam("uhct-" + (teamName.length() > 11 ? teamName.substring(0, 11) : teamName));
			bukkitTeam.setPrefix(team.getColor().toString());
			bukkitTeams.put(team, bukkitTeam);

			List<MangaRole> shuffledRoles = Lists.newArrayList(team.getRoles());
			Collections.shuffle(shuffledRoles);

			List<OfflinePlayer> teamPlayers = uhcTeamGame.getPreMadeTeams().get(associated);
			for (OfflinePlayer player : teamPlayers) {
				roleMap.put(player, shuffledRoles.remove(0));
				bukkitTeam.addEntry(player.getName());
			}
		}

		BukkitRunnable teamAnnouncementTask = new BukkitRunnable() {
			@Override
			public void run() {
				UHCGame uhcGame = GameManager.get.getCurrentGame();
				uhcGame.getLivingPlayers().stream().map(Bukkit::getOfflinePlayer).forEach(player -> {
					MangaRole role = roleMap.get(player);
					if (role == null) {
						sendMessage(player, getPrefix() + "§cVous n'avez pas de rôle, ceci n'est pas normal !");
						return;
					}

					List<MangaRole> possibleRoles = role.getParent().getRoles().stream()
							.filter(itRole -> itRole != role).collect(Collectors.toList());
					possibleRoles = Lists.newArrayList(possibleRoles.get(0), possibleRoles.get(1), role);
					Collections.shuffle(possibleRoles);

					sendMessage(player, " ");
					sendMessage(player, getPrefix() + "§6Vous faîtes parti de l’équipe " + role.getParent().getDisplayName() +
							" §6vous aurez un rôle parmi les trois suivants: §e" + possibleRoles.stream().map(MangaRole::getName)
							.collect(Collectors.joining("§6, §e")));
					sendMessage(player, " ");
				});
			}
		};
		BukkitRunnable roleAnnouncementTask = new BukkitRunnable() {
			@Override
			public void run() {
				UHCGame uhcGame = GameManager.get.getCurrentGame();
				List<OfflinePlayer> players = uhcGame.getLivingPlayers().stream().map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
				players.forEach(player -> {
					MangaRole role = roleMap.get(player);
					if (role != null && role.isWicked()) {
						badBoys.put(player, badBoys.size() < 4 ? badBoyTeam1 : badBoyTeam2);
					}
				});
				players.forEach(player -> {
					MangaRole role = roleMap.get(player);
					if (role == null) {
						sendMessage(player, getPrefix() + "§cVous n'avez pas de rôle, ceci n'est pas normal !");
						return;
					}
					bukkitTeams.get(role.getParent()).addEntry(player.getName());

					sendMessage(player, " ");
					sendMessage(player, "§e§l‖ §3Votre rôle est: " + role.getParent().getColor() + "§l" + role.getName());
					sendMessage(player, " ");
					for (String s : role.getDescription()) {
						sendMessage(player, "§e§l‖ §2" + s);
					}
					if (role.isWicked()) {
						sendMessage(player, "§e§l‖ §3Votre équipe de méchants comporte: §b" + badBoys.entrySet().stream()
								.filter(entry -> entry.getValue().equals(badBoys.get(player)))
								.map(entry -> entry.getKey().getName()).collect(Collectors.joining("§3, §b")));
					}
					sendMessage(player, " ");

					if (!player.isOnline()) {
						offlineDuringAnnouncement.add(player);
					}
					else {
						announceRole(player.getPlayer());
					}
				});
				bukkitTeams.forEach((team, bukkitTeam) -> bukkitTeam.setPrefix(team.getDisplayName() + " "));
				MangaGameType.this.announced = true;
			}
		};

		teamAnnouncementTask.runTaskLater(MangaUHC.getInstance(), mangaComposer.getTeamAnnouncement() * 20);
		roleAnnouncementTask.runTaskLater(MangaUHC.getInstance(), mangaComposer.getRoleAnnouncement() * 20);

		tasks.add(teamAnnouncementTask);
		tasks.add(roleAnnouncementTask);
	}

	@Override
	public void stopGame() {
		if (tasks != null) {
			for (BukkitRunnable task : tasks) {
				task.cancel();
			}
			tasks = null;
		}
	}

	@Override
	public void checkForWin() {
		if (game == null)
			return;

		if (isAnnounced()) {
			Map<Player, MangaRole> onlineRoleMap = roleMap.entrySet().stream()
					.filter(entry -> entry.getKey().isOnline())
					.map(entry -> Maps.immutableEntry(entry.getKey().getPlayer(), entry.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			Map<Player, Team> onlineBadBoys = badBoys.entrySet().stream()
					.filter(entry -> entry.getKey().isOnline())
					.map(entry -> Maps.immutableEntry(entry.getKey().getPlayer(), entry.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

			boolean oneTeamLeft = onlineRoleMap.keySet().stream().distinct().count() == 1;
			if (oneTeamLeft) {
				if (onlineRoleMap.values().stream().filter(MangaRole::isWicked).count() >= 1)
					return;

				// Victoire d'une team
				MangaTeam winningTeam = onlineRoleMap.values().iterator().next().getParent();

				game.broadcast(null);
				game.broadcast("§dVictoire de l'équipe " + winningTeam.getDisplayName() + " §d!");
				game.broadcast(null);

				GameManager.get.end();
				game = null;
			}
			else if (onlineRoleMap.size() == onlineBadBoys.size() && onlineBadBoys.values().stream().distinct().count() == 1) {
				// Victoire d'une team de méchants
				Team winningTeam = onlineBadBoys.values().iterator().next();

				game.broadcast(null);
				game.broadcast("§dVictoire de l'équipe " + winningTeam.getPrefix().trim() + " §d!");
				game.broadcast(null);

				GameManager.get.end();
				game = null;
			}
		}
		else {
			if (game.getOnlinePlayers().size() == 0) {
				GameManager.get.end();
			}
		}
	}

	@Override
	public List<String> checkComposer(GameComposer gameComposer, WaitingRoom waitingRoom) {
		List<String> stacktrace = Lists.newArrayList();

		MangaComposer mangaComposer = (MangaComposer) gameComposer;
		if (mangaComposer.getRoleAnnouncement() <= mangaComposer.getTeamAnnouncement()) {
			stacktrace.add("L'annonce des rôles doit avoir lieux après l'annonce des équipes.");
		}

		if (gameComposer.getMaxPlayers() > 40) {
			stacktrace.add("Le nombre de joueurs maximum ne peut excéder 40.");
		}

		return stacktrace;
	}

	@Override
	public String getPrefix() {
		return MangaUHC.PREFIX;
	}

	/*
	Utils
	 */
	public void sendMessage(OfflinePlayer player, String message) {
		if (player.isOnline()) {
			player.getPlayer().sendMessage(message);
			return;
		}

		List<String> messages = unsentMessages.getOrDefault(player, Lists.newArrayList());
		messages.add(message);
		unsentMessages.put(player, messages);
	}

	public MangaRole getRole(OfflinePlayer player) {
		return roleMap.get(player);
	}

	public Map.Entry<OfflinePlayer, MangaRole> getByRole(Class<? extends MangaRole> aClass) {
		for (Map.Entry<OfflinePlayer, MangaRole> entry : roleMap.entrySet()) {
			if (entry.getValue() != null && entry.getValue().isApplicable(aClass))
				return entry;
		}
		return null;
	}

	public void reveal(OfflinePlayer player) {
		MangaRole role = roleMap.get(player);
		if (role == null || !badBoys.containsKey(player))
			return;

		if (!isRevealed(player)) {
			bukkitTeams.get(role.getParent()).removeEntry(player.getName());
			badBoys.get(player).addEntry(player.getName());
		}
	}

	public boolean isRevealed(OfflinePlayer player) {
		return badBoys.get(player) != null && badBoys.get(player).getEntries().contains(player.getName());
	}

	public void doOrWaitReconnect(OfflinePlayer player, Consumer<Player> consumer) {
		if (player.isOnline()) {
			consumer.accept(player.getPlayer());
			return;
		}

		List<Consumer<Player>> consumers = reconnectConsumers.getOrDefault(player, Lists.newArrayList());
		consumers.add(consumer);
		reconnectConsumers.put(player, consumers);
	}

	private void announceRole(Player player) {
		MangaRole role = roleMap.get(player);
		if (role == null) {
			sendMessage(player, getPrefix() + "§4Vous n'avez pas de rôle, ceci est encore moins normal !");
			return;
		}
		role.onRoleGive(player);

		if (role.getStartingContent() != null && role.getStartingContent().length > 0 && ItemUtil.giveOrDrop(player, role.getStartingContent())) {
			player.sendMessage("§cVotre inventaire était plein, certains objets donnés par votre rôle ont été jetés par terre !");
		}
	}

	public List<Map.Entry<OfflinePlayer, MangaRole>> getTeamMates(Player player) {
		List<Map.Entry<OfflinePlayer, MangaRole>> list = Lists.newArrayList();

		MangaTeam team = getRole(player).getParent();
		roleMap.forEach(((player1, role) -> {
			if (role.getParent().equals(team) && !player.equals(player1))
				list.add(Maps.immutableEntry(player1, role));
		}));

		return list;
	}

	public void checkTeamEliminate(MangaTeam team) {
		for (Map.Entry<OfflinePlayer, MangaRole> entry : roleMap.entrySet()) {
			if (entry.getValue().getParent().equals(team) && !(entry.getValue().isWicked() && badBoys.containsKey(entry.getKey())))
				return;
		}

		game.broadcast("§eL'équipe " + team.getDisplayName() + " §eest désormais hors-compétition.");
	}

	/*
	Events
	 */
	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerDisconnect(PlayerDisconnectUHCEvent event) {
		checkForWin();
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerReconnect(PlayerReconnectUHCEvent event) {
		Player player = event.getPlayer();

		List<String> messages = unsentMessages.remove(player);
		if (messages != null)
			messages.forEach(player::sendMessage);
		if (offlineDuringAnnouncement.remove(player))
			announceRole(player);

		List<Consumer<Player>> consumers = reconnectConsumers.get(player);
		if (consumers != null) {
			for (Consumer<Player> consumer : consumers) {
				consumer.accept(player);
			}
		}
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerMove(PlayerMoveUHCEvent event) {
		MangaRole role = getRole(event.getPlayer());
		if (role != null && role.isFrozen())
			event.setCancelled(true);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onInteract(PlayerInteractUHCEvent event) {
		MangaRole role = getRole(event.getPlayer());
		if (role instanceof RoleNami) {
			PlayerInteractEvent bukkitEvent = event.getBukkitEvent();
			if (bukkitEvent.hasItem() && bukkitEvent.getItem().getType() == Material.EMPTY_MAP) {
				RoleNami roleNami = (RoleNami) role;
				if (bukkitEvent.getItem().equals(roleNami.getLocatorItem()) && roleNami.getRemaining() > 0) {
					List<Player> players = roleMap.entrySet().stream()
							.filter(entry -> !(entry.getValue().getParent() instanceof TeamOnePiece))
							.map(Map.Entry::getKey).filter(OfflinePlayer::isOnline)
							.map(OfflinePlayer::getPlayer).collect(Collectors.toList());

					if (players.isEmpty()) {
						event.getPlayer().sendMessage(getPrefix() + "§cAucun joueur d'autre équipe n'est en ligne !");
						return;
					}

					Collections.shuffle(players);
					Player target = players.remove(0);
					Location location = target.getLocation();

					event.getPlayer().sendMessage(getPrefix() + "§aUn joueur de l'équipe " + getRole(target).getParent().getDisplayName() + " §ase trouve en §2" +
							location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "§a.");
					roleNami.setRemaining(roleNami.getRemaining() - 1);
				}
			}
		}
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerAnyDamage(PlayerAnyDamageUHCEvent event) {
		if (event.getBukkitEvent().getCause() == EntityDamageEvent.DamageCause.FALL) {
			if (getRole(event.getPlayer()) instanceof RolePierre)
				event.setCancelled(true);
		}
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onGhostKill(GhostKilledUHCEvent event) {
		death(Bukkit.getOfflinePlayer(event.getVictim()));
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onDeath(PlayerDeathUHCEvent event) {
		event.getBukkitEvent().setDeathMessage(null);
		Player player = event.getPlayer(), killer = event.getPlayer().getKiller();
		MangaRole role = getRole(player);

		if (role instanceof RoleKrilin) {
			RoleKrilin krilin = (RoleKrilin) getRole(player);
			if (!krilin.isRevived()) {
				player.spigot().respawn();
				GameManager.get.randomTeleport(player, .8f, 60);
				PlayerUtil.giveEffect(player, PotionEffectType.WEAKNESS, (short) 0, (short) 20_000);
				krilin.setRevived(true);
				return;
			}
		}

		else if (role instanceof RoleGoku) {
			if (killer != null && getRole(killer) instanceof RoleFreezer) {
				PlayerUtil.giveEffect(killer, PotionEffectType.INCREASE_DAMAGE, (short) 1, (short) 20_000);
			}

			Map.Entry<OfflinePlayer, MangaRole> gohan = getByRole(RoleGohan.class);
			if (gohan != null) {
				doOrWaitReconnect(gohan.getKey(), gohanPlayer -> PlayerUtil.giveEffect(gohanPlayer, PotionEffectType.INCREASE_DAMAGE, (short) 0, (short) 20_000));
			}
		}
		else if (role instanceof RoleMadara) {
			Map.Entry<OfflinePlayer, MangaRole> naruto = getByRole(RoleGohan.class);
			if (naruto != null) {
				doOrWaitReconnect(naruto.getKey(), gohanPlayer -> {
					PlayerUtil.giveEffect(gohanPlayer, PotionEffectType.SPEED, (short) 1, (short) 20_000);
					PlayerUtil.giveEffect(gohanPlayer, PotionEffectType.WEAKNESS, (short) 0, (short) 20_000);
				});
			}
		}

		death(player);
		player.spigot().respawn();
		new BukkitRunnable() {
			@Override
			public void run() {
				if (game == null)
					return;

				roleMap.remove(player);
				badBoys.remove(player);
				if (!role.isWicked())
					checkTeamEliminate(role.getParent());
				game.spectate(event.getPlayer());
			}
		}.runTaskLater(MangaUHC.getInstance(), 2);
	}

	private void death(OfflinePlayer player) {
		MangaRole role = roleMap.get(player);
		game.broadcast(role.getParent().getColor() + player.getName() + " §eest mort.");
	}

}

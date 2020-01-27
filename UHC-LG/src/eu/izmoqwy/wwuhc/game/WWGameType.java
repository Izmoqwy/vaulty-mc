/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.game;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import eu.izmoqwy.uhc.event.player.*;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.event.world.WorldBlockBreakUHCEvent;
import eu.izmoqwy.uhc.event.world.WorldBlockPlaceUHCEvent;
import eu.izmoqwy.uhc.game.*;
import eu.izmoqwy.uhc.game.obj.UHCGame;
import eu.izmoqwy.uhc.game.obj.UHCGhost;
import eu.izmoqwy.uhc.game.obj.WaitingRoom;
import eu.izmoqwy.uhc.game.tasks.GameLoop;
import eu.izmoqwy.uhc.gui.composer.ComposerGUI;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.SimpleClickableJSON;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.nms.NMS;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import eu.izmoqwy.vaulty.utils.MathUtil;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.wwuhc.WerewolfUHC;
import eu.izmoqwy.wwuhc.gui.RolesGUI;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleGiver;
import eu.izmoqwy.wwuhc.role.RoleSide;
import eu.izmoqwy.wwuhc.role.RoleStealer;
import eu.izmoqwy.wwuhc.role.solo.RoleThief;
import eu.izmoqwy.wwuhc.role.village.*;
import eu.izmoqwy.wwuhc.role.werewolves.*;
import eu.izmoqwy.wwuhc.tasks.CustomDayCycle;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class WWGameType extends GameType implements UHCListener {

	private UHCGame game;
	private WWComposer gameComposer;

	@Getter
	private CustomDayCycle customDayCycle;

	public WWGameType() {
		super("Loup-Garou", "Un grand classique", Material.REDSTONE, WWComposer.defaultComposer, WWComposer.class);
	}

	/*
	Cycle de jeu
	 */
	private BukkitRunnable roleTask, coupleTask, voteTask;

	@Getter
	private Map<UUID, Byte> votes;
	@Getter
	private List<UUID> alreadyVoted = Lists.newArrayList();
	private Map.Entry<UUID, Double> voteTaken;

	@Getter
	private List<UUID> voteForbidden;

	@Getter
	@Setter
	private UUID protectedPlayer, lastProtectedPlayer;

	@Override
	public void startGame(UHCGame game) {
		this.game = game;
		this.customDayCycle = new CustomDayCycle(this, UHCWorldManager.getUhcWorld(), ((WWComposer) GameManager.get.getCurrentComposer()).getDayCycleLength());
		this.customDayCycle.runTaskTimer(WerewolfUHC.getInstance(), 0, 1);
		UHCWorldManager.getUhcWorld().setGameRuleValue("keepInventory", "true");
		GameManager.get.getGameLoop().setExtra(this::onGameLoop);

		roleTask = new BukkitRunnable() {
			@Override
			public void run() {
				List<Role> roles = WWGameType.this.gameComposer.getRoles();
				RoleVillager villager = new RoleVillager();
				if (!roles.contains(villager)) {
					villager.setNeeded(false);
					roles.add(villager);
				}

				roleMap = RoleGiver.attribute(roles, WWGameType.this.game.getLivingPlayers());
				WWGameType.this.onRolesGiven();
			}
		};
		roleTask.runTaskLater(WerewolfUHC.getInstance(), gameComposer.getRoleAnnouncement() * 20);

		if (gameComposer.getCoupleChoice() > 0) {
			coupleTask = new BukkitRunnable() {
				@Override
				public void run() {
					if (WWGameType.this.game.getOnlinePlayers().size() < 2)
						return;

					List<Player> players = Lists.newArrayList(WWGameType.this.game.getOnlinePlayers());
					Collections.shuffle(players);

					Player player1 = players.get(0), player2 = players.get(1);
					WWGameType.this.couple = new Couple(player1, player2);
					couple.execute((player, other) -> player.sendMessage(getPrefix() + "§aVous êtes amoureux de §d" + other.getName() + " §a. Vous devez donc gagner à deux en trahissant votre camp d'origine. Si l'un de vous deux meurt, l'autre se suicidera par amour."));
					checkForWin();

					couple.execute(player -> ItemUtil.giveOrDrop(player, new ItemStack(Material.COMPASS)));
				}
			};
			coupleTask.runTaskLater(WerewolfUHC.getInstance(), gameComposer.getCoupleChoice() * 20);
		}

		rolesGUI.clearBackButton();
	}

	@Override
	public void stopGame() {
		customDayCycle.cancel();
		roleTask.cancel();
		if (coupleTask != null)
			coupleTask.cancel();
		if (voteTask != null)
			voteTask.cancel();
		customDayCycle = null;
		roleTask = null;
		coupleTask = null;
		voteTask = null;
		votes = null;
		alreadyVoted = Lists.newArrayList();

		game = null;
		rolesGUI = null;

		protectedPlayer = null;
		lastProtectedPlayer = null;

		couple = null;
		witchCanRevive = true;
		vileCanInfect = true;
		roleMap = null;
		killedRoleMap = Maps.newHashMap();
		waitingToRevive = Maps.newHashMap();
		waitingToInfect = Maps.newHashMap();
		killedByThief = Lists.newArrayList();
		postSteal = Maps.newHashMap();
		foxUsed = 0;
		seerSpied = false;
	}

	@Override
	public void checkForWin() {
		if (areRolesGiven()) {
			List<RoleSide> roleSides = roleMap.entrySet().stream().filter(entry -> Bukkit.getPlayer(entry.getKey()) != null)
					.map(entry -> entry.getValue().getRoleSide()).distinct().collect(Collectors.toList());

			boolean coupleWinning = false;
			if (couple != null) {
				List<Boolean> coupleDistinct = roleMap.keySet().stream().filter(uuid -> Bukkit.getPlayer(uuid) != null)
						.map(uuid -> couple.contains(uuid)).distinct().collect(Collectors.toList());
				coupleWinning = coupleDistinct.size() == 1 ? coupleDistinct.get(0) : false;
			}
			if (roleSides.size() <= 1 || coupleWinning) {
				Map<String, Role> losers = Maps.newHashMap(), winners = Maps.newHashMap();
				killedRoleMap.forEach((uuid, role) -> losers.put(Bukkit.getOfflinePlayer(uuid).getName(), role));

				roleMap.forEach(((uuid, role) -> {
					Player player = Bukkit.getPlayer(uuid);
					if (player != null)
						winners.put(player.getName() + (couple != null && couple.contains(uuid) ? " §c❤§d" : ""), role);
					else
						losers.put(Bukkit.getOfflinePlayer(uuid).getName() + (couple != null && couple.contains(uuid) ? " §c❤§d" : "") + " [Hors ligne]", role);
				}));

				if (roleSides.size() == 1) {
					RoleSide roleSide = roleSides.get(0);
					if (roleSide == RoleSide.SOLO && winners.size() > 1)
						return;
					if (roleSide == RoleSide.WEREWOLF && winners.size() > 1) {
						Map.Entry<UUID, Role> whiteWerewolf = getAssignedRole(RoleWhiteWW.class);
						if (whiteWerewolf != null && winners.containsKey(Bukkit.getOfflinePlayer(whiteWerewolf.getKey()).getName()))
							return;
					}
				}

				game.broadcast(null);
				losers.forEach((player, role) -> game.broadcast("§5§m" + player + " ➾ " + role.getName(), false));
				winners.forEach((player, role) -> game.broadcast("§d" + player + " ➾ " + role.getName(), false));
				winners.forEach((player, role) -> Bukkit.getPlayerExact(player.split(" ")[0]).setGameMode(GameMode.ADVENTURE));
				game.broadcast(null);
				GameManager.get.end();
			}
		}
		else {
			if (game.getOnlinePlayers().size() == 0) {
				GameManager.get.end();
			}
		}
	}

	@Getter
	private RolesGUI rolesGUI;

	@Override
	public void onGameCreate(WaitingRoom waitingRoom) {
		this.gameComposer = (WWComposer) GameManager.get.getCurrentComposer();

		ComposerGUI composerGUI = waitingRoom.getComposerGUI();
		rolesGUI = new RolesGUI("§e§lConfig §8» §a§lRôles", composerGUI, gameComposer);

		composerGUI.setItem(2, new ItemBuilder(Material.NAME_TAG)
				.name("§a§lRôles").appendLore("§7Choisir les rôles LG")
				.toItemStack());
		composerGUI.addListener(new GUIListener() {
			@Override
			public void onClick(Player player, ItemStack clickedItem, int slot) {
				if (slot == 2) {
					rolesGUI.open(player);
				}
			}
		});
	}

	@Override
	public String getPrefix() {
		return WerewolfUHC.PREFIX;
	}

	@Override
	public List<String> checkComposer(GameComposer gameComposer, WaitingRoom waitingRoom) {
		WWComposer composer = (WWComposer) gameComposer;
		List<String> stacktrace = Lists.newArrayList();

		if (composer.isEternalDay()) {
			stacktrace.add("La journée infinie n'est pas compatible avec les LG");
		}

		if (composer.getRoles().size() <= 1) {
			stacktrace.add("Il doit y avoir au moins deux rôles");
		}
		else {
			if (composer.getRoles().stream().map(Role::getRoleSide).distinct().count() <= 1) {
				stacktrace.add("Plusieurs camps doivent être présents parmi les rôles");
			}
		}

		if (waitingRoom.getPlayers().size() < composer.getRoles().stream().map(role -> {
			if (role.isApplicable(RoleWerewolf.class)) {
				return ((RoleWerewolf) role).getAmount();
			}
			return 1;
		}).mapToInt(Integer::valueOf).sum()) {
			stacktrace.add("Il ne peut pas y avoir plus de rôles que de joueurs");
		}

		if (composer.getCoupleChoice() < composer.getRoleAnnouncement() && composer.getCoupleChoice() != 0) {
			stacktrace.add("Le couple ne peut pas être annoncé avant les rôles");
		}

		if (composer.getVoteFirstDay() * composer.getDayCycleLength() * 2 < composer.getRoleAnnouncement()) {
			stacktrace.add("Les rôles doivent être annoncés avant le premier jour de vote");
		}

		if (composer.getVoteLength() >= composer.getDayCycleLength() * 2 + 30) {
			stacktrace.add("La durée des votes doit être inférieur à la durée du jour et de la nuit (+30s pour le dépouillement).");
		}

		return stacktrace;
	}

	public void onDayCycle(DayCycle dayCycle) {
		seerSpied = false;

		int count = (int) Math.ceil((gameComposer.getDayCycleLength() + game.getDuration()) * 1d / gameComposer.getDayCycleLength() / 2);
		game.broadcast(null);
		if (dayCycle == DayCycle.DAY) {
			game.broadcast("§e§lLe jour s'est levé sur le village. (#" + (count) + ")");
			lastProtectedPlayer = protectedPlayer;
			protectedPlayer = null;
			if (count >= gameComposer.getVoteFirstDay()) {
				game.broadcast("§eLes votes sont ouverts pour 30 secondes. Utilisez '/lg vote <pseudo>'.");
				votes = Maps.newHashMap();
				(voteTask = new BukkitRunnable() {
					@Override
					public void run() {
						voteForbidden = null;
						if (votes == null || roleMap == null)
							return;

						if (votes.isEmpty()) {
							game.broadcast(null);
							game.broadcast("§cLe village n'a pas trouvé d'accord commun pour le vote");
							game.broadcast(null);
							return;
						}

						byte max = votes.values().size() == 1 ? votes.values().toArray(new Byte[0])[0] : Collections.max(votes.values());
						List<Map.Entry<UUID, Byte>> voteEntry = votes.entrySet().stream().filter(entry -> entry.getValue() == max).collect(Collectors.toList());
						if (voteEntry.size() != 1) {
							game.broadcast(null);
							game.broadcast("§cLe village n'a pas trouvé d'accord commun pour le vote");
							game.broadcast(null);
						}
						else {
							byte count = voteEntry.get(0).getValue();

							OfflinePlayer target = Bukkit.getOfflinePlayer(voteEntry.get(0).getKey());
							game.broadcast(null);
							game.broadcast("§eAprès un dépouillement minutieux, §6§l" + target.getName() + "§e s'avère être le plus voté avec §6" + count + "§e votes.");
							game.broadcast(null);

							if (!roleMap.containsKey(target.getUniqueId()))
								return;

							if (isOnline(target)) {
								target.getPlayer().damage(0);
							}

							if (hasRole(target, RoleAngel.class)) {
								setMaxHealth(target, getMaxHealth(target) + count);
								if (count % 2 == 0)
									return;
							}

							double initialMaxHealth;
							if (isOnline(target)) {
								target.getPlayer().setMaxHealth((initialMaxHealth = target.getPlayer().getMaxHealth()) - 10);
							}
							else {
								UHCGhost ghost = game.getGhosts().get(target.getUniqueId());
								if (ghost == null)
									return;

								ghost.getGhostEntity().setMaxHealth((initialMaxHealth = ghost.getGhostEntity().getMaxHealth()) - 10);
							}
							voteTaken = new AbstractMap.SimpleEntry<>(target.getUniqueId(), initialMaxHealth);

							(voteTask = new BukkitRunnable() {
								@Override
								public void run() {
									if (roleMap == null)
										return;

									Map.Entry<UUID, Double> backHealth = voteTaken;
									voteTaken = null;

									if (!roleMap.containsKey(target.getUniqueId()) || backHealth == null)
										return;

									setMaxHealth(target, backHealth.getValue());
								}
							}).runTaskLater(WerewolfUHC.getInstance(), gameComposer.getVoteLength() * 20);
						}
						alreadyVoted.clear();
						votes = null;
					}
				}).runTaskLater(WerewolfUHC.getInstance(), Math.min(30, gameComposer.getDayCycleLength() - 5) * 20);
			}
		}
		else {
			game.broadcast("§9§lLa nuit est tombée sur le village. (#" + count + ")");
			if (count + 1 >= gameComposer.getVoteFirstDay()) {
				(voteTask = new BukkitRunnable() {
					@Override
					public void run() {
						Map.Entry<UUID, Role> teacherEntry = getAssignedRole(RoleTeacher.class);
						if (teacherEntry != null) {
							voteForbidden = Lists.newArrayList();
							Player onlineTeacher = Bukkit.getPlayer(teacherEntry.getKey());
							if (onlineTeacher != null) {
								onlineTeacher.sendMessage(getPrefix() + "§aVous avez §2" + Math.min(60, gameComposer.getDayCycleLength() - 5) + " secondes§a pour exclure des personnes du vote.");
								onlineTeacher.sendMessage(getPrefix() + "§2Faîtes '/lg interdire <joueur>' pour empêcher un joueur de voter.");
							}
						}
					}
				}).runTaskLater(WerewolfUHC.getInstance(), Math.min(60, gameComposer.getDayCycleLength() - 5) * 20);
			}
		}
		game.broadcast(null);
		if (roleMap != null) {
			roleMap.forEach((uuid, role) -> {
				Player player = Bukkit.getPlayer(uuid);
				if (player == null)
					return;

				role.onDayCycle(player, dayCycle, dayCycle == DayCycle.DAY ? DayCycle.NIGHT : DayCycle.DAY);
			});
		}
	}

	public void setMaxHealth(OfflinePlayer player, double maxHealth) {
		if (isOnline(player)) {
			player.getPlayer().setMaxHealth(maxHealth);
		}
		else {
			UHCGhost ghost = game.getGhosts().get(player.getUniqueId());
			if (ghost != null)
				ghost.getGhostEntity().setMaxHealth(maxHealth);
		}

		if (voteTask != null && voteTaken != null && voteTaken.getKey().equals(player.getUniqueId())) {
			voteTask.cancel();
		}
	}

	public double getMaxHealth(OfflinePlayer player) {
		if (roleMap == null || !roleMap.containsKey(player.getUniqueId()))
			return 20;

		if (voteTaken != null && voteTaken.getKey().equals(player.getUniqueId()))
			return voteTaken.getValue();

		if (isOnline(player))
			return player.getPlayer().getMaxHealth();
		else
			return game.getGhosts().get(player.getUniqueId()).getGhostEntity().getMaxHealth();
	}

	private void onGameLoop() {
		if (couple != null && couple.isOnline())
			couple.execute((player, other) -> player.setCompassTarget(other.getLocation()));

		game.getOnlinePlayers().forEach(player -> {
			double middleDistance = MathUtil.getDistanceXZ(0, 0, player.getLocation().getX(), player.getLocation().getZ());
			String posText;
			if (middleDistance <= 300)
				posText = "§aEntre §f0 §aet §f300§a blocs";
			else if (middleDistance <= 600)
				posText = "§eEntre §f300 §eet §f600§e blocs";
			else if (middleDistance <= 900)
				posText = "§eEntre §f600 §eet §f900§e blocs";
			else if (middleDistance <= 1200)
				posText = "§eEntre §f900 §eet §f1200§e blocs";
			else
				posText = "§6Plus de 1200 blocs";
			NMS.packets.sendActionBar(player, posText + " §f§l‖ §fY: §b" + player.getLocation().getBlockY());
		});

		Map.Entry<UUID, Role> wolfMother = getAssignedRole(RoleWolfMother.class);
		if (wolfMother == null)
			return;

		Map.Entry<UUID, Role> cub = getAssignedRole(RoleCub.class);
		if (cub == null)
			return;

		Player wolfMotherPlayer = Bukkit.getPlayer(wolfMother.getKey()), cubPlayer = Bukkit.getPlayer(cub.getKey());
		if (wolfMotherPlayer == null || cubPlayer == null)
			return;

		if (MathUtil.getDistanceXZ(wolfMotherPlayer.getLocation(), cubPlayer.getLocation()) <= 10 && MathUtil.getDistanceY(wolfMotherPlayer.getLocation().getY(), cubPlayer.getLocation().getY()) <= 10) {
			if (!wolfMotherPlayer.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE))
				PlayerUtil.giveEffect(wolfMotherPlayer, PotionEffectType.DAMAGE_RESISTANCE, (short) 0, (short) 2, true);
			if (!cubPlayer.hasPotionEffect(PotionEffectType.SPEED))
				PlayerUtil.giveEffect(cubPlayer, PotionEffectType.SPEED, (short) 0, (short) 2, true);
		}
	}

	/*
	Roles
	 */

	@Getter
	private Couple couple;

	@Getter
	private Map<UUID, Role> roleMap;
	private Map<UUID, Role> killedRoleMap = Maps.newHashMap();
	private List<UUID> offlineDuringRoleAnnouncement = Lists.newArrayList();

	private boolean witchCanRevive = true, vileCanInfect = true;
	private Map<Player, Location> waitingToInfect = Maps.newHashMap(), waitingToRevive = Maps.newHashMap();
	private List<UUID> killedByThief = Lists.newArrayList();

	@Getter
	@Setter
	private Player hunter;

	private CompositionScoreboards compositionScoreboards;

	@Getter
	@Setter
	@Accessors(fluent = true)
	private boolean seerSpied = true;

	@Getter
	@Setter
	private byte foxUsed;

	public boolean areRolesGiven() {
		return roleMap != null;
	}

	public Map.Entry<UUID, Role> getAssignedRole(Class<? extends Role> roleClass) {
		Preconditions.checkNotNull(roleClass);
		if (!areRolesGiven())
			return null;

		for (Map.Entry<UUID, Role> entry : roleMap.entrySet()) {
			if (entry.getValue().isApplicable(roleClass))
				return entry;
		}
		return null;
	}

	public Role getRole(OfflinePlayer player) {
		Preconditions.checkNotNull(player);
		if (!areRolesGiven())
			return null;

		return roleMap.get(player.getUniqueId());
	}

	public boolean hasRole(OfflinePlayer player, Class<? extends Role> roleClass) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(roleClass);
		if (!areRolesGiven() || !roleMap.containsKey(player.getUniqueId()))
			return false;

		Role role = getRole(player);
		if (role == null)
			return false;
		return role.isApplicable(roleClass);
	}

	private void onRolesGiven() {
		if (rolesGUI != null) {
			rolesGUI.breakBukkitInventory();
			rolesGUI = null;
		}

		List<UUID> werewolves = roleMap.entrySet().stream().filter(entry -> entry.getValue().getRoleSide() == RoleSide.WEREWOLF).map(Map.Entry::getKey).collect(Collectors.toList());
		final String wwMessage = "§e§l‖ §6Voici la liste des loups-garous: "
				+ werewolves.stream().map(uuid -> "§c§l" + Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.joining("§6, "));

		roleMap.forEach((uuid, role) -> {
			Player player = Bukkit.getPlayer(uuid);
			if (player != null) {
				announceRole(player);
				if (role.getRoleSide() == RoleSide.WEREWOLF)
					player.sendMessage(wwMessage);

				role.onDayCycle(player, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());
			}
			else
				offlineDuringRoleAnnouncement.add(uuid);

			role.onRoleAttribute(Bukkit.getOfflinePlayer(uuid));
		});

		GameLoop gameLoop = GameManager.get.getGameLoop();
		compositionScoreboards = new CompositionScoreboards(roleMap.values());
		compositionScoreboards.applyOnGameLoop(gameLoop);
		gameLoop.setAlternateScoreboardTimer(30);
		gameLoop.setAlternateScoreboardDuration(10);
	}

	public void announceRole(Player player) {
		Preconditions.checkNotNull(player);
		Preconditions.checkArgument(roleMap.containsKey(player.getUniqueId()));

		Role role = roleMap.get(player.getUniqueId());
		if (role == null) {
			player.sendMessage("§cVous n'avez pas de rôle, ce n'est pas normal !");
			return;
		}

		player.sendMessage(" ");
		player.sendMessage("§e§l‖ §3Votre rôle est: " + role.getColor() + "§l" + role.getName());

		RoleSide roleSide = role.getRoleSide();
		if (roleSide == RoleSide.VILLAGE)
			player.sendMessage("§e§l‖ §aVous devez gagner avec le village.");
		else if (roleSide == RoleSide.WEREWOLF && !role.getClass().equals(RoleWhiteWW.class))
			player.sendMessage("§e§l‖ §cVous devez gagner avec les autres loups.");
		else
			player.sendMessage("§e§l‖ §bVous devez gagner seul.");

		player.sendMessage(" ");
		for (String s : role.getDescription()) {
			player.sendMessage("§e§l‖ §2" + s);
		}
		role.onRoleReceive(player);
		player.sendMessage(" ");
	}

	/*
	Infection / Ressusciter
	 */
	public void infect(Player target, Player infecter) {
		if (!hasRole(infecter, RoleVFOW.class))
			return;

		if (!waitingToInfect.containsKey(target) || !vileCanInfect)
			return;

		vileCanInfect = false;
		waitingToInfect.remove(target);
		killedByThief.remove(target.getUniqueId());

		GameManager.get.randomTeleport(target, .6f, 40);
		roleMap.put(target.getUniqueId(), new RoleInfected(roleMap.get(target.getUniqueId())));
		thereIsANewWolf(target);
		getRole(target).onDayCycle(target, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());

		target.sendMessage(" ");
		target.sendMessage(getPrefix() + "§aVous avez été infecté. Vous devez désormais gagner dans le camps des §c§lLoups-Garous§a.");
		target.sendMessage(" ");
		infecter.sendMessage(getPrefix() + "§aVous avez infecter §6§l" + target.getName() + "§a.");
		checkForWin();
	}

	public void revive(Player target, Player reviver) {
		if (!hasRole(reviver, RoleWitch.class))
			return;

		if (!waitingToRevive.containsKey(target) || !witchCanRevive)
			return;

		witchCanRevive = false;
		waitingToRevive.remove(target);
		killedByThief.remove(target.getUniqueId());

		GameManager.get.randomTeleport(target, .6f, 40);
		getRole(target).onDayCycle(target, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());

		target.sendMessage(" ");
		target.sendMessage(getPrefix() + "§aVous avez été ressusciter. Profitez bien de votre nouvelle chance !");
		target.sendMessage(" ");
		reviver.sendMessage(getPrefix() + "§aVous avez ressusciter §6§l" + target.getName() + "§a.");
		checkForWin();
	}

	private boolean checkRevive(Player player, Location location) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(location);

		if (waitingToRevive.containsKey(player) || !witchCanRevive)
			return false;

		Map.Entry<UUID, Role> witch = getAssignedRole(RoleWitch.class);
		if (witch != null) {
			Player witchPlayer = Bukkit.getPlayer(witch.getKey());
			if (witchPlayer == null)
				return false;

			new SimpleClickableJSON(
					getPrefix() + "§eLe joueur §6§l" + player.getName() + "§e est mort. ", "§f§l[ §c✚ §f§lSoigner ]", "",
					"/werewolf --revive " + player.getName(), "§c➾ Sauver §6" + player.getName()
			).send(witchPlayer);
			waitingToRevive.put(player, location);
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!waitingToRevive.containsKey(player))
						return;

					death(player, waitingToRevive.remove(player));
				}
			}.runTaskLater(WerewolfUHC.getInstance(), 10 * 20);
			GameManager.get.teleportToMiddle(player);
			return true;
		}
		return false;
	}

	/*
	Events
	 */
	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onDeathEvent(PlayerDeathUHCEvent event) {
		Player player = event.getPlayer();
		event.getBukkitEvent().setDeathMessage(null);

		if (!areRolesGiven()) {
			player.spigot().respawn();
			GameManager.get.randomTeleport(player, .8f, 40);
			return;
		}

		if (waitingToInfect.containsKey(player)) {
			death(player, waitingToInfect.remove(player));
			return;
		}

		if (waitingToRevive.containsKey(player)) {
			death(player, waitingToRevive.remove(player));
			return;
		}

		Player killer = player.getKiller();
		if (hasRole(player, RoleAncient.class)) {
			RoleAncient roleAncient = (RoleAncient) getRole(player).getRootRole();
			if (roleAncient.canRevive()) {
				roleAncient.revive(player, killer != null && getRole(killer) != null && getRole(killer).getRoleSide() == RoleSide.WEREWOLF);
				GameManager.get.randomTeleport(player, .6f, 40);
				roleAncient.onDayCycle(player, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());
				return;
			}
		}
		if (killer != null && getRole(killer) != null) {
			Role role = getRole(killer);
			if (role.getRoleSide() != RoleSide.VILLAGE) {
				PlayerUtil.giveEffect(killer, PotionEffectType.ABSORPTION, (short) 0, (short) 120, true);
				PlayerUtil.giveEffect(killer, PotionEffectType.SPEED, (short) 0, (short) 60, true);
			}
		}
		if (killer != null && hasRole(killer, RoleThief.class)) {
			killedByThief.add(player.getUniqueId());
		}
		if (killer != null && hasRole(killer, RoleVFOW.class) && vileCanInfect && getRole(player).getRoleSide() != RoleSide.WEREWOLF) {
			new SimpleClickableJSON(
					getPrefix() + "§eVous avez tué §6§l" + player.getName() + "§e. Cliquez ", "§c§l[ ICI ]", "§e pour l'infecter.",
					"/werewolf --infect " + player.getName(), "§c➾ Infecter §6" + player.getName()
			).send(killer);
			waitingToInfect.put(player, player.getLocation());
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!waitingToInfect.containsKey(player))
						return;

					Location location = waitingToInfect.remove(player);
					if (checkRevive(player, location))
						return;

					death(player, location);
				}
			}.runTaskLater(WerewolfUHC.getInstance(), 10 * 20);
			GameManager.get.teleportToMiddle(player);
			return;
		}

		if (checkRevive(player, player.getLocation()))
			return;

		death(player, player.getLocation());
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerReconnect(PlayerReconnectUHCEvent event) {
		Player player = event.getPlayer();
		if (offlineDuringRoleAnnouncement.remove(player.getUniqueId())) {
			announceRole(player);
		}
		if (areRolesGiven())
			getRole(player).onDayCycle(player, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onPlayerDisconnect(PlayerDisconnectUHCEvent event) {
		Player player = event.getPlayer();
		Location location;
		if ((location = waitingToInfect.remove(player)) != null || (location = waitingToRevive.remove(player)) != null) {
			death(player, location);
		}
		checkForWin();
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onGhostKill(GhostKilledUHCEvent event) {
		death(Bukkit.getOfflinePlayer(event.getVictim()), event.getBukkitEvent().getEntity().getLocation());
	}

	// Protections de la salle d'attente (infect / revive)
	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onBlockPlace(WorldBlockPlaceUHCEvent event) {
		if (waitingToInfect.containsKey(event.getPlayer()) || waitingToRevive.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onBlockBreak(WorldBlockBreakUHCEvent event) {
		if (waitingToInfect.containsKey(event.getPlayer()) || waitingToRevive.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onDropOrPickup(PlayerDropOrPickupUHCEvent event) {
		if (waitingToInfect.containsKey(event.getPlayer()) || waitingToRevive.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onInventoryClick(PlayerInventoryClickUHCEvent event) {
		if (waitingToInfect.containsKey(event.getPlayer()) || waitingToRevive.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	@UHCEventHandler(priority = UHCEventPriority.GAME_TYPE)
	public void onAnyDamage(PlayerAnyDamageUHCEvent event) {
		if (waitingToInfect.containsKey(event.getPlayer()) || waitingToRevive.containsKey(event.getPlayer()))
			event.setCancelled(true);
	}

	/*
	Actions directes
	 */
	private void onHunterDeath(Player player) {
		hunter = player;
		hunter.sendMessage(getPrefix() + "§aVous avez 30 secondes pour réaliser votre tir final avec '/lg tirer <joueur>'.");
		new BukkitRunnable() {
			@Override
			public void run() {
				hunter = null;
			}
		}.runTaskLater(WerewolfUHC.getInstance(), 30 * 20);
	}

	private void death(OfflinePlayer player, Location location) {
		Preconditions.checkNotNull(player);
		Preconditions.checkNotNull(location);

		if (roleMap.containsKey(player.getUniqueId())) {
			double maxHealth = getMaxHealth(player), loverMaxHealth = 20;
			Role role = roleMap.remove(player.getUniqueId());
			killedRoleMap.put(player.getUniqueId(), role);

			new BukkitRunnable() {
				@Override
				public void run() {
					if (game == null)
						return;

					game.broadcast(null);
					game.broadcast("§6=-----------------------------------=", false);
					game.broadcast("  §eLe village a perdu un de ses membres:", false);
					game.broadcast("    §6§l" + player.getName() + "§e qui était " + role.getColor() + "§l" + role.getName(), false);
					game.broadcast("§6=-----------------------------------=", false);
					game.broadcast(null);
				}
			}.runTaskLater(WerewolfUHC.getInstance(), 5 * 20);
			if (isOnline(player)) {
				Player onlinePlayer = player.getPlayer();
				PlayerUtil.dropFullPlayerInventory(onlinePlayer.getInventory(), location);
				game.spectate(onlinePlayer);

				if (role.isApplicable(RoleHunter.class)) {
					onHunterDeath(onlinePlayer);
				}
			}

			boolean thiefStoleCouple = false;
			OfflinePlayer loverDeath = couple != null && couple.contains(player.getUniqueId()) ? couple.getOther(player) : null;
			Role loverRole = null;
			if (loverDeath != null && !(thiefStoleCouple = killedByThief.contains(player.getUniqueId()) && getAssignedRole(RoleThief.class) != null)) {
				couple = null;
				loverMaxHealth = getMaxHealth(loverDeath);
				loverRole = roleMap.remove(loverDeath.getUniqueId());
				if (loverRole == null)
					return;
				killedRoleMap.put(loverDeath.getUniqueId(), loverRole);

				Role finalLoverRole = loverRole;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (game == null)
							return;

						game.broadcast(null);
						game.broadcast("§6=-----------------------------------=", false);
						game.broadcast("  §c❤ §eDans son chagrin d'amour, §6§l" + loverDeath.getName() + " §es'est suicidé.", false);
						game.broadcast("    §eLe village perd un " + finalLoverRole.getColor() + "§l" + finalLoverRole.getName(), false);
						game.broadcast("§6=-----------------------------------=", false);
						game.broadcast(null);
					}
				}.runTaskLater(WerewolfUHC.getInstance(), 5 * 20);

				if (isOnline(loverDeath)) {
					Player onlineLoverDeath = loverDeath.getPlayer();
					waitingToRevive.remove(onlineLoverDeath);
					waitingToInfect.remove(onlineLoverDeath);
					PlayerUtil.dropFullPlayerInventory(onlineLoverDeath.getInventory(), onlineLoverDeath.getLocation());
					game.spectate(onlineLoverDeath);

					if (loverRole.isApplicable(RoleHunter.class)) {
						onHunterDeath(onlineLoverDeath);
					}
				}
				else {
					UHCGhost ghost = game.getGhosts().remove(loverDeath.getUniqueId());
					if (ghost != null) {
						ghost.drop(ghost.getGhostEntity().getLocation());
						ghost.getGhostEntity().remove();
						game.getKilledGhost().add(loverDeath.getUniqueId());
						game.eliminatePlayer(loverDeath.getUniqueId());
					}
				}
			}

			if (compositionScoreboards != null && roleMap != null)
				compositionScoreboards.update(roleMap.values());

			if (GameManager.get.getGameState() == GameState.ENDED)
				return;

			Map.Entry<UUID, Role> wildChild = getAssignedRole(RoleWildChild.class);
			if (wildChild != null && !wildChild.getValue().isInfected()) {
				RoleWildChild roleWildChild = (RoleWildChild) wildChild.getValue();
				if (roleWildChild.getMaster() == player.getUniqueId()) {
					roleWildChild.transform();
					if (Bukkit.getPlayer(wildChild.getKey()) != null) {
						Player onlineWildChild = Bukkit.getPlayer(wildChild.getKey());
						onlineWildChild.sendMessage(getPrefix() + "§aVotre maître est mort.");
						if (couple == null || !couple.contains(onlineWildChild.getUniqueId()))
							onlineWildChild.sendMessage("§e§l‖ §cVous devez gagner avec les autres loups.");
						roleWildChild.onDayCycle(onlineWildChild, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());
					}
					thereIsANewWolf(Bukkit.getOfflinePlayer(wildChild.getKey()));
					checkForWin();
				}
			}

			if (GameManager.get.getGameState() == GameState.ENDED)
				return;

			if (killedByThief.contains(player.getUniqueId())) {
				steal(player, role, maxHealth, RoleThief.class, loverDeath != null && thiefStoleCouple ? loverDeath : null);
				checkForWin();
				return;
			}

			if (GameManager.get.getGameState() == GameState.ENDED)
				return;

			checkMotherCub(player, role);
			if (loverDeath != null && loverRole != null) {
				checkMotherCub(loverDeath, loverRole);
			}

			mayPostSteal(player, role, maxHealth);
			if (loverDeath != null && loverRole != null) {
				mayPostSteal(loverDeath, loverRole, loverMaxHealth);
			}
		}
		else if (player.isOnline()) {
			game.spectate(player.getPlayer());
		}
	}

	private void checkMotherCub(OfflinePlayer death, Role role) {
		Preconditions.checkNotNull(death);

		OfflinePlayer target = null;
		if (role.isApplicable(RoleWolfMother.class)) {
			Map.Entry<UUID, Role> cub = getAssignedRole(RoleCub.class);
			if (cub != null)
				target = Bukkit.getOfflinePlayer(cub.getKey());
		}
		else if (role.isApplicable(RoleCub.class)) {
			Map.Entry<UUID, Role> mother = getAssignedRole(RoleWolfMother.class);
			if (mother != null)
				target = Bukkit.getOfflinePlayer(mother.getKey());
		}

		if (target != null) {
			if (target.isOnline())
				target.getPlayer().sendMessage(getPrefix() + "§5Votre liaison parentale est rompue, vous perdez donc 5 coeurs.");
			setMaxHealth(target, getMaxHealth(target) - 10);
		}
	}

	public void steal(OfflinePlayer player, Role role, double maxHealth, Class<? extends RoleStealer> stealerClass, OfflinePlayer lover) {
		Map.Entry<UUID, Role> thiefEntry = getAssignedRole(stealerClass);
		if (thiefEntry != null) {
			((RoleStealer) thiefEntry.getValue()).steal(role);

			Player thief = Bukkit.getPlayer(thiefEntry.getKey());
			if (lover != null) {
				couple.exchange(player, Bukkit.getOfflinePlayer(thiefEntry.getKey()));
				if (isOnline(lover))
					lover.getPlayer().sendMessage(getPrefix() + "§aVous avez un nouvel amoureux qui est §d" + thief.getName() + "§a.");
			}
			if (thief != null) {
				thief.sendMessage(getPrefix() + "§aVous avez volé le rôle de §6§l" + player.getName() + "§a.");
				if (voteTaken != null && voteTaken.getKey().equals(player.getUniqueId())) {
					voteTaken = null;
					setMaxHealth(player, maxHealth);
				}
				else
					setMaxHealth(thief, maxHealth);

				if (lover != null) {
					thief.sendMessage(getPrefix() + "§aVous avez également volé son couple. Votre amoureux est §d" + lover.getName() + "§a.");
					thief.sendMessage("§e§l‖ §dVous devez gagner en couple.");
				}
				else {
					RoleSide roleSide = role.getRoleSide();
					if (roleSide == RoleSide.VILLAGE)
						thief.sendMessage("§e§l‖ §aVous devez gagner avec le village.");
					else if (roleSide == RoleSide.WEREWOLF && !role.isApplicable(RoleWhiteWW.class))
						thief.sendMessage("§e§l‖ §cVous devez gagner avec les autres loups.");
					else
						thief.sendMessage("§e§l‖ §bVous devez gagner seul.");
				}

				if (thiefEntry.getValue().getRoleSide() == RoleSide.WEREWOLF)
					thereIsANewWolf(Bukkit.getOfflinePlayer(thiefEntry.getKey()));
				thiefEntry.getValue().onDayCycle(thief, customDayCycle.getDayCycle(), customDayCycle.getDayCycle());
				checkForWin();
			}
		}
	}

	@Getter
	private Map<UUID, Map.Entry<Role, Double>> postSteal = Maps.newHashMap();

	private void mayPostSteal(OfflinePlayer player, Role role, double maxHealth) {
		Map.Entry<UUID, Role> devotedServantEntry = getAssignedRole(RoleDevotedServant.class);
		if (devotedServantEntry != null && Bukkit.getPlayer(devotedServantEntry.getKey()) != null) {
			Player servant = Bukkit.getPlayer(devotedServantEntry.getKey());
			new SimpleClickableJSON(
					getPrefix() + "§eLe joueur §6§l" + player.getName() + "§e est mort. ", "§b§l[ Voler ]", "",
					"/werewolf --take-role " + player.getName(), "§c➾ Voler son rôle"
			).send(servant);

			postSteal.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(role, maxHealth));
			new BukkitRunnable() {
				@Override
				public void run() {
					postSteal.remove(player.getUniqueId());
				}
			}.runTaskLater(WerewolfUHC.getInstance(), 5 * 20);
		}
	}

	public void sendToWolves(String message) {
		sendToWolves(message, null);
	}

	public void sendToWolves(String message, OfflinePlayer except) {
		if (!areRolesGiven())
			return;
		roleMap.forEach((uuid, role) -> {
			if (role.getRoleSide() != RoleSide.WEREWOLF)
				return;

			if (except != null && except.getUniqueId().equals(uuid))
				return;

			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				player.sendMessage(message);
		});
	}

	private void thereIsANewWolf(OfflinePlayer player) {
		sendToWolves(getPrefix() + "§eLe joueur §6§l" + player.getName() + " §ea rejoint le camp des loups-garous.", player);
		if (isOnline(player))
			player.getPlayer().performCommand("werewolf list");
	}

}

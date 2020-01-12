/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.commands;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.uhc.scenario.GameType;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.rank.Rank;
import eu.izmoqwy.vaulty.rank.VaultyRank;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.vaulty.utils.TextUtil;
import eu.izmoqwy.wwuhc.WerewolfUHC;
import eu.izmoqwy.wwuhc.game.WWGameType;
import eu.izmoqwy.wwuhc.role.Role;
import eu.izmoqwy.wwuhc.role.RoleSide;
import eu.izmoqwy.wwuhc.role.village.*;
import eu.izmoqwy.wwuhc.tasks.DayCycle;
import net.minecraft.server.v1_8_R3.BiomeCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class WerewolfCommand extends VaultyCommand {

	public WerewolfCommand() {
		super("werewolf", true);
		setPrefix(WerewolfUHC.PREFIX);

		helpCommands.put("role", "Voir son rôle (et les loups si vous êtes LG)");
		helpCommands.put("c", "Parler avec les loups");
		helpCommands.put("choisir|voir|flairer|protéger|tirer|interdire", "Commandes d'action de certains rôles");
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		GameManager gameManager = GameManager.get;
		if (gameManager.getGameState() == GameState.PLAYING && gameManager.getCurrentComposer() != null && gameManager.getCurrentComposer().getGameType() instanceof WWGameType && gameManager.getCurrentGame().getOnlinePlayers().contains(player)) {
			WWGameType werewolf = (WWGameType) gameManager.getCurrentComposer().getGameType();
			if (match(args, 0, "--infect") && args.length == 2) {
				Player target = Bukkit.getPlayerExact(args[1]);
				if (target != null)
					werewolf.infect(target, player);
			}
			else if (match(args, 0, "--revive") && args.length == 2) {
				Player target = Bukkit.getPlayerExact(args[1]);
				if (target != null)
					werewolf.revive(target, player);
			}
			else if (match(args, 0, "--take-role") && args.length == 2) {
				OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
				if (target != null && werewolf.getPostSteal().containsKey(target.getUniqueId())) {
					Map.Entry<Role, Double> postSteal = werewolf.getPostSteal().remove(target.getUniqueId());
					werewolf.steal(target, postSteal.getKey(), postSteal.getValue(), RoleDevotedServant.class, null);
				}
			}
			else if (match(args, 0, "list")) {
				checkArgument(werewolf.areRolesGiven(), "Les rôles n'ont pas encore été attribués !");
				checkArgument(werewolf.getRole(player).getRoleSide() == RoleSide.WEREWOLF, "Vous devez être loup-garou pour voir cette liste.");

				send(player, "§6Voici la liste des loups-garous: " + (
						werewolf.getRoleMap().entrySet().stream()
								.filter(entry -> entry.getValue().getRoleSide() == RoleSide.WEREWOLF)
								.map(entry -> "§c§l" + Bukkit.getOfflinePlayer(entry.getKey()).getName()).collect(Collectors.joining("§6, "))
				));
			}
			else if (match(args, 0, "chat", "c")) {
				checkArgument(werewolf.areRolesGiven(), "Les rôles n'ont pas encore été attribués !");
				checkArgument(werewolf.getRole(player) != null && werewolf.getRole(player).getRoleSide() == RoleSide.WEREWOLF, "Vous n'êtes pas loup-garou.");
				checkArgument(werewolf.getCustomDayCycle().getDayCycle() == DayCycle.NIGHT, "Le village n'est pas plongé dans la nuit.");
				missingArg(args, 1, "Message à envoyer");

				String message = "§6[Loup-Garou] " + player.getName() + ": §e" + TextUtil.getFinalArg(args, 1);
				werewolf.sendToWolves(message);
				Map.Entry<UUID, Role> littleGirl = werewolf.getAssignedRole(RoleLittleGirl.class);
				if (littleGirl != null && !littleGirl.getValue().isInfected() && Bukkit.getPlayer(littleGirl.getKey()) != null)
					Bukkit.getPlayer(littleGirl.getKey()).sendMessage("§6[Loup-Garou] §k_censure_§6: §e" + TextUtil.getFinalArg(args, 1));
			}
			else if (match(args, 0, "don", "give")) {
				checkArgument(werewolf.getCouple() != null && werewolf.getCouple().contains(player.getUniqueId()), "Vous n'êtes pas en couple.");

				OfflinePlayer offlineOther = werewolf.getCouple().getOther(player);
				if (GameType.isOnline(offlineOther)) {
					missingArg(args, 1, "Pourcentage de vie à donner");
					int percentage;
					try {
						percentage = Integer.parseInt(args[1]);
					}
					catch (NumberFormatException ex) {
						send(player, "§cCe nombre est invalide.");
						return;
					}
					checkArgument(percentage > 0 && percentage < 90, "Veuillez donner un pourcentage entre 0 et 90%.");


					double health = player.getMaxHealth() * percentage / 100;
					checkArgument(player.getHealth() - health > 0, "Don annulé, il vous aurait coûté la vie !");

					Player other = offlineOther.getPlayer();
					checkArgument(other.getMaxHealth() - other.getHealth() >= health, "Votre partenaire n'a pas besoin d'autant de vie pour le moment.");
					other.setHealth(other.getHealth() + health);
					player.setHealth(player.getHealth() - health);
					send(player, "§aVous avez donné §2" + percentage + "% §ade vie.");
					send(other, "§aVous avez reçu §2" + percentage + "% §ade vie.");
				}
				else {
					send(player, "§cVotre partenaire est hors-ligne");
				}
			}
			else if (match(args, 0, "voir", "see")) {
				checkArgument(werewolf.hasRole(player, RoleSeer.class), "Vous n'êtes pas Voyante.");
				missingArg(args, 1, "Joueur à espionner");

				if (werewolf.getCustomDayCycle().getDayCycle() == DayCycle.DAY && !werewolf.seerSpied()) {
					OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
					checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");

					Role role = werewolf.getRole(target);
					String displayName = role.getColor() + "§l" + role.getName();
					send(player, "§3Le joueur §6§l" + target.getName() + "§3 possède le rôle " + displayName + "§3.");
					gameManager.getCurrentGame().broadcast(null);
					gameManager.getCurrentGame().broadcast("§fLa voyante a espionnée un joueur possédant le rôle " + displayName + "§f.");
					gameManager.getCurrentGame().broadcast(null);
					werewolf.seerSpied(true);
				}
				else {
					send(player, "§cVous ne pouvez pas espionner de joueur actuellement");
				}
			}
			else if (match(args, 0, "flairer", "nose")) {
				checkArgument(werewolf.hasRole(player, RoleFox.class), "Vous n'êtes pas Renard.");
				missingArg(args, 1, "Joueur à flairer");
				checkArgument(werewolf.getFoxUsed() < 3, "Vous avez déjà utilisé trois fois votre flaire.");

				Player target = Bukkit.getPlayerExact(args[1]);
				checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");
				checkArgument(target.getLocation().distance(player.getLocation()) <= 10, "Vous devez être à moins de 10 blocs du joueur en question.");

				send(player, "§aLe joueur flairé " + (werewolf.getRole(target).getRoleSide() == RoleSide.WEREWOLF ? "§2appartient" : "§2n'appartient pas") + " §aau camp des loups-garous.");
				werewolf.setFoxUsed((byte) (werewolf.getFoxUsed() + 1));
			}
			else if (match(args, 0, "interdire", "forbid")) {
				checkArgument(werewolf.hasRole(player, RoleTeacher.class), "Vous n'êtes pas Institutrice.");
				checkArgument(werewolf.getVoteForbidden() != null, "Vous ne pouvez pas faire cela pour le moment.");
				checkArgument(werewolf.getVoteForbidden().size() < 2, "Vous avez déjà utilisé votre pouvoir sur deux joueurs pour le prochain vote.");

				missingArg(args, 1, "Joueur à interdire");
				OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
				checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");

				werewolf.getVoteForbidden().add(target.getUniqueId());
				if (GameType.isOnline(target))
					target.getPlayer().sendMessage(werewolf.getPrefix() + "§cL'Institutrice vous a privé de votre droit de vote pour le prochain jour.");
				send(player, "§aInterdiction prise en compte.");
			}
			else if (match(args, 0, "vote", "voter")) {
				checkArgument(werewolf.areRolesGiven(), "Les rôles n'ont pas encore été attribués !");
				checkArgument(werewolf.getRoleMap().containsKey(player.getUniqueId()), "Vous n'avez pas de pouvoir d'action dans la partie (pas de rôle) !");
				checkArgument(werewolf.getVotes() != null, "Les votes ne sont pas ouverts !");
				checkArgument(!werewolf.getAlreadyVoted().contains(player.getUniqueId()), "Vous avez déjà voté !");
				checkArgument(werewolf.getVoteForbidden() == null || !werewolf.getVoteForbidden().contains(player.getUniqueId()), "§cVous ne pouvez pas voter.");
				checkArgument(!werewolf.hasRole(player, RoleTeacher.class) || werewolf.getVoteForbidden() == null || werewolf.getVoteForbidden().isEmpty(), "§cVous ne pouvez pas voter.");

				missingArg(args, 1, "Joueur à voter");
				OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
				checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");

				werewolf.getVotes().put(target.getUniqueId(), (byte) (werewolf.getVotes().getOrDefault(target.getUniqueId(), (byte) 0) + 1));
				werewolf.getAlreadyVoted().add(player.getUniqueId());
				send(player, "§aVote pris en compte.");
			}
			else if (match(args, 0, "proteger", "protéger", "protect")) {
				checkArgument(werewolf.hasRole(player, RoleSavior.class), "Vous n'êtes pas Salvateur.");

				missingArg(args, 1, "Joueur à protéger");
				OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
				checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");
				checkArgument(werewolf.getProtectedPlayer() == null, "Vous avez déjà protégé un joueur.");
				checkArgument(werewolf.getLastProtectedPlayer() != target.getUniqueId(), "Vous ne pouvez pas protéger la même personne deux jours de suite.");

				werewolf.setProtectedPlayer(target.getUniqueId());
				send(player, "§aSalvation donnée à §6§l" + target.getName() + "§a.");
				if (GameType.isOnline(target)) {
					Player onlineTarget = target.getPlayer();
					PlayerUtil.giveEffect(onlineTarget, PotionEffectType.DAMAGE_RESISTANCE, (short) 0, (short) 20_000, true);
					send(onlineTarget, "§aVous avez reçu la protection du Salvateur.");
				}
			}
			else if (match(args, 0, "choisir")) {
				checkArgument(werewolf.hasRole(player, RoleWildChild.class), "Vous n'êtes pas Enfant Sauvage.");
				Role role = werewolf.getAssignedRole(RoleWildChild.class).getValue();
				checkArgument(!role.isInfected(), "Vous êtes infecté.");
				RoleWildChild roleWildChild = (RoleWildChild) role;
				checkArgument(roleWildChild.getMaster() == null, "Vous avez déjà choisi un maître.");

				missingArg(args, 1, "Maître à choisir");
				OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
				checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");

				roleWildChild.setMaster(target.getUniqueId());
				send(player, "§aVous avez choisi §6§l" + target.getName() + "§a en tant que maître.");
			}
			else if (match(args, 0, "tirer", "shoot")) {
				checkArgument(werewolf.hasRole(player, RoleHunter.class), "Vous n'êtes pas Chasseur.");
				send(player, "§cLe tir final se réalise à la mort.");
			}
			else if (match(args, 0, "config")) {
				checkArgument(GameManager.get.isCurrentHost(player) || VaultyRank.get(player).isEqualsOrAbove(Rank.MODERATOR), "Vous ne pouvez pas changer les rôles.");
				checkArgument(werewolf.getRolesGUI() != null, "Il n'est plus possible de changer les rôles.");

				werewolf.getRolesGUI().open(player);
			}
			else if (match(args, 0, "help", "?")) {
				sendHelp(commandSender);
			}
			else {
				send(commandSender, "§cArgument invalide ou incorrect, faîtes '/lg help'.");
			}
		}
		else {
			if (gameManager.getGameState() == GameState.PLAYING && gameManager.getCurrentComposer() != null && gameManager.getCurrentComposer().getGameType() instanceof WWGameType) {
				WWGameType werewolf = (WWGameType) gameManager.getCurrentComposer().getGameType();
				if (match(args, 0, "tirer", "shoot")) {
					checkArgument(werewolf.getHunter() == player, "Vous ne pouvez pas tirer.");

					missingArg(args, 1, "Personne à tirer");
					OfflinePlayer target = PlayerUtil.getOfflinePlayer(args[1]);
					checkArgument(target != null && werewolf.getRole(target) != null, "Ce joueur n'est pas dans la partie.");
					if (GameType.isOnline(target))
						target.getPlayer().damage(10);

					send(player, "§aVous avez réalisé votre tir final.");
					gameManager.getCurrentGame().broadcast(null);
					gameManager.getCurrentGame().broadcast("§fLe chasseur a tiré sur §6§l" + target.getName() + "§f.");
					gameManager.getCurrentGame().broadcast(null);
					werewolf.setHunter(null);
					return;
				}
			}
			send(player, "§cVous n'êtes pas en partie de LG.");
		}
	}
}

package eu.izmoqwy.mangauhc.commands;

import eu.izmoqwy.mangauhc.MangaUHC;
import eu.izmoqwy.mangauhc.game.MangaGameType;
import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.mangauhc.team.dbz.RoleGoku;
import eu.izmoqwy.mangauhc.team.dbz.RoleVegeta;
import eu.izmoqwy.mangauhc.team.fairy.RoleZelephNoire;
import eu.izmoqwy.mangauhc.team.titans.RoleArmine;
import eu.izmoqwy.mangauhc.team.titans.RoleEren;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import eu.izmoqwy.vaulty.utils.PlayerUtil;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MangaCommand extends VaultyCommand {

	public MangaCommand() {
		super("manga", true);
		setPrefix(MangaUHC.PREFIX);

		helpCommands.put("role", "Voir son rôle (et les autres méchants si vous êtes méchant)");
		helpCommands.put("c", "Parler avec les méchants");
		helpCommands.put("reveal", "Se révéler être un méchant");
		helpCommands.put("spg|spv|inv|effect|titan", "Commandes d'action de certains rôles");
	}

	@Override
	public void executeCommand(CommandSender commandSender, String[] args) {
		Player player = (Player) commandSender;
		GameManager gameManager = GameManager.get;
		if (gameManager.getGameState() == GameState.PLAYING && gameManager.getCurrentComposer() != null
				&& gameManager.getCurrentComposer().getGameType() instanceof MangaGameType
				&& gameManager.getCurrentGame().getOnlinePlayers().contains(player)) {
			MangaGameType manga = (MangaGameType) gameManager.getCurrentComposer().getGameType();
			if (match(args, 0, "role", "list")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");

				MangaRole role = manga.getRole(player);
				player.sendMessage(" ");
				player.sendMessage("§e§l‖ §3Votre rôle est: " + role.getParent().getColor() + "§l" + role.getName());
				for (String s : role.getDescription()) {
					player.sendMessage("§e§l‖ §2" + s);
				}
				if (role.isWicked()) {
					player.sendMessage("§e§l‖ §3Votre équipe de méchants comporte: §b" + manga.getBadBoys().entrySet().stream()
							.filter(entry -> entry.getValue().equals(manga.getBadBoys().get(player)))
							.map(entry -> entry.getKey().getName()).collect(Collectors.joining("§3, §b")));
				}
				player.sendMessage(" ");
			}
			else if (match(args, 0, "chat", "c")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");
				checkArgument(manga.getRole(player) != null && manga.getRole(player).isWicked(), "Vous n'êtes pas méchant.");
				missingArg(args, 1, "Message à envoyer");

				Team badBoyTeam = manga.getBadBoys().get(player);
				String message = "§6[" + ChatColor.stripColor(badBoyTeam.getPrefix()) + "] " + player.getName() + ": §e" + TextUtil.getFinalArg(args, 1);
				manga.getBadBoys().forEach((badBoy, team) -> {
					if (badBoy.isOnline() && team.equals(badBoyTeam))
						badBoy.getPlayer().sendMessage(message);
				});
			}
			else if (match(args, 0, "reveal")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");
				checkArgument(manga.getRole(player) != null && manga.getRole(player).isWicked(), "Vous n'êtes pas méchant.");
				checkArgument(!manga.isRevealed(player), "Vous êtes déjà révélé.");

				manga.reveal(player);
				ItemUtil.give(player, new ItemStack(Material.GOLDEN_APPLE));
				gameManager.getCurrentGame().broadcast(ChatColor.DARK_RED + player.getName() + " §ese révèle être un méchant !");
			}
			else if (match(args, 0, "spg", "spv")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");

				MangaRole role = manga.getRole(player);
				checkArgument((role instanceof RoleGoku && match(args, 0, "spg"))
						|| (role instanceof RoleVegeta && match(args, 0, "spv")), "Vous ne pouvez pas faire ça.");
				checkArgument(!role.isTransformed(), "Vous êtes déjà transformé !");

				role.setFrozen(true);
				role.onTransform(player);
				send(player, "§aVous avez entamé votre transformation.");
				Bukkit.getScheduler().runTaskLater(MangaUHC.getInstance(), () -> {
					role.setFrozen(false);
					if (player.isOnline())
						send(player, "§aVous avez terminé votre transformation.");
				}, 10 * 20);
			}
			else if (match(args, 0, "inv")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");

				MangaRole role = manga.getRole(player);
				checkArgument(role instanceof RoleZelephNoire, "Vous ne pouvez pas faire ça.");
				RoleZelephNoire roleZelephNoire = (RoleZelephNoire) role;
				checkArgument(!roleZelephNoire.isUsedInvisibility(), "Vous avez déjà utilisé ce pouvoir !");

				roleZelephNoire.setUsedInvisibility(true);
				PlayerUtil.giveEffect(player, PotionEffectType.INVISIBILITY, (short) 0, (short) (60 * 5), true);
				send(player, "§aVous êtes invisible pour 5 minutes.");
			}
			else if (match(args, 0, "effects", "effect")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");

				MangaRole role = manga.getRole(player);
				checkArgument(role instanceof RoleArmine, "Vous ne pouvez pas faire ça.");
				RoleArmine roleArmine = (RoleArmine) role;
				checkArgument(roleArmine.getRemaining() > 0, "Vous avez déjà utilisé ce pouvoir !");

				List<Map.Entry<Player, MangaRole>> mates = manga.getOnlineTeamMates(player);
				checkArgument(!mates.isEmpty(), "Vous n'avez pas d'équipiers en ligne.");

				Collections.shuffle(mates);
				Player mate = mates.remove(0).getKey();

				roleArmine.setRemaining(roleArmine.getRemaining() - 1);
				PlayerUtil.giveEffect(mate, manga.getRandomEffect(), (short) 0, (short) (60 * 5), true);
				send(player, "§aVotre équipier §2" + mate.getName() + " §aa reçu un effet aléatoire pour 5 minutes.");
				send(mate, "§aVous avez reçu un effet aléatoire pour 5 minutes de la part d'un de vos équipiers.");
			}
			else if (match(args, 0, "titan")) {
				checkArgument(manga.isAnnounced(), "Les rôles n'ont pas encore été annoncés !");

				MangaRole role = manga.getRole(player);
				checkArgument(role instanceof RoleEren, "Vous ne pouvez pas faire ça.");
				checkArgument(!role.isTransformed(), "Vous avez déjà utilisé ce pouvoir !");

				role.setTransformed(true);
				PlayerUtil.giveEffect(player, PotionEffectType.DAMAGE_RESISTANCE, (short) 0, (short) (60 * 5), true);
				PlayerUtil.giveEffect(player, PotionEffectType.INCREASE_DAMAGE, (short) 0, (short) (60 * 5), true);
				send(player, "§aVotre évolution commence, vous avez reçu Force I et Résistance I pour 5 minutes.");

				Bukkit.getScheduler().runTaskLater(MangaUHC.getInstance(), () -> {
					if (player.isOnline()) {
						PlayerUtil.clearEffect(player, PotionEffectType.DAMAGE_RESISTANCE);
						PlayerUtil.clearEffect(player, PotionEffectType.INCREASE_DAMAGE);
						PlayerUtil.giveEffect(player, PotionEffectType.SLOW, (short) 0, (short) (60 * 3), true);
						send(player, "§aVotre évolution a pris fin, vous recevez Slowness I pour 3 minutes.");
					}
				}, 20 * 60 * 5);
			}
			else if (match(args, 0, "help", "?")) {
				sendHelp(commandSender);
			}
			else {
				send(commandSender, "§cArgument invalide ou incorrect, faîtes '/manga help'.");
			}
		}
		else {
			send(player, "§cVous n'êtes pas en partie de Manga UHC.");
		}
	}

}

package eu.izmoqwy.mangauhc.commands;

import eu.izmoqwy.mangauhc.MangaUHC;
import eu.izmoqwy.mangauhc.game.MangaGameType;
import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.mangauhc.team.dbz.RoleGoku;
import eu.izmoqwy.mangauhc.team.dbz.RoleVegeta;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.vaulty.VaultyCommand;
import eu.izmoqwy.vaulty.utils.ItemUtil;
import eu.izmoqwy.vaulty.utils.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

import java.util.stream.Collectors;

public class MangaCommand extends VaultyCommand {

	public MangaCommand() {
		super("manga", true);
		setPrefix(MangaUHC.PREFIX);

		helpCommands.put("role", "Voir son rôle (et les autres méchants si vous êtes méchant)");
		helpCommands.put("c", "Parler avec les méchants");
		helpCommands.put("reveal", "Se révéler être un méchant");
		helpCommands.put("spg|spv|clonage", "Commandes d'action de certains rôles");
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
				checkArgument(role != null && ((role.isApplicable(RoleGoku.class) && match(args, 0, "spg"))
						|| (role.isApplicable(RoleVegeta.class) && match(args, 0, "spv"))), "Vous ne pouvez pas faire ça.");
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

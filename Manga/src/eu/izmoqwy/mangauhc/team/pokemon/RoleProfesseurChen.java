package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.game.MangaGameType;
import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.uhc.game.GameManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleProfesseurChen extends MangaRole {

	public RoleProfesseurChen() {
		super("Professeur Chen", createDescription(
				"Vous êtes Professeur Chen, vous devez gagner avec votre équipe actuelle, " +
						"pour ce faire vous obtenez 2 bibliothèques de plus vous connaissez l’identité d’un coéquipier sûr."
		));
		this.startingContent = new ItemStack[]{
				new ItemStack(Material.BOOKSHELF, 2)
		};
	}

	@Override
	public void onRoleGive(Player player) {
		super.onRoleGive(player);

		MangaGameType manga = (MangaGameType) GameManager.get.getCurrentComposer().getGameType();
		List<OfflinePlayer> safeMates = manga.getTeamMates(player).stream()
				.filter(entry -> !entry.getValue().isWicked())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (safeMates.isEmpty()) {
			player.sendMessage(manga.getPrefix() + "§cVous n'avez pas d'équipier de confiance.");
			return;
		}

		Collections.shuffle(safeMates);
		player.sendMessage(manga.getPrefix() + "§aVous pouvez faire confiance à §2" + safeMates.remove(0).getName() + "§a.");
	}

}

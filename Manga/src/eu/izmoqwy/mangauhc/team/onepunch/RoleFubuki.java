package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.game.MangaGameType;
import eu.izmoqwy.mangauhc.team.MangaRole;
import eu.izmoqwy.uhc.game.GameManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RoleFubuki extends MangaRole {

	public RoleFubuki() {
		super("Fubuki", createDescription(
			"Vous êtes Fubuki, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez l'effet Résistance I. De plus, vous connaissez le rôle de l’un de vos coéquipiers."
		));
		this.effectTypes = new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE};
	}

	@Override
	public void onRoleGive(Player player) {
		super.onRoleGive(player);

		MangaGameType manga = (MangaGameType) GameManager.get.getCurrentComposer().getGameType();
		List<OfflinePlayer> mates = manga.getTeamMates(player).stream()
				.filter(entry -> !entry.getValue().isWicked())
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());

		if (mates.isEmpty()) {
			player.sendMessage(manga.getPrefix() + "§cVous n'avez pas d'équipier.");
			return;
		}

		Collections.shuffle(mates);
		OfflinePlayer mate = mates.remove(0);
		player.sendMessage(manga.getPrefix() + "§aLe joueur §2" + mate.getName() + "§a est " + manga.getRole(mate).getParent().getColor() + manga.getRole(mate).getName() + "§a.");
	}

}

package eu.izmoqwy.uhc.game.obj;

import eu.izmoqwy.uhc.game.PreMadeTeam;
import lombok.Getter;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;

@Getter
public class UHCTeamGame extends UHCGame {

	private Map<PreMadeTeam, List<OfflinePlayer>> preMadeTeams;

	public UHCTeamGame(Map<PreMadeTeam, List<OfflinePlayer>> preMadeTeams) {
		this.preMadeTeams = preMadeTeams;
	}

}

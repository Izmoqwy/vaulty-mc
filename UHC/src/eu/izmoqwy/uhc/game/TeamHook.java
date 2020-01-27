package eu.izmoqwy.uhc.game;

import eu.izmoqwy.uhc.game.obj.UHCTeam;
import org.bukkit.OfflinePlayer;

public interface TeamHook {

	void onTeamEmpty(UHCTeam team);

	void onTeamJoin(UHCTeam team, OfflinePlayer player);

	void onTeamLeave(UHCTeam team, OfflinePlayer player);

}

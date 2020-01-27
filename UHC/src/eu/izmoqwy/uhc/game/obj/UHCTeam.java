package eu.izmoqwy.uhc.game.obj;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.TeamGameComposer;
import eu.izmoqwy.uhc.game.TeamHook;
import eu.izmoqwy.uhc.scenario.GameType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.function.Consumer;

@Getter
public class UHCTeam {

	private Team bukkitTeam;
	private List<OfflinePlayer> players;
	@Setter
	private boolean unlimited;

	private UHCTeam(Team bukkitTeam) {
		this.bukkitTeam = bukkitTeam;
		this.players = Lists.newArrayList();
		this.unlimited = false;
	}

	public void addPlayer(OfflinePlayer player) {
		if (GameManager.get.getGameState() == null || players.contains(player) || isFull()) {
			return;
		}

		players.add(player);
		callHook(teamHook -> teamHook.onTeamJoin(this, player));
	}

	public void removePlayer(OfflinePlayer player) {
		if (GameManager.get.getGameState() == null || !players.contains(player)) {
			return;
		}

		players.remove(player);
		callHook(teamHook -> teamHook.onTeamLeave(this, player));
		if (players.isEmpty()) {
			callHook(teamHook -> teamHook.onTeamEmpty(this));
		}
	}

	public boolean isFull() {
		if (unlimited)
			return true;

		GameComposer gameComposer = GameManager.get.getCurrentComposer();
		return gameComposer instanceof TeamGameComposer && ((TeamGameComposer) gameComposer).getTeamSize() <= players.size();
	}

	private void callHook(Consumer<TeamHook> consumer) {
		if (GameManager.get.getCurrentComposer() == null)
			return;

		GameType gameType = GameManager.get.getCurrentComposer().getGameType();
		if (gameType instanceof TeamHook) {
			consumer.accept((TeamHook) gameType);
		}
	}

}

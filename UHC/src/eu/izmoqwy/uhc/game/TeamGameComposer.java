package eu.izmoqwy.uhc.game;

import eu.izmoqwy.uhc.config.GUISetting;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class TeamGameComposer extends GameComposer {

	public static final TeamGameComposer defaultComposer;

	static {
		defaultComposer = new TeamGameComposer(GameComposer.defaultComposer);
//		defaultComposer.setTeams(true);
		defaultComposer.setTeamSize(4);
	}

	public TeamGameComposer(GameComposer gameComposer) {
		super(gameComposer);
	}

	public TeamGameComposer(TeamGameComposer other) {
		super(other);
//		this.teams = other.teams;
		this.teamSize = other.teamSize;
	}

//	@GUISetting(name = "Équipes", icon = Material.IRON_SWORD)
//	private boolean teams;
	@GUISetting(name = "Taille des équipes", icon = Material.CHEST,
			min = 2, max = 6)
	private int teamSize;

	@Override
	public TeamGameComposer copy() {
		return new TeamGameComposer(this);
	}

}

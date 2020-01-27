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
		defaultComposer.setTeamSize(4);
		defaultComposer.setTeamEditable(true);
	}

	public TeamGameComposer(GameComposer gameComposer) {
		super(gameComposer);
	}

	public TeamGameComposer(TeamGameComposer other) {
		super(other);
		this.teamSize = other.teamSize;
		this.teamEditable = other.teamEditable;
	}

	@GUISetting(name = "Taille des équipes", icon = Material.CHEST,
			min = 2, max = 6)
	private int teamSize;

	private boolean teamEditable;

	@Override
	public TeamGameComposer copy() {
		return new TeamGameComposer(this);
	}

}

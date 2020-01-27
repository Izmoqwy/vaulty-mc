package eu.izmoqwy.moleuhc.game;

import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.TeamGameComposer;
import eu.izmoqwy.uhc.scenario.all.ScenarioDiamondLimit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class MoleComposer extends TeamGameComposer {

	public static final MoleComposer defaultComposer;

	static {
		defaultComposer = new MoleComposer(TeamGameComposer.defaultComposer);
		defaultComposer.setGameTitle("§6§lTaupe Gun");
		defaultComposer.setChatMessages(false);
		defaultComposer.setEternalDay(true);
		defaultComposer.setNetherEnabled(true);
		defaultComposer.setEndEnabled(false);
		defaultComposer.setMoleAnnouncement(60 * 20);

		ScenarioDiamondLimit diamondLimit = new ScenarioDiamondLimit();
		diamondLimit.setLimit(17);
		diamondLimit.setReplace(false);
		defaultComposer.getScenarios().add(diamondLimit);
	}

	public MoleComposer(TeamGameComposer gameComposer) {
		super(gameComposer);
	}

	public MoleComposer(MoleComposer other) {
		super(other);
		this.moleAnnouncement = other.moleAnnouncement;
	}

	@GUISetting(group = "timer", name = "Annonce des taupes", icon = Material.NAME_TAG, duration = true,
			min = 60, max = 60 * 40, step = 30)
	private int moleAnnouncement;

	@Override
	public MoleComposer copy() {
		return new MoleComposer(this);
	}

}

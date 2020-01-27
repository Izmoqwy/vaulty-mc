package eu.izmoqwy.mangauhc.game;

import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.game.TeamGameComposer;
import eu.izmoqwy.uhc.scenario.all.ScenarioDiamondLimit;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@Getter
@Setter
public class MangaComposer extends TeamGameComposer {

	public static final MangaComposer defaultComposer;

	static {
		defaultComposer = new MangaComposer(TeamGameComposer.defaultComposer);
		defaultComposer.setGameTitle("§6§lManga UHC");
		defaultComposer.setChatMessages(false);
		defaultComposer.setEternalDay(true);
		defaultComposer.setNetherEnabled(false);
		defaultComposer.setEndEnabled(false);

		defaultComposer.setTeamAnnouncement(15);
		defaultComposer.setRoleAnnouncement(30);
		defaultComposer.setPvpStartsAt(60);
		defaultComposer.setInvincibilityStopsAt(45);

		//defaultComposer.setTeamAnnouncement(60 * 2);
		//defaultComposer.setRoleAnnouncement(60 * 20);
		defaultComposer.setTeamSize(5);
		defaultComposer.setTeamEditable(false);

		ScenarioDiamondLimit diamondLimit = new ScenarioDiamondLimit();
		diamondLimit.setLimit(17);
		diamondLimit.setReplace(false);
		defaultComposer.getScenarios().add(diamondLimit);
	}

	public MangaComposer(TeamGameComposer gameComposer) {
		super(gameComposer);
	}

	public MangaComposer(MangaComposer other) {
		super(other);
		this.teamAnnouncement = other.teamAnnouncement;
		this.roleAnnouncement = other.roleAnnouncement;
	}

	@GUISetting(group = "timer", name = "Annonce des équipes", icon = Material.BANNER, duration = true,
			min = 60, max = 60 * 30, step = 30)
	private int teamAnnouncement;

	@GUISetting(group = "timer", name = "Annonce des rôles", icon = Material.NAME_TAG, duration = true,
			min = 60, max = 60 * 40, step = 30)
	private int roleAnnouncement;

	@Override
	public MangaComposer copy() {
		return new MangaComposer(this);
	}

}

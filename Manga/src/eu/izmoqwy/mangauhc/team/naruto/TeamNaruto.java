package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamNaruto extends MangaTeam {

	public TeamNaruto() {
		super("Naruto", Arrays.asList(
				new RoleNaruto(),
				new RoleSakura(),
				new RoleKakashi(),
				new RoleJiraya(),
				new RoleMadara()
		));
	}

}

package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamDBZ extends MangaTeam {

	public TeamDBZ() {
		super("DBZ", Arrays.asList(
				new RoleGoku(),
				new RoleVegeta(),
				new RoleKrilin(),
				new RoleGohan(),
				new RoleFreezer()
		));
	}

}

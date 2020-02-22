package eu.izmoqwy.mangauhc.team.onepunch;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamOnePunch extends MangaTeam {

	public TeamOnePunch() {
		super("One Punch", Arrays.asList(
				new RoleGenos(),
				new RoleRouletteRider(),
				new RoleSonic(),
				new RoleFubuki(),
				new RoleSaitama()
		));
	}

}

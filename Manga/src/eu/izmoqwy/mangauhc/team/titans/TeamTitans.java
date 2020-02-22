package eu.izmoqwy.mangauhc.team.titans;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamTitans extends MangaTeam {

	public TeamTitans() {
		super("Titans", Arrays.asList(
				new RoleEren(),
				new RoleReiner(),
				new RoleLivai(),
				new RoleArmine(),
				new RoleMikasa()
		));
	}

}

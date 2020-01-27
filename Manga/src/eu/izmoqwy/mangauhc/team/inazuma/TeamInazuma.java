package eu.izmoqwy.mangauhc.team.inazuma;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamInazuma extends MangaTeam {

	public TeamInazuma() {
		super("Inazuma E.", Arrays.asList(
				new RoleAxelBlaze(),
				new RoleMarkEvans(),
				new RoleNathanSwift(),
				new RoleKevinDragonfly(),
				new RoleJudeSharpe()
		));
	}

}

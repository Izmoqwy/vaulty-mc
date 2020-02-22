package eu.izmoqwy.mangauhc.team.fairy;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamFairyTail extends MangaTeam {

	public TeamFairyTail() {
		super("Fairy Tail", Arrays.asList(
				new RoleNatsu(),
				new RoleErza(),
				new RoleGrev(),
				new RoleLucy(),
				new RoleZelephNoire()
		));
	}

}

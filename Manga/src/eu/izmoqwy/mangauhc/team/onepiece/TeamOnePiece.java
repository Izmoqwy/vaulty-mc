package eu.izmoqwy.mangauhc.team.onepiece;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamOnePiece extends MangaTeam {

	public TeamOnePiece() {
		super("One Piece", Arrays.asList(
				new RoleLuffy(),
				new RoleZoro(),
				new RoleAce(),
				new RoleNami(),
				new RoleBarbeNoire()
		));
	}

}

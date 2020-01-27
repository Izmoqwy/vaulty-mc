package eu.izmoqwy.mangauhc.team.pokemon;

import eu.izmoqwy.mangauhc.team.MangaTeam;

import java.util.Arrays;

public class TeamPokemon extends MangaTeam {

	public TeamPokemon() {
		super("Pokémon", Arrays.asList(
				new RoleSacha(),
				new RolePierre(),
				new RoleOndine(),
				new RoleProfesseurChen(),
				new RoleGiovani()
		));
	}

}

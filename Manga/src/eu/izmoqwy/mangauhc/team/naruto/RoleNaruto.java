package eu.izmoqwy.mangauhc.team.naruto;

import eu.izmoqwy.mangauhc.team.MangaRole;

public class RoleNaruto extends MangaRole {

	public RoleNaruto() {
		super("Naruto", createDescription(
				"Vous êtes Naruto, vous devez gagner avec votre équipe actuelle, pour ce faire vous obtenez 1 coeurs en plus.",
				"Si Madara meurt vous gagnez Speed II mais aussi Weakness I."
		));
	}

}

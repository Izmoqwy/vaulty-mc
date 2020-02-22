package eu.izmoqwy.mangauhc.team.titans;

import eu.izmoqwy.mangauhc.team.MangaRole;

public class RoleEren extends MangaRole {

	public RoleEren() {
		super("Eren", createDescription(
			"Vous êtes Eren vous devez gagner avec votre équipe actuelle, pour ce faire la commande ‘/mg titan’ vous permet de vous transformer pendant une durée de 5 minutes " +
					"vous offrant ainsi Force I, Résistance I mais Slowness I a la fin de votre transformation."
		));
	}

}

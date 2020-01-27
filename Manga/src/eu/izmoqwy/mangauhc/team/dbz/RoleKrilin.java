package eu.izmoqwy.mangauhc.team.dbz;

import eu.izmoqwy.mangauhc.team.MangaRole;
import lombok.Getter;
import lombok.Setter;

@Getter
public class RoleKrilin extends MangaRole {

	@Setter
	private boolean revived;

	public RoleKrilin() {
		super("Krilin", createDescription(
				"Vous êtes Krilin vous devez gagner avec votre équipe actuelle, pour ce faire vous pourrez vous ressusciter une seule fois mais vous obtiendrez Weakness I."
		));
	}

}

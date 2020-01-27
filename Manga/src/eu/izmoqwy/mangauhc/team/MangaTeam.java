package eu.izmoqwy.mangauhc.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Objects;

@Getter
public class MangaTeam {

	@Setter
	private ChatColor color;

	private final String name;
	private final List<MangaRole> roles;

	public MangaTeam(String name, List<MangaRole> roles) {
		this.name = name;
		this.roles = roles;

		roles.forEach(role -> role.setParent(this));
	}

	public String getDisplayName() {
		return color != null ? color + name : name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MangaTeam)) return false;
		MangaTeam mangaTeam = (MangaTeam) o;
		return color == mangaTeam.color &&
				Objects.equals(name, mangaTeam.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, name);
	}

}

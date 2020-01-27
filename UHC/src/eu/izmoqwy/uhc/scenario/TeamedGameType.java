package eu.izmoqwy.uhc.scenario;

import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.TeamHook;
import eu.izmoqwy.uhc.game.obj.UHCTeam;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

public abstract class TeamedGameType extends GameType implements TeamHook {

	@Getter(AccessLevel.PROTECTED)
	private List<UHCTeam> teams;

	public TeamedGameType(String name, String description, Material icon, GameComposer defaultComposer, Class<? extends GameComposer> defaultComposerClass) {
		super(name, description, icon, defaultComposer, defaultComposerClass);
	}

}

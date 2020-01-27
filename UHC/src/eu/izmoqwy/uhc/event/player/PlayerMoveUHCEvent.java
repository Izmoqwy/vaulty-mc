package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@Getter
public class PlayerMoveUHCEvent extends CancellableEvent {

	private Player player;
	private PlayerMoveEvent bukkitEvent;

	public PlayerMoveUHCEvent(PlayerMoveEvent bukkitEvent) {
		super(bukkitEvent.isCancelled());
		this.player = bukkitEvent.getPlayer();
		this.bukkitEvent = bukkitEvent;
	}

}

package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

@Getter
public class PlayerInteractUHCEvent extends CancellableEvent {

	private Player player;
	private PlayerInteractEvent bukkitEvent;

	public PlayerInteractUHCEvent(PlayerInteractEvent bukkitEvent) {
		super(bukkitEvent.isCancelled());
		this.player = bukkitEvent.getPlayer();
		this.bukkitEvent = bukkitEvent;
	}

}

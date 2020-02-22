package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;

@Getter
public class PlayerInteractEntityUHCEvent extends CancellableEvent {

	private Player player;
	private PlayerInteractEntityEvent bukkitEvent;

	public PlayerInteractEntityUHCEvent(PlayerInteractEntityEvent bukkitEvent) {
		super(bukkitEvent.isCancelled());
		this.player = bukkitEvent.getPlayer();
		this.bukkitEvent = bukkitEvent;
	}

}

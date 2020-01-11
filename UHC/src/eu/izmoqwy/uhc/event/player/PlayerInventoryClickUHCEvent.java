package eu.izmoqwy.uhc.event.player;

import eu.izmoqwy.uhc.event.CancellableEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

@Getter
public class PlayerInventoryClickUHCEvent extends CancellableEvent {

	private Player player;
	private InventoryClickEvent bukkitEvent;

	public PlayerInventoryClickUHCEvent(InventoryClickEvent bukkitEvent) {
		super(bukkitEvent.isCancelled());
		this.bukkitEvent = bukkitEvent;
		this.player = (Player) bukkitEvent.getWhoClicked();
	}
}

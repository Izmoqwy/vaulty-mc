/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event;

import lombok.Getter;
import lombok.Setter;

public abstract class CancellableEvent extends UHCEvent {

	@Getter @Setter
	private boolean cancelled;

	public CancellableEvent(boolean cancelled) {
		this.cancelled = cancelled;
	}

}

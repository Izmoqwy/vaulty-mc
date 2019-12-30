/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.event.registration;

import lombok.Getter;

import java.lang.reflect.Method;

@Getter
class RegisteredListener {

	private UHCListener instance;
	private Method method;
	private UHCEventHandler eventHandler;

	public RegisteredListener(UHCListener instance, Method method, UHCEventHandler eventHandler) {
		this.instance = instance;
		this.method = method;
		this.eventHandler = eventHandler;
	}

	public int getPriority() {
		return eventHandler.priority().ordinal();
	}
}

/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.wwuhc.game;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import java.lang.reflect.Field;

public class WWTabListManager {

	private PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();

	public WWTabListManager() {
		// display name
		// prefix
		// suffix
		// pack option 1
		// visibility always


		// members g
		//prefix c
		//suffix d
		//teamname a
		//paramint h
		//packoption i
		//disaplayname b
		//visibility e


	}

	private void set(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
		Field field = packet.getClass().getField(fieldName);
		field.setAccessible(true);
		field.set(packet, value);
	}

}

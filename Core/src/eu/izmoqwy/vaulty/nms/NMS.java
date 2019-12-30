/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.nms;

import eu.izmoqwy.vaulty.nms.global.Global_v1_8_R3;
import eu.izmoqwy.vaulty.nms.global.NMSGlobal;
import eu.izmoqwy.vaulty.nms.packets.NMSPackets;
import eu.izmoqwy.vaulty.nms.packets.Packets_v1_8_R3;
import org.bukkit.Bukkit;

public class NMS {

	public static NMSPackets packets;
	public static NMSGlobal global;

	static {
		String nmsVersion = getNMSVersion();
		if (nmsVersion.equals("v1_8_R3")) {
			packets = new Packets_v1_8_R3();
			global = new Global_v1_8_R3();
		}
		else {
			System.err.println("Version NMS incorrecte, demandez à Izmoqwy de rajouter cette version.");
			Bukkit.shutdown();
		}
	}

	private static String getNMSVersion() {
		String fullName = Bukkit.getServer().getClass().getPackage().getName();
		return fullName.substring(fullName.lastIndexOf('.') + 1);
	}

}

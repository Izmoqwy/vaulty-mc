/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.nms.global;

import org.bukkit.entity.LivingEntity;

public interface NMSGlobal {

	int replaceBiome(String from, String to) throws NoSuchFieldException, IllegalAccessException;
	void setBiome(int id, String biome) throws NoSuchFieldException, IllegalAccessException;

	void removeAI(LivingEntity entity);

}

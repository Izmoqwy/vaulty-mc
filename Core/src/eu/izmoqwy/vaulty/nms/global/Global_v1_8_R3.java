/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.nms.global;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.util.Map;

public class Global_v1_8_R3 implements NMSGlobal {

	private Map<String, BiomeBase> cachedBiome = Maps.newHashMap();

	@Override
	public int replaceBiome(String from, String to) throws NoSuchFieldException, IllegalAccessException {
		Preconditions.checkNotNull(from);
		Preconditions.checkNotNull(to);

		BiomeBase fromBiome = getBiome(from), toBiome = getBiome(to);
		if (fromBiome == null)
			return -1;

		Preconditions.checkNotNull(toBiome);

		Field biomesField = BiomeBase.class.getDeclaredField("biomes");
		biomesField.setAccessible(true);

		Object obj = biomesField.get(null);
		if (obj instanceof BiomeBase[]) {
			BiomeBase[] biomes = (BiomeBase[]) obj;
			biomes[fromBiome.id] = toBiome;
			return fromBiome.id;
		}

		return -1;
	}

	@Override
	public void setBiome(int id, String biome) throws NoSuchFieldException, IllegalAccessException {
		BiomeBase toBiome = getBiome(biome);
		Preconditions.checkNotNull(toBiome);

		Field biomesField = BiomeBase.class.getDeclaredField("biomes");
		biomesField.setAccessible(true);

		Object obj = biomesField.get(null);
		if (obj instanceof BiomeBase[]) {
			BiomeBase[] biomes = (BiomeBase[]) obj;
			biomes[id] = toBiome;
		}
	}

	@Override
	public void removeAI(LivingEntity entity) {
		Entity nmsEn = ((CraftEntity) entity).getHandle();
		NBTTagCompound compound = new NBTTagCompound();
		nmsEn.c(compound);
		compound.setByte("NoAI", (byte) 1);
		nmsEn.f(compound);
	}

	private BiomeBase getBiome(String name) {
		name = name.replace('_', ' ');
		if (cachedBiome.containsKey(name))
			return cachedBiome.get(name);

		for (BiomeBase biome : BiomeBase.getBiomes()) {
			if (biome != null && biome.ah != null && biome.ah.equalsIgnoreCase(name)) {
				cachedBiome.put(name, biome);
				return biome;
			}
		}
		return null;
	}

}

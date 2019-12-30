/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import org.bukkit.Location;
import org.bukkit.util.NumberConversions;

import java.util.AbstractMap;
import java.util.Random;

public class MathUtil {

	private static final Random random = new Random();

	public static AbstractMap.SimpleEntry<Integer, Integer> getPointOnCircle(int radius, int currentPoint, int totalPoints) {
		double theta = Math.PI * 2 / totalPoints;
		double angle = theta * currentPoint;
		return new AbstractMap.SimpleEntry<>((int) (radius * Math.cos(angle)), (int) (radius * Math.sin(angle)));
	}

	public static int getRandomInRange(int start, int end) {
		return start + random.nextInt(end - start + 1);
	}

	public static double getDistanceXZ(Location from, Location to) {
		return Math.sqrt(NumberConversions.square(from.getX() - to.getX()) + NumberConversions.square(from.getZ() - to.getZ()));
	}

	public static double getDistanceXZ(double fromX, double fromZ, double toX, double toZ) {
		return Math.sqrt(NumberConversions.square(fromX - toX) + NumberConversions.square(fromZ - toZ));
	}

	public static double getDistanceY(double fromY, double toY) {
		return Math.sqrt(NumberConversions.square(fromY - toY));
	}

}

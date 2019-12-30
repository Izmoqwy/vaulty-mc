/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import org.apache.commons.lang.StringUtils;

import java.text.NumberFormat;
import java.text.ParseException;

public class TimeUtil {

	public static String fromSeconds(int seconds) {
		int hours = (int) Math.floor(seconds / 3600d);
		seconds %= 3600d;
		int minutes = (int) Math.floor(seconds / 60d);
		seconds %= 60d;

		if (hours > 0) {
			return hours + ":" + zfill(minutes) + ":" + zfill(seconds);
		}
		return zfill(minutes) + ":" + zfill(seconds);
	}

	private static String zfill(int i) {
		return StringUtils.leftPad(String.valueOf(i), 2, '0');
	}

	public static long millisTime(String toParse) throws ParseException {
		int time = NumberFormat.getInstance().parse(toParse).intValue();

		int multiplier;
		if (toParse.endsWith("s") || toParse.endsWith("sec"))
			multiplier = 1;
		else if (toParse.endsWith("m") || toParse.endsWith("min"))
			multiplier = 60;
		else if (toParse.endsWith("h") || toParse.endsWith("hour"))
			multiplier = 3600;
		else if (toParse.endsWith("d") || toParse.endsWith("day"))
			multiplier = 86400;
		else if (toParse.endsWith("w") || toParse.endsWith("week"))
			multiplier = 604800;
		else
			return -1;

		return time * multiplier * 1000;
	}

}

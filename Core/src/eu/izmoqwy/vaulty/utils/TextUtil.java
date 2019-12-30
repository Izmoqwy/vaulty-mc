/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.utils;

import org.bukkit.ChatColor;

import java.util.Arrays;

public class TextUtil {

	public static ChatColor getLastColor(String s, ChatColor def) {
		int lastColorCharIndex = s.lastIndexOf(ChatColor.COLOR_CHAR);
		if (lastColorCharIndex != -1 && lastColorCharIndex + 1 < s.length())
			return ChatColor.getByChar(s.charAt(lastColorCharIndex + 1));
		return def;
	}

	public static String getFinalArg(String[] args, int from) {
		if (from >= args.length)
			return null;
		return String.join(" ", Arrays.copyOfRange(args, from, args.length));
	}

}

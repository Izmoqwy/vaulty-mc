/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package test;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;

import java.util.List;
import java.util.Scanner;

public class ScoreboardFormattingTests {

	public static void main(String[] args) {
		animation(ChatColor.GREEN, ChatColor.GRAY);
	}

	private static void colorCorrectSplit() {
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String value = scanner.nextLine();
			if (value.length() <= 16)
				continue;

			String initialFirstPart = value.substring(0, 16), initialSecondPart = value.substring(16);
			String firstPart = null, secondPart = null;

			int lastColorCharIndex = initialFirstPart.lastIndexOf('§');
			if (lastColorCharIndex + 1 == initialFirstPart.length()) {
				firstPart = initialFirstPart.substring(0, lastColorCharIndex);
				secondPart = '§' + initialSecondPart;
			}
			else if (lastColorCharIndex != -1) {
				ChatColor lastColor = ChatColor.getByChar(initialFirstPart.charAt(lastColorCharIndex + 1));
				if (lastColor != null) {
					secondPart = lastColor + initialSecondPart;
				}
			}

			System.out.println(firstPart != null ? firstPart : initialFirstPart);
			System.out.println(secondPart != null ? secondPart : initialSecondPart);
		}
	}

	private static String at(String s, int index) {
		if (index < 0 || index >= s.length())
			return null;
		return String.valueOf(s.charAt(index));
	}

	private static void animation(ChatColor animationColor, ChatColor fadeColor) {
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			String animationText = scanner.nextLine();
			List<String> completeAnimation = Lists.newArrayList();

			int index = -2, length = animationText.length();
			while (index++ < length) {
				String previous = at(animationText, index - 1), current = at(animationText, index), next = at(animationText, index + 1);
				System.out.println(previous + " " + current + " " + next);

				int replacementStart = index, replacementEnd = index;
				StringBuilder replacement = new StringBuilder();
				if (previous != null && fadeColor != null) {
					replacement.append(fadeColor).append(previous);
					replacementStart--;
				}
				if (current != null && animationColor != null) {
					replacement.append(animationColor).append(current);
				}
				else {
					if (replacement.length() == 0)
						replacementStart++;
					else
						replacementStart--;
				}
				if (next != null && fadeColor != null) {
					replacement.append(fadeColor).append(next);
					replacementEnd++;
					if (index + 1 < length)
						replacement.append(ChatColor.RESET);
				}

				completeAnimation.add(animationText.substring(0, replacementStart) + " " + replacement.toString() + " " + animationText.substring(replacementEnd));
			}

			for (String line : completeAnimation) {
				System.out.println(line);
			}
		}
	}

}

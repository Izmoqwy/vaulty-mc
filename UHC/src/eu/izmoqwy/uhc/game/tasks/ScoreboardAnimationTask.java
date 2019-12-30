/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.tasks;

import com.google.common.collect.Lists;
import eu.izmoqwy.vaulty.scoreboard.VaultyScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class ScoreboardAnimationTask extends BukkitRunnable {

	private VaultyScoreboard scoreboard;
	private int line;

	private String animationText;
	private ChatColor animationColor, fadeColor;
	private String[] animationArray;

	public ScoreboardAnimationTask(VaultyScoreboard scoreboard, int line, String animationText, ChatColor animationColor, ChatColor fadeColor) {
		this.scoreboard = scoreboard;
		this.line = line;
		this.animationColor = animationColor;
		this.fadeColor = fadeColor;
		setAnimationText(animationText);
	}

	public void setAnimationText(String text) {
		animationText = text;

		List<String> completeAnimation = Lists.newArrayList();

		int index = -2, length = animationText.length();
		while (index++ < length) {
			String previous = at(animationText, index - 1), current = at(animationText, index), next = at(animationText, index + 1);

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
			}
			if (next != null && fadeColor != null) {
				replacement.append(fadeColor).append(next);
				replacementEnd++;
				if (index + 1 < length)
					replacement.append(ChatColor.RESET);
			}

			replacementEnd += 1;
			if (replacementEnd < length)
				completeAnimation.add(animationText.substring(0, replacementStart) + replacement.toString() + animationText.substring(replacementEnd));
			else
				completeAnimation.add(animationText.substring(0, replacementStart) + replacement.toString());
		}
		animationArray = completeAnimation.toArray(new String[0]);
	}

	private static String at(String s, int index) {
		if (index < 0 || index >= s.length())
			return null;
		return String.valueOf(s.charAt(index));
	}

	private int animationBuffer, animationIndex;

	@Override
	public void run() {
		if (animationBuffer == 0) {
			if (++animationIndex == animationArray.length) {
				animationBuffer = 35;
				animationIndex = 0;
			}
			else
				scoreboard.setLine(line, animationArray[animationIndex]);
		}

		if (animationBuffer > 0) {
			scoreboard.setLine(line, animationText);
			animationBuffer--;
		}
	}

	public void start(JavaPlugin plugin) {
		runTaskTimer(plugin, 0, 2);
	}
}

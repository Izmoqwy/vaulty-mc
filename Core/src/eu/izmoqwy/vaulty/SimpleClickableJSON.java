/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Getter
public class SimpleClickableJSON {

	private String prefix, text, suffix;
	private String command, hover;

	public SimpleClickableJSON(String prefix, String text, String suffix, String command, String hover) {
		this.prefix = prefix;
		this.text = text;
		this.suffix = suffix;
		this.command = command;
		this.hover = hover;
	}

	public void send(Player player) {
		player.spigot().sendMessage(getComponents());
	}

	private BaseComponent[] getComponents() {
		List<BaseComponent> components = Lists.newLinkedList();
		components.addAll(Arrays.asList(TextComponent.fromLegacyText(prefix)));

		BaseComponent[] textComponents = TextComponent.fromLegacyText(text);
		if (command != null) {
			ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, command);
			for (BaseComponent textComponent : textComponents) {
				textComponent.setClickEvent(clickEvent);
			}
		}
		if (hover != null) {
			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(hover));
			for (BaseComponent textComponent : textComponents) {
				textComponent.setHoverEvent(hoverEvent);
			}
		}
		components.addAll(Arrays.asList(textComponents));

		components.addAll(Arrays.asList(TextComponent.fromLegacyText(suffix)));
		return components.toArray(new BaseComponent[0]);
	}

}

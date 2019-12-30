/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.vaulty.nms.packets;

import eu.izmoqwy.vaulty.utils.ReflectionUtil;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;

public class Packets_v1_8_R3 implements NMSPackets {

	@Override
	public void sendTitle(Player player, String title, String subTitle) {
		if (title != null)
			sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, strToComponent(title)));
		if (subTitle != null)
			sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, strToComponent(subTitle)));
	}

	@Override
	public void sendTimings(Player player, int ticks, int fadeIn, int fadeOut) {
		sendPacket(player, new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, ticks, fadeOut));
	}

	@Override
	public void forceUpdateReducedDebugInfo(Player player, boolean value) {
		sendPacket(player, new PacketPlayOutEntityStatus(toCraftPlayer(player).getHandle(), (byte) (value ? 22 : 23)));
	}

	@Override
	public void sendTabList(String header, String footer) {
		sendPacket(Bukkit.getOnlinePlayers(), getTabListPacket(header, footer));
	}

	@Override
	public void sendActionBar(Player player, String text) {
		sendPacket(player, new PacketPlayOutChat(strToComponent(text), (byte) 2));
	}

	@Override
	public void openAnvil(Player player, String name) {
		EntityPlayer entityPlayer = toCraftPlayer(player).getHandle();

		int containerId = entityPlayer.nextContainerCounter();
		entityPlayer.playerConnection.sendPacket(new PacketPlayOutOpenWindow(containerId, "minecraft:anvil", new ChatMessage(name != null ? name : Blocks.ANVIL.a() + ".name"), 0));
		entityPlayer.activeContainer = new AnvilContainer(entityPlayer);
		entityPlayer.activeContainer.windowId = containerId;
	}

	private PacketPlayOutPlayerListHeaderFooter getTabListPacket(String header, String footer) {
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

		try {
			if (header != null) {
				Field headerField = ReflectionUtil.getField(packet.getClass(), "a");
				Objects.requireNonNull(headerField).set(packet, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + header + "\"}"));
				headerField.setAccessible(!headerField.isAccessible());
			}
			if (footer != null) {
				Field footerField = ReflectionUtil.getField(packet.getClass(), "b");
				Objects.requireNonNull(footerField).set(packet, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}"));
				footerField.setAccessible(!footerField.isAccessible());
			}
		}
		catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return packet;
	}

	private IChatBaseComponent strToComponent(String text) {
		return IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + text + "\"}");
	}

	private void sendPacket(Player player, Packet<?> packet) {
		toCraftPlayer(player).getHandle().playerConnection.sendPacket(packet);
	}

	private void sendPacket(Collection<? extends Player> players, Packet<?> packet) {
		players.forEach(player -> sendPacket(player, packet));
	}

	private CraftPlayer toCraftPlayer(Player player) {
		return (CraftPlayer) player;
	}

	private static class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}

		@Override
		public boolean a(EntityHuman entityhuman) {
			return true;
		}

	}

}

package eu.izmoqwy.vaulty;

import eu.izmoqwy.vaulty.gui.InventoryListener;
import eu.izmoqwy.vaulty.oss.OSS;
import eu.izmoqwy.vaulty.oss.OSSBukkitListener;
import eu.izmoqwy.vaulty.utils.ServerUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultyCore extends JavaPlugin {

	public static final String PREFIX = "§a§lVaulty §f§m═§r ";
	public static final boolean DEBUG = false;

	@Getter
	private static VaultyCore instance;

	@Override
	public void onEnable() {
		instance = this;
		ServerUtil.registerListeners(this, new OSSBukkitListener(), new InventoryListener());

		for (Player player : Bukkit.getOnlinePlayers()) {
			OSS.getMainServer().onJoin(player, false);
		}
	}
}

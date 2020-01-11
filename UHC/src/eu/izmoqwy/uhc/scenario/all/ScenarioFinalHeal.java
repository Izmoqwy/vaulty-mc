/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.scenario.all;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.VaultyUHC;
import eu.izmoqwy.uhc.config.GUIConfigurable;
import eu.izmoqwy.uhc.config.GUISetting;
import eu.izmoqwy.uhc.event.player.PlayerReconnectUHCEvent;
import eu.izmoqwy.uhc.event.registration.UHCEventHandler;
import eu.izmoqwy.uhc.event.registration.UHCEventPriority;
import eu.izmoqwy.uhc.event.registration.UHCListener;
import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.scenario.Scenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public class ScenarioFinalHeal extends Scenario implements UHCListener, GUIConfigurable {

	public ScenarioFinalHeal() {
		super("FinalHeal", "Régénérer la vie de tous les joueurs", Material.GOLDEN_APPLE);
	}

	@GUISetting(name = "Temps", icon = Material.GOLDEN_APPLE, duration = true,
			min = 60, max = 60 * 45, step = 30)
	private int healDuration = 60 * 20;

	private List<UUID> offlineDuringHealing = Lists.newArrayList();

	@Override
	public void onStartGame(GameManager gameManager) {
		offlineDuringHealing.clear();

		Bukkit.getScheduler().runTaskLater(VaultyUHC.getInstance(), () -> {
			if (gameManager.getCurrentGame() == null || !gameManager.getCurrentComposer().getGameTitle().equals(GameManager.get.getCurrentComposer().getGameTitle()))
				return;
			gameManager.getCurrentGame().getOnlinePlayers().forEach(player -> player.setHealth(player.getMaxHealth()));
			gameManager.getCurrentGame().broadcast("§6Tous les joueurs ont été soignés");
			offlineDuringHealing.addAll(gameManager.getCurrentGame().getGhosts().keySet());
		}, healDuration * 20 + 5);
	}

	@UHCEventHandler(priority = UHCEventPriority.SCENARIO)
	public void onReconnect(PlayerReconnectUHCEvent event) {
		if (offlineDuringHealing.remove(event.getPlayer().getUniqueId()))
			event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
	}
}

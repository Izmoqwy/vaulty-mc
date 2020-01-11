/*
 * Copyright (c) Izmoqwy 2019.
 * Plugin réalisé pour le serveur Minecraft Vaulty.
 */

package eu.izmoqwy.uhc.game.tasks;

import com.google.common.collect.Lists;
import eu.izmoqwy.uhc.game.GameComposer;
import eu.izmoqwy.uhc.game.UHCGame;
import eu.izmoqwy.uhc.world.UHCWorldManager;
import eu.izmoqwy.vaulty.Procedure;
import eu.izmoqwy.vaulty.scoreboard.VaultyScoreboard;
import eu.izmoqwy.vaulty.utils.TimeUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class GameLoop extends BukkitRunnable {

	@Getter
	private UHCGame game;
	private GameComposer gameComposer;

	private World world;
	private WorldBorder worldBorder;

	private VaultyScoreboard scoreboard;
	@Getter
	private VaultyScoreboard currentScoreboard;

	@Getter
	private List<VaultyScoreboard> alternateScoreboard = Lists.newArrayList();
	@Setter
	private int alternateScoreboardTimer = 60, alternateScoreboardDuration = 10;

	@Setter
	private Procedure extra;

	public GameLoop(UHCGame uhcGame) {
		this.game = uhcGame;
		game.setInvincibility(true);
		gameComposer = game.getGameComposer();

		world = UHCWorldManager.getUhcWorld();
		world.setPVP(false);
		worldBorder = world.getWorldBorder();
		worldBorder.setCenter(0, 0);
		worldBorder.setSize(gameComposer.getBorderInitialSize());

		scoreboard = new VaultyScoreboard("uhc", gameComposer.getGameTitle());
		scoreboard.reset(11);
		scoreboard.setLine(0, " ");
		scoreboard.setLine(1, "§e§lJoueurs");
		updatePlayers();
		scoreboard.setLine(3, " ");
		scoreboard.setLine(4, "§e§lInfos:");
		updateDuration();
		updatePvP("§cDésactivé");
		scoreboard.setLine(7, "Dégâts: ", "§cDésactivé");
		scoreboard.setLine(8, "Limites: §b" + gameComposer.getBorderInitialSize() + "§7x§b" + gameComposer.getBorderInitialSize());
		scoreboard.setLine(9, " ");
		scoreboard.setLine(10, "§e§m═§r §6§lvaul", "§6§lty.minesr.com");
		game.getOnlinePlayers().forEach(scoreboard::addPlayer);
		game.getOnlineSpectators().forEach(scoreboard::addPlayer);
		currentScoreboard = scoreboard;
	}

	private void updatePlayers() {
		scoreboard.setLine(2, "Restants: ", "§a" + game.getOnlinePlayers().size());
	}

	private void updateDuration() {
		scoreboard.setLine(5, "Durée: ", "§e" + TimeUtil.fromSeconds(elapsedTime));
	}

	private void updatePvP(String value) {
		scoreboard.setLine(6, "PvP: ", value);
	}

	private boolean displayBorder = false;
	@Getter
	private int elapsedTime;

	private double lastBorderSize;

	@Override
	public void run() {
		updatePlayers();
		if (game.isTeleporting())
			return;

		if (!alternateScoreboard.isEmpty()) {
			VaultyScoreboard newSb = null;
			if (currentScoreboard == scoreboard && elapsedTime % alternateScoreboardTimer == 0) {
				newSb = alternateScoreboard.get(0);
			}
			else if (currentScoreboard != scoreboard && elapsedTime % alternateScoreboardDuration == 0) {
				int index = alternateScoreboard.indexOf(currentScoreboard) + 1;
				newSb = index >= alternateScoreboard.size() ? scoreboard : alternateScoreboard.get(index);
			}

			if (newSb != null) {
				game.getOnlinePlayers().forEach(currentScoreboard::removePlayer);
				game.getOnlineSpectators().forEach(currentScoreboard::removePlayer);
				currentScoreboard = newSb;
				game.getOnlinePlayers().forEach(currentScoreboard::addPlayer);
				game.getOnlineSpectators().forEach(currentScoreboard::addPlayer);
			}
		}


		updateDuration();
		if (gameComposer.getInvincibilityStopsAt() >= elapsedTime) {
			int invincibilityExpires = gameComposer.getInvincibilityStopsAt() - elapsedTime;
			if (invincibilityExpires <= 0) {
				game.broadcast("§6Les dégâts sont désormais actifs !");
				game.setInvincibility(false);
				displayBorder = true;
			}
			else {
				scoreboard.setLine(7, "Dégâts: ", "§a" + TimeUtil.fromSeconds(invincibilityExpires));
			}
		}

		if (gameComposer.getPvpStartsAt() > elapsedTime) {
			int pvpStarts = gameComposer.getPvpStartsAt() - elapsedTime;
			updatePvP("§a" + TimeUtil.fromSeconds(pvpStarts));
			if (pvpStarts == 30 || pvpStarts == 15 || pvpStarts == 10 || pvpStarts <= 3) {
				game.broadcast("§eActivation du PvP dans §6" + pvpStarts + "§e secondes.");
			}
		}
		else {
			if (gameComposer.getPvpStartsAt() == elapsedTime) {
				world.setPVP(true);
				game.broadcast("§6Le PvP est désormais actif !");
				updatePvP("§aActivé");
			}
		}

		if (displayBorder) {
			if (elapsedTime <= gameComposer.getBorderShrinksAt()) {
				int borderShrinks = gameComposer.getBorderShrinksAt() - elapsedTime;
				if (borderShrinks <= 0) {
					scoreboard.setLine(7, "Bordure: ", "§aMouvement");
					game.broadcast("§6La bordure est en mouvement !");
				}
				else {
					if (borderShrinks == 30 || borderShrinks == 15 || borderShrinks == 10 || borderShrinks <= 3) {
						game.broadcast("§eBordure en mouvement  dans §6" + borderShrinks + "§e secondes.");
					}
					scoreboard.setLine(7, "Bordure: ", "§b" + TimeUtil.fromSeconds(borderShrinks));
				}
			}
			else if (worldBorder.getSize() != lastBorderSize) {
				lastBorderSize = worldBorder.getSize();
				int size = (int) Math.round(lastBorderSize);
				scoreboard.setLine(8, "Limites: §b" + size + "§7x§b" + size);
			}
			else {
				displayBorder = false;
				scoreboard.setLine(7, "Bordure: ", "§aArrêtée");
				game.broadcast("§aLa bordure a atteint sa taille finale !");
			}
		}

		if (elapsedTime == gameComposer.getBorderShrinksAt()) {
			long borderTime = (long) Math.ceil((gameComposer.getBorderInitialSize() - gameComposer.getBorderFinalSize()) / gameComposer.getBorderSpeed());
			worldBorder.setSize(gameComposer.getBorderFinalSize(), borderTime);
		}

		if (extra != null)
			extra.invoke();

		this.elapsedTime++;
	}

	public void destroyScoreboards() {
		scoreboard.destroy();
		alternateScoreboard.forEach(VaultyScoreboard::destroy);
	}

	@Override
	public synchronized void cancel() throws IllegalStateException {
		super.cancel();
	}
}

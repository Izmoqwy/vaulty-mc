package eu.izmoqwy.uhc.gui.composer;

import eu.izmoqwy.uhc.game.GameManager;
import eu.izmoqwy.uhc.game.GameState;
import eu.izmoqwy.uhc.game.PreMadeTeam;
import eu.izmoqwy.uhc.game.TeamGameComposer;
import eu.izmoqwy.uhc.game.obj.WaitingRoom;
import eu.izmoqwy.vaulty.builder.ItemBuilder;
import eu.izmoqwy.vaulty.gui.GUIListener;
import eu.izmoqwy.vaulty.gui.VaultyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TeamGUI extends VaultyInventory implements GUIListener {

	private TeamGameComposer gameComposer;
	private WaitingRoom waitingRoom;

	public TeamGUI(TeamGameComposer gameComposer, WaitingRoom waitingRoom) {
		super(null, "§e§lÉquipes", true);
		this.gameComposer = gameComposer;
		this.waitingRoom = waitingRoom;

		update(false);
		addListener(this);
	}

	public void update(boolean clear) {
		if (clear) {
			setRows(1);
			getSlots().clear();
		}

		int slot = 0;
		for (Map.Entry<PreMadeTeam, List<OfflinePlayer>> entry : waitingRoom.getTeams().entrySet()) {
			if ((slot + 1) >= getRows() * 9)
				setRows(getRows() + 1);

			updateSlot(slot++, entry.getKey(), entry.getValue());
		}
	}

	public void updateSlot(int slot, PreMadeTeam team, List<OfflinePlayer> players) {
		ItemBuilder itemBuilder = new ItemBuilder(Material.BANNER)
				.name(team.getColor() + team.getName())
				.dyeColor(team.getDyeColor());

		for (int i = 0; i < gameComposer.getTeamSize(); i++) {
			if (players.size() > i)
				itemBuilder.appendLore(team.getColor() + players.get(i).getName());
			else
				itemBuilder.appendLore(ChatColor.GRAY + "Disponible");
		}

		setItem(slot, itemBuilder.toItemStack());
	}

	public int getIndex(PreMadeTeam team) {
		Iterator<PreMadeTeam> itTeams = waitingRoom.getTeams().keySet().iterator();
		for (int i = 0; itTeams.hasNext(); i++) {
			if (team == itTeams.next())
				return i;
		}
		return -1;
	}

	@Override
	public void onClick(Player player, ItemStack clickedItem, int slot) {
		if (clickedItem.getType() == Material.AIR)
			return;

		if (!waitingRoom.getPlayers().contains(player))
			return;
		if (GameManager.get.getGameState() != GameState.COMPOSING)
			return;

		PreMadeTeam team = waitingRoom.getTeam(slot);
		List<OfflinePlayer> players = waitingRoom.getTeams().get(team);

		if (players.size() >= gameComposer.getTeamSize() || players.contains(player))
			return;

		waitingRoom.removePlayerFromTeamGUI(player);
		waitingRoom.getTeams().get(team).add(player);
		updateSlot(slot, team, waitingRoom.getTeams().get(team));
	}

}

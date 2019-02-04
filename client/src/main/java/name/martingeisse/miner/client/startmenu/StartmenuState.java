package name.martingeisse.miner.client.startmenu;

import com.google.common.collect.ImmutableList;
import name.martingeisse.miner.common.Faction;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;

/**
 *
 */
public final class StartmenuState {

	public static final StartmenuState INSTANCE = new StartmenuState();

	private Faction newPlayerFaction;
	private ImmutableList<LoginResponse.Element> players;
	private LoginResponse.Element selectedPlayer;

	private StartmenuState() {
	}

	public Faction getNewPlayerFaction() {
		return newPlayerFaction;
	}

	public void setNewPlayerFaction(Faction newPlayerFaction) {
		this.newPlayerFaction = newPlayerFaction;
	}

	public ImmutableList<LoginResponse.Element> getPlayers() {
		return players;
	}

	public void setPlayers(ImmutableList<LoginResponse.Element> players) {
		this.players = players;
	}

	public LoginResponse.Element getSelectedPlayer() {
		return selectedPlayer;
	}

	public void setSelectedPlayer(LoginResponse.Element selectedPlayer) {
		this.selectedPlayer = selectedPlayer;
	}

}

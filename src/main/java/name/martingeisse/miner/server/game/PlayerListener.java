package name.martingeisse.miner.server.game;

/**
 *
 */
public interface PlayerListener {

	void onCoinsChanged();

	void onFlashMessage(String message);

	void onInventoryChanged();

}

package name.martingeisse.miner;

/**
 *
 */
public class Main {

	public static void main(String[] args) throws Exception {
		name.martingeisse.miner.server.Main.main(new String[0]);
		name.martingeisse.miner.server.Main.startupFinishedLatch.await();
		name.martingeisse.miner.client.Main.main(new String[0]);
	}

}

package name.martingeisse.miner;

import name.martingeisse.miner.client.ClientStartup;
import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.startmenu.StartmenuNetworkClient;
import name.martingeisse.miner.client.startmenu.StartmenuState;
import name.martingeisse.miner.common.network.c2s.request.LoginRequest;
import name.martingeisse.miner.common.network.s2c.response.LoginResponse;

import java.util.concurrent.CountDownLatch;

/**
 *
 */
public class AutologinMain {

	public static void main(String[] args) throws Exception {

		CountDownLatch serverLatch = new CountDownLatch(1);
		new Thread(() -> {
			try {
				name.martingeisse.miner.server.Main.main(new String[0]);
			} catch (Exception e) {
				e.printStackTrace(System.err);
				System.exit(1);
			}
			serverLatch.countDown();
		}).start();

		Thread.currentThread().setName("Startup (later OpenGL)");
		ClientStartup startup = new ClientStartup();
		startup.parseCommandLine(args);
		startup.openWindow();
		name.martingeisse.miner.client.Main.frameLoop = startup.getFrameLoop();

		serverLatch.await();
		startup.startConnectingToServer();
		ClientEndpoint.INSTANCE.waitUntilConnected();
		ClientEndpoint.INSTANCE.setMessageConsumer(StartmenuNetworkClient.INSTANCE);
		StartmenuNetworkClient.INSTANCE.requestAndWait(new LoginRequest("martin", "foobar"), LoginResponse.class);
		// StartmenuState.INSTANCE.setSelectedPlayer(response.getElements().get(0));


		startup.createApplicationThread();
		startup.becomeGlWorkerThread();
		System.exit(0);
	}

}

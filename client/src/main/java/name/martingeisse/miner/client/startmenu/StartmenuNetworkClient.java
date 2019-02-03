/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.network.MessageConsumer;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.c2s.request.Request;
import name.martingeisse.miner.common.network.s2c.response.ErrorResponse;
import name.martingeisse.miner.common.network.s2c.response.Response;
import name.martingeisse.miner.common.util.UserVisibleMessageException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public final class StartmenuNetworkClient implements MessageConsumer {

	public static final StartmenuNetworkClient INSTANCE = new StartmenuNetworkClient();

	private boolean requestInProgress = false;
	private Class<?> expectedResponseClass = null;
	private ResponseConsumer<?> responseConsumer = null;

	public <T extends Response> void request(Request requestMessage, Class<T> expectedResponseClass, ResponseConsumer<T> responseConsumer) {
		if (requestInProgress) {
			throw new IllegalStateException("request in progress");
		}
		this.expectedResponseClass = expectedResponseClass;
		this.responseConsumer = responseConsumer;
		requestInProgress = true;
		ClientEndpoint.INSTANCE.send(requestMessage);
	}

	// TODO keep the UI responsive while the request is being executed. This method does the opposite and should be removed.
	public <T extends Response> T requestAndWait(Request requestMessage, Class<T> expectedResponseClass) {
		AtomicReference<T> responseContainer = new AtomicReference<>();
		AtomicReference<ErrorResponse> errorContainer = new AtomicReference<>();
		CountDownLatch countDownLatch = new CountDownLatch(1);
		request(requestMessage, expectedResponseClass, new ResponseConsumer<T>() {

			@Override
			public void consumeResponse(T response) {
				responseContainer.set(response);
				countDownLatch.countDown();
			}

			@Override
			public void consumeError(ErrorResponse response) {
				errorContainer.set(response);
				countDownLatch.countDown();
			}

		});
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		T response = responseContainer.get();
		if (response != null) {
			return response;
		} else {
			throw new UserVisibleMessageException(errorContainer.get().getText());
		}
	}

	@Override
	public void consume(Message message) {
		if (!requestInProgress) {
			return;
		}
		if (expectedResponseClass.isInstance(message)) {
			ResponseConsumer consumer = this.responseConsumer;
			this.requestInProgress = false;
			this.expectedResponseClass = null;
			this.responseConsumer = null;
			consumer.consumeResponse((Response)message);
		} else if (message instanceof ErrorResponse) {
			ResponseConsumer<?> consumer = this.responseConsumer;
			this.requestInProgress = false;
			this.expectedResponseClass = null;
			this.responseConsumer = null;
			consumer.consumeError((ErrorResponse) message);
		}
	}

	public interface ResponseConsumer<T extends Response> {

		void consumeResponse(T response);

		void consumeError(ErrorResponse response);

	}

}

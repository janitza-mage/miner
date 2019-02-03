/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.client.startmenu;

import name.martingeisse.miner.client.network.ClientEndpoint;
import name.martingeisse.miner.client.network.MessageConsumer;
import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.response.ErrorResponse;

/**
 *
 */
public final class StartmenuNetworkClient implements MessageConsumer {

	private boolean requestInProgress = false;
	private Class<?> expectedResponseClass = null;
	private ResponseConsumer<?> responseConsumer = null;

	public <T extends Message> void request(Message requestMessage, Class<T> expectedResponseClass, ResponseConsumer<T> responseConsumer) {
		if (requestInProgress) {
			throw new IllegalStateException("request in progress");
		}
		this.expectedResponseClass = expectedResponseClass;
		this.responseConsumer = responseConsumer;
		requestInProgress = true;
		ClientEndpoint.INSTANCE.send(requestMessage);
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
			consumer.consumeResponse(message);
		} else if (message instanceof ErrorResponse) {
			ResponseConsumer<?> consumer = this.responseConsumer;
			this.requestInProgress = false;
			this.expectedResponseClass = null;
			this.responseConsumer = null;
			consumer.consumeError((ErrorResponse) message);
		}
	}

	public interface ResponseConsumer<T extends Message> {

		void consumeResponse(T response);

		void consumeError(ErrorResponse response);

	}

}

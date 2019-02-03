/*
 * Copyright (c) 2018 Martin Geisse
 * This file is distributed under the terms of the MIT license.
 */
package name.martingeisse.miner.common.network.c2s.request;

import name.martingeisse.miner.common.network.Message;
import name.martingeisse.miner.common.network.s2c.response.ErrorResponse;
import name.martingeisse.miner.common.network.s2c.response.OkayResponse;
import name.martingeisse.miner.common.network.s2c.response.Response;

/**
 * A message that implements this interface must be responded to with a {@link Response} message. Any problems are
 * responded to with an {@link ErrorResponse}. If the response is not expected to transfer data beyond acknowledging
 * the request, an {@link OkayResponse} is used.
 */
public abstract class Request extends Message {
}

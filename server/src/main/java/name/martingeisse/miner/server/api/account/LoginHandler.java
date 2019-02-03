/**
 * Copyright (c) 2010 Martin Geisse
 * <p>
 * This file is distributed under the terms of the MIT license.
 */

package name.martingeisse.miner.server.api.account;

import name.martingeisse.api.handler.jsonapi.AbstractJsonApiHandler;
import name.martingeisse.api.request.ApiRequestCycle;
import name.martingeisse.common.SecurityTokenUtil;
import name.martingeisse.common.javascript.analyze.JsonAnalyzer;
import name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder;
import name.martingeisse.miner.server.MinerServerSecurityConstants;
import name.martingeisse.miner.server.game.UserAccountRepository;
import org.joda.time.Instant;

import javax.servlet.http.Cookie;

/**
 * Handles "login" requests that exchange a username/password for an account access token.
 * The account access token is returned as a field in the response (for the actual API
 * client) and also as a Set-Cookie header (for testing in the browser). Downstream API
 * actions will accept the token either as a request field or as a cookie.
 */
public final class LoginHandler extends AbstractJsonApiHandler {

	/* (non-Javadoc)
	 * @see name.martingeisse.api.handler.jsonapi.AbstractJsonApiHandler#handle(name.martingeisse.api.request.RequestCycle, name.martingeisse.common.javascript.analyze.JsonAnalyzer, name.martingeisse.common.javascript.jsonbuilder.JsonValueBuilder)
	 */
	@Override
	protected void handle(ApiRequestCycle requestCycle, JsonAnalyzer input, JsonValueBuilder<?> output) throws Exception {

		// check credentials
		String username = input.analyzeMapElement("username").expectString();
		String password = input.analyzeMapElement("password").expectString();
		UserAccountRepository.INSTANCE.login(username, password);

		// build the access token and add it to the response body and to the cookie header
		Instant expiryTime = new Instant().plus(MinerServerSecurityConstants.ACCOUNT_ACCESS_TOKEN_MAX_AGE_MILLISECONDS);
		String token = SecurityTokenUtil.createToken(username, expiryTime, MinerServerSecurityConstants.SECURITY_TOKEN_SECRET);
		output.object().property("accountAccessToken").string(token).end();
		requestCycle.getResponse().addCookie(buildCookie(token));

	}

	/**
	 *
	 */
	private static Cookie buildCookie(String token) {
		Cookie cookie = new Cookie("accountAccessToken", token);
		cookie.setMaxAge(MinerServerSecurityConstants.ACCOUNT_ACCESS_TOKEN_MAX_AGE_SECONDS);
		cookie.setPath("/");
		return cookie;
	}

}

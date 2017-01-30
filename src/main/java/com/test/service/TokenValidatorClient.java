package com.test.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;

public class TokenValidatorClient<T> //extends AbstractVerticle 
{

	private EventBus eventBus;
	
	public TokenValidatorClient(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void validateToken(String token, Handler<AsyncResult<String>> resultHandler) {
		
		System.out.println("Posting... "+token);
		eventBus.send("TOKEN-VALIDATOR", token, reply -> {
			if (reply.failed()) {
				System.out.println("fail...");
				resultHandler.handle(Future.failedFuture(reply.cause()));
			} else {
				System.out.println("succeeded...");
				resultHandler
						.handle(Future.succeededFuture(reply.result().body() == null ? null : reply.result().body().toString()));
			}
		});
	}
}
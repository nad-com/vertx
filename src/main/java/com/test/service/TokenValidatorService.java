package com.test.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;

public class TokenValidatorService extends AbstractVerticle {

	@Override
	  public void start() throws Exception {
		EventBus eb = vertx.eventBus();

		System.out.println("Deploying TokenValidatorService");
		eb.consumer("TOKEN-VALIDATOR", message -> {

			System.out.println("Received message: " + message.body());
			
			if (message.body().toString().equalsIgnoreCase("abc1234")) //Extend this
			message.reply("valid");
			else
			message.reply("invalid");	
		});
	}

}
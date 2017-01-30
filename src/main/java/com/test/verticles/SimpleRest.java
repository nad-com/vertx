package com.test.verticles;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.test.dto.StatementDTO;
import com.test.service.TokenValidatorClient;
import com.test.service.TokenValidatorService;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class SimpleRest extends AbstractVerticle {

    public static void main(String[] args) {
      runExample(SimpleRest.class);
    }

    private Map<String, JsonObject> products = new HashMap<>();
    EventBus eventBus;
    
    @Override
    public void start() {

        setUpInitialData();

        Router router = Router.router(vertx);
        System.out.println("Start");
        vertx.deployVerticle(new TokenValidatorService());

        router.route().handler(BodyHandler.create());
        router.get("/products/:productID").handler(this::handleGetProduct);
        router.put("/products/:productID").handler(this::handleAddProduct);
        router.get("/products").handler(this::handleListProducts);
        router.put("/postStatement").handler(this::handlePostStatement);
        router.get("/getStatement/:id").handler(this::handleGetStatement);
        router.get("/handleValidateToken/:token").handler(this::handleValidateToken);
        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
    
    
    private void handleValidateToken(RoutingContext routingContext) { 
    	eventBus = vertx.eventBus();
    	TokenValidatorClient client = new TokenValidatorClient(eventBus);
        String token = routingContext.request().getParam("token");
        System.out.println("token: "+token);
        
        HttpServerResponse response = routingContext.response();
        if (token == null) {
            sendError(400, response);
        } else {
        	//TokenValidatorClient client = new TokenValidatorClient();
        	System.out.println("Calling...");
        	client.validateToken(token, tokenResultHandler(routingContext));
        }
    }
    
    protected <T> Handler<AsyncResult<T>> tokenResultHandler(RoutingContext context) {
		return ar -> {
			System.out.println("In the handler...");
			if (ar.succeeded()) {
				T res = ar.result();
				if (res == null) {
					System.out.println("null...");
					context.response().setStatusCode(404).end();
				} else {
					System.out.println("received..."+res.toString());
					context.response().setStatusCode(200).setStatusMessage("reply received").end(res.toString());
				}
			} else {
				context.response().setStatusCode(404).end();
				// internalError(context, ar.cause());
				// ar.cause.printStackTrace();
			}
		};
	}
    
    private void handlePostStatement(RoutingContext routingContext) {
        
        
        HttpServerResponse response = routingContext.response();
        JsonObject statement = routingContext.getBodyAsJson();
        
        String id = statement.getString("id");
        String name = statement.getString("name");
        String balance = statement.getString("balance");
        
        if (id == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
            	StatementDTO.insertStatement(id, name, balance);
            	response.putHeader("content-type", "application/json").end("Successfully inserted the record");
                response.end();
            }
        }
    }

    private void handleGetStatement(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        HttpServerResponse response = routingContext.response();
        if (id == null) {
            sendError(400, response);
        } else {
            String statement = StatementDTO.getStatement(id);
            if (statement == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(statement);
            }
        }
    }
    
    private void handleGetProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = products.get(productID);
            if (product == null) {
                sendError(404, response);
            } else {
                response.putHeader("content-type", "application/json").end(product.encodePrettily());
            }
        }
    }

    private void handleAddProduct(RoutingContext routingContext) {
        String productID = routingContext.request().getParam("productID");
        HttpServerResponse response = routingContext.response();
        if (productID == null) {
            sendError(400, response);
        } else {
            JsonObject product = routingContext.getBodyAsJson();
            if (product == null) {
                sendError(400, response);
            } else {
                products.put(productID, product);
                response.end();
            }
        }
    }

    private void handleListProducts(RoutingContext routingContext) {
        System.out.println("routingContext" + routingContext.request().headers().toString());

        JsonArray arr = new JsonArray();
        products.forEach((k, v) -> arr.add(v));
        routingContext.response().putHeader("content-type", "application/json").end(arr.encodePrettily());
    }

    private void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }

    private void setUpInitialData() {
        addProduct(new JsonObject().put("id", "prod3568").put("name", "Egg Whisk").put("price", 3.99).put("weight", 150));
        addProduct(new JsonObject().put("id", "prod7340").put("name", "Tea Cosy").put("price", 5.99).put("weight", 100));
        addProduct(new JsonObject().put("id", "prod8643").put("name", "Spatula").put("price", 1.00).put("weight", 80));
    }

    private void addProduct(JsonObject product) {
        products.put(product.getString("id"), product);
    }


    public static void runExample(Class clazz) {
        runExample(WEB_EXAMPLES_JAVA_DIR, clazz, new VertxOptions().setClustered(false), null);
    }

    public static void runExample(String exampleDir, Class clazz, VertxOptions options, DeploymentOptions
            deploymentOptions) {
        runExample(exampleDir + clazz.getPackage().getName().replace(".", "/"), clazz.getName(), options, deploymentOptions);
    }

    private static final String WEB_EXAMPLES_JAVA_DIR = "/src/main/java/";


    public static void runExample(String exampleDir, String verticleID, VertxOptions options, DeploymentOptions deploymentOptions) {
       System.out.println("Example dir " + exampleDir);
        System.out.println("verticleID dir " + verticleID);



        if (options == null) {
            // Default parameter
            options = new VertxOptions();
        }
        // Smart cwd detection

        // Based on the current directory (.) and the desired directory (exampleDir), we try to compute the vertx.cwd
        // directory:
        try {
            // We need to use the canonical file. Without the file name is .
            File current = new File(".").getCanonicalFile();
            if (exampleDir.startsWith(current.getName()) && !exampleDir.equals(current.getName())) {
                exampleDir = exampleDir.substring(current.getName().length() + 1);
            }
        } catch (IOException e) {
            // Ignore it.
        }

        System.setProperty("vertx.cwd", "/vertx/");
        Consumer<Vertx> runner = vertx -> {
            try {
                if (deploymentOptions != null) {
                    vertx.deployVerticle(verticleID, deploymentOptions);
                } else {
                    vertx.deployVerticle(verticleID);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
        if (options.isClustered()) {
            Vertx.clusteredVertx(options, res -> {
                if (res.succeeded()) {
                    Vertx vertx = res.result();
                    runner.accept(vertx);
                } else {
                    res.cause().printStackTrace();
                }
            });
        } else {
            Vertx vertx = Vertx.vertx(options);
            runner.accept(vertx);
        }
    }


}

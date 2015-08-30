package pt.davidafsilva.shb;

/*
 * #%L
 * slack-hello-word
 * %%
 * Copyright (C) 2015 David Silva
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.eclipsesource.json.JsonObject;

import java.util.Arrays;
import java.util.Optional;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * The hello-back service for slack
 *
 * @author David Silva
 */
public class HelloVerticle extends AbstractVerticle {

  // the logger
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloVerticle.class);

  // the hello text response format
  private static final String HELLO_RESPONSE_FORMAT = "Hello, <@%s|%s> :sunglasses:";

  // the http server
  private HttpServer server;

  @Override
  public void start(final Future<Void> startFuture) throws Exception {
    // create the routing configuration
    final Router router = Router.router(vertx);

    // default handler
    router.route().handler(BodyHandler.create());
    // POST /hello
    router.post("/hello")
        .produces("application/json")
        .handler(this::helloRequest);

    // validate the configuration
    if (!validateOptions(startFuture, "keystore_file", "keystore_pass")) {
      return;
    }

    // the http server options
    final HttpServerOptions options = new HttpServerOptions()
        .setPort(config().getInteger("http_port", 8443))
        .setSsl(true)
        .setKeyStoreOptions(new JksOptions()
            .setPath(config().getString("keystore_file"))
            .setPassword(config().getString("keystore_pass")));

    // create the actual http server
    server = vertx.createHttpServer(options)
        .requestHandler(router::accept)
        .listen(deployedHandler -> {
          if (deployedHandler.succeeded()) {
            LOGGER.info(String.format("http server listening at port %s", options.getPort()));
            startFuture.complete();
          } else {
            throw new IllegalStateException("unable to start http server", deployedHandler.cause());
          }
        });
  }

  @Override
  public void stop() throws Exception {
    server.close();
  }

  /**
   * Validates the options for runtime and if there are missing options, fails the start of this
   * verticle.
   *
   * @param startFuture the start future for startup abortion
   * @param required    the required properties
   * @return {@code true} if the options are correct, {@code false} otherwise.
   */
  private boolean validateOptions(final Future<Void> startFuture, final String... required) {
    final String[] missing = Arrays.stream(required)
        .filter(prop -> !config().containsKey(prop))
        .toArray(String[]::new);
    if (missing.length > 0) {
      startFuture.fail("some required properties are missing: " + Arrays.toString(missing));
      return false;
    }

    return true;
  }

  /**
   * Handles the incoming hello requests
   *
   * @param context the routing context of the request
   */
  private void helloRequest(final RoutingContext context) {
    LOGGER.info("handling request..");
    // create the request data from the POST request
    final Optional<SlackRequest> slackRequest = SlackRequest.parse(context);
    LOGGER.info("request data: " + slackRequest);

    // validate the request
    if (!slackRequest.isPresent()) {
      context.response().setStatusCode(400).end();
    } else {
      final SlackRequest request = slackRequest.get();
      final JsonObject response = new JsonObject()
          .add("text", String.format(HELLO_RESPONSE_FORMAT,
              request.getUserId(), request.getUserName()));

      // send the response back to the channel
      context.response().setStatusCode(200)
          .end(response.toString());
    }
  }
}

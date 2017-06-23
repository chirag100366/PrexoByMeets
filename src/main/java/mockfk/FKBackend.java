package mockfk;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This is a verticle. A verticle is a _Vert.x component_. This verticle is implemented in Java, but you can
 * implement them in JavaScript, Groovy, Ruby or Ceylon.
 */
public class FKBackend extends AbstractVerticle {

    HttpClient httpClient;

    Map<FKUser, FKMachine> userMachineMap = new HashMap<>();

    @Override
    public void start(Future<Void> fut) {

        Router router = Router.router(vertx);
        router.route().handler(LoggerHandler.create());
        router.route("/assets/*").handler(StaticHandler.create("assets"));
        router.get("/quote/:username/:password").handler(this::handleGetQuote);
        router.route("/first").handler(BodyHandler.create());
        router.route("/first").handler(this::handlePurchasePath);
        router.route("/second").handler(BodyHandler.create());
        router.route("/second").handler(this::handleDoorStep);
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration,
                        // default to 8080.
                        config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );

        httpClient = vertx.createHttpClient();
    }


    private void handleGetQuote(RoutingContext routingContext) {
       final String username = routingContext.request().getParam("username");
       final String password = routingContext.request().getParam("password");
       FKUser fkUser = new FKUser(username, password);
       if(userMachineMap.get(fkUser) == null){
           System.out.println("No user found");
           routingContext.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code()).end();
       }else{
           Integer price = 2000;
           String spec = userMachineMap.get(fkUser).getSpecs();
           if(spec.contains("i5-")){
               price = 5000;
           }else if(spec.contains("i7-")){
               price = 10000;
           }else if(spec.contains("i3-")){
               price = 3000;
           }

           routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end(price.toString());
       }
    }

    private void handleDoorStep(RoutingContext routingContext) {
        String request = routingContext.getBodyAsString();

        if(request.split("__").length != 4){
            routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
            return;
        }
        final String username = request.split("__")[0];
        final String password = request.split("__")[1];
        final String specs = request.split("__")[2];
        final String otp = request.split("__")[3];

        FKUser user = new FKUser(username, password);
        FKMachine fkMachine = userMachineMap.get(user);
        if(fkMachine == null){
            routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
            return;
        }
        System.out.println(fkMachine.getSpecs());
        System.out.println(fkMachine.getOtp());

        if(fkMachine.getSpecs().equals(specs) && fkMachine.getOtp().equals(otp)) {
            routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end();
        }else{
            routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
        }

    }


    private void handlePurchasePath(RoutingContext routingContext) {
        String request = routingContext.getBodyAsString();
        if(request.split("__").length != 3){
            routingContext.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end();
            return;
        }
        final String username = request.split("__")[0];
        final String password = request.split("__")[1];
        final String specs = request.split("__")[2];

        FKUser user = new FKUser(username, password);
        Random rand = new Random();

        FKMachine machine = new FKMachine(specs, String.valueOf(rand.nextInt(9999) + 1));
        userMachineMap.put(user, machine);
        System.out.println("U: " + user.username + " OTP: " + machine.getOtp());

        routingContext.response().setStatusCode(HttpResponseStatus.OK.code()).end();
    }

    private class FKUser {
        String username;
        String pass;

        public FKUser(String username, String pass) {
            this.username = username;
            this.pass = pass;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FKUser fkUser = (FKUser) o;

            if (!username.equals(fkUser.username)) return false;
            return pass.equals(fkUser.pass);
        }

        @Override
        public int hashCode() {
            int result = username.hashCode();
            result = 31 * result + pass.hashCode();
            return result;
        }
    }

    private class FKMachine {
        String specs;
        String otp;
        FKMachine(String specs, String otp){
            this.specs = specs;
            this.otp = otp;
        }


        public String getSpecs() {
            return specs;
        }

        public String getOtp() {
            return otp;
        }
    }
}

package hex1r0;

import io.vertx.core.Vertx;
import io.vertx.grpc.server.GrpcServer;

public class VertxApp {
  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    var grpcServer = GrpcServer.server(vertx);

    grpcServer.callHandler(
        VertxHelloGrpcGrpcServer.SayHello,
        request ->
            request.handler(
                hello -> {
                  var response = request.response();

                  HelloReply reply =
                      HelloReply.newBuilder().setMessage("Hello " + hello.getName()).build();

                  response.end(reply);
                }));

    vertx
        .createHttpServer()
        .requestHandler(grpcServer)
        .listen(8080)
        .onSuccess(
            server -> {
              System.out.println("Server started, listening on " + server.actualPort());
            })
        .onFailure(Throwable::printStackTrace);
  }
}

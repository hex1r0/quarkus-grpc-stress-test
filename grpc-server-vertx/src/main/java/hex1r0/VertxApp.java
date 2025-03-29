package hex1r0;

import io.grpc.stub.StreamObserver;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.grpcio.server.GrpcIoServer;
import io.vertx.grpcio.server.GrpcIoServiceBridge;

public class VertxApp {

  public static class Server extends AbstractVerticle {

    @Override
    public void start() throws Exception {
      var service =
          new HelloGrpcGrpc.HelloGrpcImplBase() {
            @Override
            public void sayHello(
                HelloRequest request, StreamObserver<HelloReply> responseObserver) {
              responseObserver.onNext(
                  HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
              responseObserver.onCompleted();
            }
          };

      var grpcServer = GrpcIoServer.server(getVertx());
      var serverStub = GrpcIoServiceBridge.bridge(service);
      serverStub.bind(grpcServer);

      getVertx()
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

  public static void main(String[] args) {
    var vertx =
        Vertx.vertx(
            new VertxOptions().setEventLoopPoolSize(Runtime.getRuntime().availableProcessors()));

    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
      vertx.deployVerticle(new Server());
    }
  }
}

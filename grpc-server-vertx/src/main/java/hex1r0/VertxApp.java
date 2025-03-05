package hex1r0;

import io.grpc.stub.StreamObserver;
import io.vertx.core.Vertx;
import io.vertx.grpcio.server.GrpcIoServer;
import io.vertx.grpcio.server.GrpcIoServiceBridge;

public class VertxApp {
  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    //    var grpcServer = GrpcServer.server(vertx);

    //    grpcServer.callHandler(
    //        VertxHelloGrpcGrpcServer.SayHello,
    //        request ->
    //            request.handler(
    //                hello -> {
    //                  var response = request.response();
    //
    //                  HelloReply reply =
    //                      HelloReply.newBuilder().setMessage("Hello " + hello.getName()).build();
    //
    //                  response.end(reply);
    //                }));

    var service =
        new HelloGrpcGrpc.HelloGrpcImplBase() {
          @Override
          public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
            responseObserver.onNext(
                HelloReply.newBuilder().setMessage("Hello " + request.getName()).build());
            responseObserver.onCompleted();
          }
        };

    var grpcServer = GrpcIoServer.server(vertx);
    var serverStub = GrpcIoServiceBridge.bridge(service);
    serverStub.bind(grpcServer);

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

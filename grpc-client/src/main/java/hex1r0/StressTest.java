package hex1r0;

import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

public class StressTest {
  public static void main(String[] args) throws InterruptedException {
    final int threadCount = 10;
    final int requestsPerThread = 100000;
    var requestCount = new CountDownLatch(threadCount * requestsPerThread);

    System.out.println("requestCount: " + requestCount.getCount());

    IntStream.range(0, threadCount)
        .forEach(
            i -> {
              var channel =
                  ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();

              var stub = HelloGrpcGrpc.newStub(channel);
              new Thread(
                      () -> {
                        for (int j = 0; j < requestsPerThread; j++) {
                          String value = String.valueOf(j);
                          var request = HelloRequest.newBuilder().setName(value).build();

                          stub.sayHello(
                              request,
                              new StreamObserver<>() {
                                @Override
                                public void onNext(HelloReply helloReply) {
                                  if (!helloReply.getMessage().equals("Hello " + value)) {
                                    System.err.println("Invalid response!");
                                    System.exit(1);
                                  }
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                  System.err.println(throwable.getMessage());
                                  System.exit(1);
                                }

                                @Override
                                public void onCompleted() {
                                  requestCount.countDown();
                                }
                              });
                        }
                      })
                  .start();
            });

    Thread.sleep(1000);

    long count;
    while ((count = requestCount.getCount()) > 0) {
      try {
        Thread.sleep(1000);
        System.out.println("remaining responses: " + count);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    System.out.println("remaining responses: " + count);
  }
}

1. Start Quarkus gRPC server using the following command:
```shell
cd grpc-server-quarkus && ./run.sh
```

2. Start gRPC stress test client using the following command:
```shell
cd grpc-client && ./run.sh
```

3. Observe hard GC pressure ending with OutOfMemoryError.

4. Repeat the test with Vertx gRPC server and client using the following commands:
```shell
cd grpc-server-vertx && ./run.sh
cd grpc-client && ./run.sh
```

5. Observe that the test completes successfully without any OutOfMemoryError.
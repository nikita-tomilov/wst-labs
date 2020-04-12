package com.programmer74.wstlab1.standalone;

import com.programmer74.wstlab1.service.UsersService;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.grizzly.http.server.HttpServer;
import java.io.IOException;

@Slf4j
public class App {
  public static void main(String... args) throws IOException {
    String url = "http://0.0.0.0:8081/";
    PackagesResourceConfig config = new PackagesResourceConfig(UsersService.class.getPackage()
        .getName());
    log.info("Creating server");
    HttpServer server = GrizzlyServerFactory.createHttpServer(url, config);
    log.info("Starting server");
    server.start();
    Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    log.info("Application started");
    while (true) {
      Thread.yield();
    }
  }
}

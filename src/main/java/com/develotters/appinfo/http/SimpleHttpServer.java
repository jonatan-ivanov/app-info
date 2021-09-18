package com.develotters.appinfo.http;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Jonatan Ivanov
 */
public class SimpleHttpServer {
    private final HttpServer server;
    private final Supplier<String> responseSupplier;

    public SimpleHttpServer(Supplier<String> responseSupplier) throws IOException {
        this(8080, responseSupplier);
    }

    public SimpleHttpServer(int port, Supplier<String> responseSupplier) throws IOException {
        this(HttpServer.create(new InetSocketAddress(port), 0), responseSupplier);
    }

    public SimpleHttpServer(HttpServer server, Supplier<String> responseSupplier) {
        this.server = server;
        this.responseSupplier = responseSupplier;
    }

    public void start() {
        server.createContext("/", this::handleRequest);
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
        System.out.println("Listening on port " + server.getAddress().getPort());
    }

    private void handleRequest(HttpExchange exchange) throws IOException {
        byte[] response = responseSupplier.get().getBytes(UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(HTTP_OK, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void stop() {
        server.stop(0);
        System.out.println("Shutting down...");
    }
}

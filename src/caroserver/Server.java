/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import caroserver.component.Game;
import caroserver.handler.AccountHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import caroserver.thread.ClientThread;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

/**
 *
 * @author phandungtri
 */
public class Server {
    private static ExecutorService executor;
    private static ServerSocket server;
    private static Queue<ClientThread> waitingAccounts = new LinkedList<>();
    private static Map<String, ClientThread> activeAccounts = new HashMap<>();
    private static int port;
    private static Map<String, Game> games = new HashMap<>();

    public static void setPort(int port) {
        Server.port = port;
    }

    public static void start() {
        try {
            server = new ServerSocket(port);
            executor = Executors.newCachedThreadPool();
            listen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void listen() throws IOException {
        System.out.println("Server is waiting for client...");
        while (true) {
            Socket socket = server.accept();
            ClientThread client = new ClientThread(socket);
            String id = UUID.randomUUID().toString();

            activeAccounts.put(id, client);
            client.registerHandler(new AccountHandler());
            executor.execute(client);
            client.response("CONNECTED:" + id);
        }
    }

    public static void queueAccount(ClientThread thread) {
        waitingAccounts.add(thread);
    }

    public static ClientThread dequeueAccount() {
        return waitingAccounts.poll();
    }

    public static boolean isQueueEmpty() {
        return waitingAccounts.isEmpty();
    }

    public static void addGame(Game game) {
        games.put(game.getId(), game);
    }

    public static void removeGame(String id) {
        games.remove(id);
    }

    public static Map<String, Game> getGameList() {
        return games;
    }

    public static void disconnectClient(String id) {
        ClientThread client = activeAccounts.get(id);

        activeAccounts.remove(id);

        if (waitingAccounts.contains(client)) {
            waitingAccounts.remove(client);
        }
    }
}

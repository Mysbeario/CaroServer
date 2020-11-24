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
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.ArrayList;
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
    private static ArrayList<ClientThread> activeAccounts = new ArrayList<>();
    private static int port;
    private static Map<String, Game> games = new HashMap<>();

    public static void setPort(int port) {
        Server.port = port;
    }

    public static int countOnlineAccount() {
        return activeAccounts.size();
    }

    public static void start() {
        try {
            AdminConsole adminConsole = new AdminConsole();
            server = new ServerSocket(port);
            executor = Executors.newCachedThreadPool();
            executor.execute(adminConsole);
            listen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static void listen() throws IOException, NoSuchAlgorithmException {
        System.out.println("Server is waiting for client...");
        while (true) {
            Socket socket = server.accept();
            String id = UUID.randomUUID().toString();
            ClientThread client = new ClientThread(socket, id);

            client.registerHandler(new AccountHandler());
            executor.execute(client);
            client.response("CONNECTED:" + id, false);
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

    public static void addActiveAccount(ClientThread clientThread) {
        activeAccounts.add(clientThread);
    }

    public static boolean isAccountActive(String email) {
        for (ClientThread cl : activeAccounts) {
            if (cl.getAccount().getEmail().equals(email)) {
                return true;
            }
        }

        return false;
    }

    public static void disconnectClient(ClientThread clientThread) {
        activeAccounts.remove(clientThread);

        // Case: player is looking for match.
        if (waitingAccounts.contains(clientThread)) {
            waitingAccounts.remove(clientThread);
        }

        // Case: player found a match but hasn't responsed yet.
        /* Client will send "DECLINE" signal when the application is terminated */
    }
}

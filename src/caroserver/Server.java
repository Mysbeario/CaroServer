/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import caroserver.handler.AccountHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import caroserver.thread.ClientThread;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author phandungtri
 */
public class Server {
    private static ExecutorService executor;
    private static ServerSocket server;
    private static Queue<ClientThread> activeAccounts = new LinkedList<>();
    private static int port;

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
            client.registerHandler(new AccountHandler());
            executor.execute(client);
        }
    }

    public static void queueAccount(ClientThread thread) {
        activeAccounts.add(thread);
    }

    public static ClientThread dequeueAccount() {
        return activeAccounts.poll();
    }

    public static boolean isQueueEmpty() {
        return activeAccounts.isEmpty();
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import caroserver.handler.AccountHandler;
import caroserver.model.Account;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import caroserver.thread.ClientThread;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author phandungtri
 */
public class Server {
    private ExecutorService executor;
    private ServerSocket server;
    private Queue<Pair<ClientThread, Account>> activeAccounts = new LinkedList<>();

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            executor = Executors.newCachedThreadPool();
            listen();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void listen() throws IOException {
        System.out.println("Server is waiting for client...");
        while (true) {
            Socket socket = server.accept();
            ClientThread client = new ClientThread(socket);
            client.registerHandler(new AccountHandler(this));
            executor.execute(client);
        }
    }

    public void queueAccount(ClientThread thread, Account account) {
        activeAccounts.add(new Pair<>(thread, account));
    }

    public Queue<Pair<ClientThread, Account>> getActiveAccounts() {
        return activeAccounts;
    }
}

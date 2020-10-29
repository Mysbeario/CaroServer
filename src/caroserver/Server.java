/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import caroserver.model.Account;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import caroserver.thread.AccountThread;
import java.util.ArrayList;

/**
 *
 * @author phandungtri
 */
public class Server {
    private ExecutorService executor;
    private ServerSocket server;
    private Room room;
    private ArrayList<Account> activeAccounts = new ArrayList<Account>();

    public Server(int port) {
        try {
            room = new Room();
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
            executor.execute(new AccountThread(socket, this));
            Player player = new Player(socket);
            room.addPlayer(player);
            executor.execute(player);
        }
    }

    public void logAccountIn(Account account) {
        activeAccounts.add(account);
    }
}

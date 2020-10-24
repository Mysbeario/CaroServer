/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 *
 * @author phandungtri
 */
public class Player implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String request;

    private class OnNewMove implements Command {
        public void execute(String data) {
            String[] moves = data.split(";");
            System.out.println(moves[0] + " - " + moves[1]);
        }
    }

    public Player(Socket socket) throws IOException {
        this.socket = socket;
        request = "";
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
    }

    private void on(String command, Command handler) {
        String[] requestParts = request.split(":");
        if (requestParts[0].equals(command)) {
            handler.execute(requestParts[1]);
        }
    }

    public void run() {
        try {
            System.out.println("Client connected!");
            while (true) {
                request = in.readLine();
                on("MOV", new OnNewMove());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

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
import java.util.UUID;

/**
 *
 * @author phandungtri
 */
public class Player implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String request;
    private Room room;
    private String id;
    
    private class OnRegister implements Command {
        public void execute(String data) {
            String[] parts = data.split(";");
            String email = parts[0];
            String password = parts[1];
            String fullname = parts[2];
            String gender = parts[3];
            String birthday = parts[4];
            
            System.out.println(String.join(",", email, password, fullname, gender, birthday));
        }
    }

    private class OnNewMove implements Command {
        public void execute(String data) {
            String[] parts = data.split(";");
            int col = Integer.parseInt(parts[0]);
            int row = Integer.parseInt(parts[1]);
            String fromPlayer = parts[2];

            if (room.getCurrentPlayerId().equals(fromPlayer) && room.newMove(col, row, fromPlayer)) {
                room.sendAll(request);

                if (room.isWinning(col, row)) {
                    room.sendAll("END:" + room.getCurrentPlayerId());
                }

                room.nextTurn();
            }
        }
    }

    public Player(Socket socket) throws IOException {
        this.socket = socket;
        request = "";
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        createId();
    }

    private void createId() throws IOException {
        id = UUID.randomUUID().toString();
        out.write("CON:" + id + "\n");
        out.flush();
    }

    private void on(String command, Command handler) {
        String[] requestParts = request.split(":");
        if (requestParts[0].equals(command)) {
            handler.execute(requestParts[1]);
        }
    }

    public String getId() {
        return id;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void run() {
        try {
            System.out.println("Client connected!");
            while (true) {
                request = in.readLine();
                on("MOV", new OnNewMove());
                on("REG", new OnRegister());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void response(String data) {
        try {
            out.write(data + "\n");
            out.flush();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

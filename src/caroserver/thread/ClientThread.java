package caroserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import caroserver.handler.HandlerBase;

public class ClientThread implements Runnable {
	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;
	private ArrayList<HandlerBase> handlers = new ArrayList<>();

	public ClientThread(Socket socket) {
		this.socket = socket;

		try {
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void response(String res) {
		try {
			out.write(res + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void registerHandler(HandlerBase handler) {
		handler.setThread(this);
		handlers.add(handler);
	}

	@Override
	public void run() {
		try {
			while (true) {
				String request = in.readLine();
				String[] parts = request.split(":");
				String[] data = parts[1].split(";");

				for (HandlerBase handler : handlers) {
					handler.handleRequest(parts[0], data);
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
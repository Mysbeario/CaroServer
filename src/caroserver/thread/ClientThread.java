package caroserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import caroserver.Server;
import caroserver.handler.HandlerBase;
import caroserver.model.Account;

public class ClientThread implements Runnable {
	private Socket socket;
	private BufferedWriter out;
	private BufferedReader in;
	private ArrayList<HandlerBase> handlers = new ArrayList<>();
	private ArrayList<HandlerBase> toRemove = new ArrayList<>();
	private ArrayList<HandlerBase> toAdd = new ArrayList<>();
	private Account account;
	private boolean isDisconnected = false;

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
		toAdd.add(handler);
	}

	public void unregisterHandler(HandlerBase handler) {
		toRemove.add(handler);
	}

	@Override
	public void run() {
		try {
			while (!isDisconnected) {
				handlers.addAll(toAdd);
				toAdd.clear();

				String request = in.readLine();
				String[] parts = request.split(":");
				String[] data = parts[1].split(";");

				if (parts[0].equals("DISCONNECT")) {
					isDisconnected = true;
					Server.disconnectClient(data[0]);
				}

				for (HandlerBase handler : handlers) {
					handler.handleRequest(parts[0], data);
				}

				handlers.removeAll(toRemove);
				toRemove.clear();
			}

			socket.close();
			out.close();
			in.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Account getAccount() {
		return account;
	}
}
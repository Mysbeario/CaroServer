package caroserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import caroserver.Server;
import caroserver.handler.HandlerBase;
import caroserver.model.Account;

public class ClientThread implements Runnable {
	private Socket socket;
	private String id;
	private BufferedWriter out;
	private BufferedReader in;
	private ArrayList<HandlerBase> handlers = new ArrayList<>();
	private ArrayList<HandlerBase> toRemove = new ArrayList<>();
	private ArrayList<HandlerBase> toAdd = new ArrayList<>();
	private StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
	private Account account;
	private boolean isDisconnected = false;

	public ClientThread(Socket socket, String id) {
		try {
			this.socket = socket;
			this.id = id;
			encryptor.setPassword(id);
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public String getId() {
		return id;
	}

	public void response(String res, boolean isEncrypted) {
		try {
			String encryptedData = res;

			if (isEncrypted) {
				encryptedData = encryptor.encrypt(res);
			}

			out.write(encryptedData + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void response(String res) {
		response(res, true);
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

				String request = in.readLine().replace("\n", "").replace("\r", "");
				String decryptedRequest = encryptor.decrypt(request);
				String[] parts = decryptedRequest.split(":");
				String[] data = parts[1].split(";");

				if (parts[0].equals("DISCONNECT")) {
					isDisconnected = true;
					Server.disconnectClient(this);
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
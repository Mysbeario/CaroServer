package caroserver.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public abstract class ClientThread implements Runnable {
	protected Socket socket;
	protected BufferedWriter out;
	protected BufferedReader in;

	protected ClientThread(Socket socket) {
		this.socket = socket;

		try {
			out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	protected abstract void handleRequest(String command, String[] data);

	public void response(String res) {
		try {
			out.write(res + "\n");
			out.flush();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				String request = in.readLine();
				String[] parts = request.split(":");
				String[] data = parts[1].split(";");

				handleRequest(parts[0], data);
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
}
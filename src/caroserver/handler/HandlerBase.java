package caroserver.handler;

import caroserver.thread.ClientThread;

public abstract class HandlerBase {
	protected ClientThread thread;

	public void setThread(ClientThread thread) {
		this.thread = thread;
	}

	public abstract void handleRequest(String command, String[] data);
}

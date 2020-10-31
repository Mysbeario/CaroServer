package caroserver.model;

import caroserver.thread.ClientThread;
import javafx.util.Pair;

public class MatchMaker {
	private Pair<Pair<ClientThread, Account>, Pair<ClientThread, Account>> pair;

	public MatchMaker(Pair<ClientThread, Account> acc1, Pair<ClientThread, Account> acc2) {
		pair = new Pair<>(acc1, acc2);

		new Thread(() -> {
			try {
				Thread.sleep(300);
				pair.getKey().getKey().response("MMK:Match found!");
				pair.getValue().getKey().response("MMK:Match found!");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
}

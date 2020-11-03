package caroserver.component;

import java.util.ArrayList;

import caroserver.handler.MatchMakingHandler;
import caroserver.thread.ClientThread;

public class MatchMaker {
	private ClientThread[] pair = new ClientThread[2];
	private ArrayList<Boolean> acceptStatus = new ArrayList<>();

	public MatchMaker(ClientThread acc1, ClientThread acc2) {

		pair[0] = acc1;
		pair[1] = acc2;

		for (ClientThread acc : pair) {
			acc.registerHandler(new MatchMakingHandler(this));
			acc.response("MMK:Match found!");
		}
	}

	public void acceptMatch() {
		acceptStatus.add(true);
	}

	public void declineMatch() {
		acceptStatus.add(false);
	}

	public void checkMatch() {
		if (acceptStatus.size() == 2) {
			for (boolean isAccepted : acceptStatus) {
				if (!isAccepted) {
					for (ClientThread acc : pair) {
						acc.response("MMK_BCK:Back to queue");
					}

					return;
				}
			}

			for (ClientThread acc : pair) {
				acc.response("MMK_NEW:New match");
			}

			new Game(pair);
		}
	}
}

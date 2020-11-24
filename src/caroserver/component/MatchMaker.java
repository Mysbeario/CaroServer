package caroserver.component;

import java.util.ArrayList;

import caroserver.Server;
import caroserver.handler.MatchMakingHandler;
import caroserver.ClientThread;

public class MatchMaker {
	private ClientThread[] pair = new ClientThread[2];
	private ArrayList<Boolean> acceptStatus = new ArrayList<>();

	public MatchMaker(ClientThread acc1, ClientThread acc2) {

		pair[0] = acc1;
		pair[1] = acc2;

		for (ClientThread acc : pair) {
			acc.registerHandler(new MatchMakingHandler(this));
			acc.response("MATCHMAKING:Match found");
		}
	}

	public void acceptMatch() {
		acceptStatus.add(true);
	}

	public void declineMatch() {
		acceptStatus.add(false);
	}

	private String getBriefInfo(ClientThread thread) {
		return thread.getAccount().getId() + "," + thread.getAccount().getFullname();
	}

	public void checkMatch() {
		if (acceptStatus.size() == 2) {
			for (boolean isAccepted : acceptStatus) {
				if (!isAccepted) {
					for (ClientThread acc : pair) {
						acc.response("BACK_TO_QUEUE:Back to queue");
					}

					return;
				}
			}

			Game game = new Game(pair);
			String currentPlayerId = game.getCurrentPlayerId();

			Server.addGame(game);
			pair[0].response("NEW_MATCH:" + getBriefInfo(pair[1]) + ";" + currentPlayerId);
			pair[1].response("NEW_MATCH:" + getBriefInfo(pair[0]) + ";" + currentPlayerId);
		}
	}
}

package caroserver.handler;

import java.sql.SQLException;

import caroserver.bll.AccountBLL;
import caroserver.component.MatchMaker;
import caroserver.model.Account;

public class MatchMakingHandler extends HandlerBase {
	private MatchMaker matchMaker;

	public MatchMakingHandler(MatchMaker matchMaker) {
		this.matchMaker = matchMaker;
	}

	private void declineMatch() {
		try {
			AccountBLL service = new AccountBLL();
			Account account = this.thread.getAccount();

			account.setScore(account.getScore() - 1);
			service.update(account);
			thread.unregisterHandler(this);
			matchMaker.declineMatch();
			matchMaker.checkMatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleRequest(String command, String[] data) {
		switch (command) {
			case "ACCEPT_MATCHMAKING": {
				matchMaker.acceptMatch();
				thread.unregisterHandler(this);
				matchMaker.checkMatch();
				break;
			}
			case "DISCONNECT":
			case "DECLINE_MATCHMAKING": {
				declineMatch();
				break;
			}
		}
	}
}

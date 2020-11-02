package caroserver.handler;

import caroserver.thread.MatchMaker;

public class MatchMakingHandler extends HandlerBase {
	private MatchMaker matchMaker;

	public MatchMakingHandler(MatchMaker matchMaker) {
		this.matchMaker = matchMaker;
	}

	@Override
	public void handleRequest(String command, String[] data) {
		switch (command) {
			case "MMK_ACP": {
				matchMaker.acceptMatch();
				thread.unregisterHandler(this);
				matchMaker.checkMatch();
				break;
			}
			case "MMK_DEC": {
				matchMaker.declineMatch();
				thread.unregisterHandler(this);
				matchMaker.checkMatch();
				break;
			}
		}
	}
}

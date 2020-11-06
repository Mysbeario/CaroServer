package caroserver.handler;

import caroserver.component.Game;

public class GameHandler extends HandlerBase {
	private Game game;

	public GameHandler(Game game) {
		this.game = game;
	}

	@Override
	public void handleRequest(String command, String[] data) {
		switch (command) {
			case "MOVE": {
				int col = Integer.parseInt(data[0]);
				int row = Integer.parseInt(data[1]);
				String fromPlayer = data[2];

				if (game.getCurrentPlayerId().equals(fromPlayer) && game.newMove(col, row, fromPlayer)) {
					game.sendAll(command + ":" + String.join(";", data));

					if (game.isWinning(col, row)) {
						thread.unregisterHandler(this);
						game.calculateScore();
						game.sendAll("GAMEOVER:" + game.getCurrentPlayerId());
					} else {
						game.nextTurn();
					}
				}

				break;
			}
		}
	}
}
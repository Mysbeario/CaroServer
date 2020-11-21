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
				String fromPlayer = thread.getAccount().getId();

				if (game.getCurrentPlayerId().equals(fromPlayer) && game.newMove(col, row)) {
					game.nextTurn();
					game.sendAll(command + ":" + String.join(";", data[0], data[1], fromPlayer, game.getCurrentPlayerId()));

					if (game.isWinning(col, row)) {
						game.gameOver(fromPlayer);
					} else {
						game.resetTimer();
					}
				}

				break;
			}
			case "CHAT": {
				game.sendAll(command + ":" + data[0] + ";" + thread.getAccount().getFullname());
				break;
			}
			case "READY": {
				thread.unregisterHandler(this);
				break;
			}
			case "DISCONNECT": {
				if (game.getCurrentPlayerId().equals(thread.getAccount().getId())) {
					game.nextTurn();
				}

				game.gameOver(game.getCurrentPlayerId());
				break;
			}
		}
	}
}

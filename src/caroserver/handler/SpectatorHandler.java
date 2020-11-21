package caroserver.handler;

import caroserver.component.Game;

public class SpectatorHandler extends HandlerBase {
	private Game room;

	public SpectatorHandler(Game room) {
		this.room = room;
	}

	@Override
	public void handleRequest(String command, String[] data) {
		switch (command) {
			case "CHAT": {
				room.sendAll(command + ":" + data[0] + ";" + thread.getAccount().getFullname());
				break;
			}
			case "DISCONNECT":
			case "NEW_MATCH": {
				room.removeSpectator();
				thread.unregisterHandler(this);
				break;
			}
		}
	}
}

package caroserver.model;

import java.util.Date;

public class MatchHistory {
	public enum MatchStatus {
		WIN(0), LOSE(1), DRAW(2);

		private final int value;

		private MatchStatus(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private String playerId;
	private MatchStatus status;
	private String date = (new Date()).toString();

	public MatchHistory() {
	}

	public MatchHistory(String playerId, MatchStatus status, String date) {
		this.playerId = playerId;
		this.status = status;
		this.date = date;
	}

	public MatchHistory(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public String getDate() {
		return date;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public void setStatus(MatchStatus status) {
		this.status = status;
	}

	public void setDate(String date) {
		this.date = date;
	}
}

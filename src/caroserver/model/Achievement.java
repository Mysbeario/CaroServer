package caroserver.model;

public class Achievement {
	private int win;
	private int lose;
	private int draw;
	private int longestWinStreak;
	private int longestLoseStreak;
	private String playerId;

	public Achievement(String playerId, int win, int lose, int draw, int longestWinStreak, int longestLoseStreak) {
		this.playerId = playerId;
		this.win = win;
		this.lose = lose;
		this.draw = draw;
		this.longestLoseStreak = longestLoseStreak;
		this.longestWinStreak = longestWinStreak;
	}

	public int getWin() {
		return win;
	}

	public int getLose() {
		return lose;
	}

	public int getDraw() {
		return draw;
	}

	public int getTotalGame() {
		return win + draw + lose;
	}

	public int getLongestWinStreak() {
		return longestWinStreak;
	}

	public int getLongestLoseStreak() {
		return longestLoseStreak;
	}

	public double getWinRate() {
		if (getTotalGame() == 0) {
			return 0;
		}

		double winRate = win / (getTotalGame() * 1.f) * 100;
		return Math.round(winRate * 100.0) / 100.0;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public void setLose(int lose) {
		this.lose = lose;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}

	public void setLongestWinChain(int winStreak) {
		this.longestWinStreak = winStreak;
	}

	public void setLongestLoseChain(int loseStreak) {
		this.longestLoseStreak = loseStreak;
	}

	public String toString() {
		return String.join(";", Integer.toString(getWin()), Integer.toString(getLose()), Integer.toString(getDraw()),
				Integer.toString(getLongestWinStreak()), Integer.toString(getLongestLoseStreak()),
				Double.toString(getWinRate()));
	}
}

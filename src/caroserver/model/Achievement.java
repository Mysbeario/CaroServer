package caroserver.model;

public class Achievement {
	private int win;
	private int lose;
	private int draw;
	private int longestWinChain;
	private int longestLoseChain;
	private String playerId;

	public Achievement(String playerId, int win, int lose, int draw, int longestWinChain, int longestLoseChain) {
		this.playerId = playerId;
		this.win = win;
		this.lose = lose;
		this.draw = draw;
		this.longestLoseChain = longestLoseChain;
		this.longestWinChain = longestWinChain;
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

	public int getLongestWinChain() {
		return longestWinChain;
	}

	public int getLongestLoseChain() {
		return longestLoseChain;
	}

	public double getWinRate() {
		return win / (getTotalGame() * 1.f);
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

	public void setLongestWinChain(int winChain) {
		this.longestWinChain = winChain;
	}

	public void setLongestLoseChain(int loseChain) {
		this.longestLoseChain = loseChain;
	}
}

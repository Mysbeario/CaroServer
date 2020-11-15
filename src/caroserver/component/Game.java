package caroserver.component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import caroserver.Server;
import caroserver.bll.AccountBLL;
import caroserver.bll.MatchHistoryBLL;
import caroserver.handler.GameHandler;
import caroserver.model.Account;
import caroserver.model.MatchHistory;
import caroserver.model.MatchHistory.MatchStatus;
import caroserver.thread.ClientThread;

public class Game {
	private ClientThread[] players = new ClientThread[2];
	private ClientThread spectator;
	private String[][] board = new String[15][15];
	private int currentPlayer = 0;
	private Thread turnTimer;
	private Thread gameTimer;
	private boolean isDraw = false;
	private String id = UUID.randomUUID().toString();

	public Game(ClientThread[] players) {
		this.players = players;

		for (ClientThread p : players) {
			p.registerHandler(new GameHandler(this));
		}

		for (String[] row : board) {
			Arrays.fill(row, "");
		}

		startGameTimer();
		startTurnTimer();
	}

	private void startGameTimer() {
		int duration = 10 * 60 * 1000;

		gameTimer = new Thread(() -> {
			try {
				Thread.sleep(duration);
				isDraw = true;
				gameOver(null);
			} catch (InterruptedException e) {
			}
		});

		gameTimer.start();
	}

	private String getPlayerInfo(ClientThread p) {
		return p.getAccount().getId() + "," + p.getAccount().getFullname();
	}

	public void addSpectator(ClientThread spectator) {
		String playerInfos = getPlayerInfo(players[0]) + ";" + getPlayerInfo(players[1]);
		ArrayList<String> moveInfos = new ArrayList<>();

		for (int i = 0; i < 15; i++) {
			for (int j = 0; j < 15; j++) {
				if (!board[i][j].equals("")) {
					moveInfos.add(String.join(".", Integer.toString(j), Integer.toString(i), board[i][j]));
				}
			}
		}

		this.spectator = spectator;
		this.spectator
				.response("GAME_INFO:" + playerInfos + ";" + getCurrentPlayerId() + ";" + String.join(",", moveInfos));
	}

	public void nextTurn() {
		currentPlayer = (currentPlayer + 1) % 2;
	}

	private void startTurnTimer() {
		turnTimer = new Thread(() -> {
			try {
				Thread.sleep(30000);
				nextTurn();
				gameOver(getCurrentPlayerId());
			} catch (InterruptedException e) {
			}
		});
		turnTimer.start();
	}

	public String getCurrentPlayerId() {
		return players[currentPlayer].getAccount().getId();
	}

	public void resetTimer() {
		turnTimer.interrupt();
		startTurnTimer();
	}

	public boolean newMove(int col, int row, String fromPlayer) {
		if (!board[row][col].equals("")) {
			return false;
		}

		board[row][col] = fromPlayer;
		return true;
	}

	private boolean checkHorizontal(int x, int y) {
		int count = 1;
		int col = x;

		while (col < 14 && board[y][col].equals(board[y][col + 1])) {
			count++;
			col++;
		}

		col = x;
		while (col > 0 && board[y][col].equals(board[y][col - 1])) {
			count++;
			col--;
		}

		return count >= 5;
	}

	private boolean checkVertical(int x, int y) {
		int count = 1;
		int row = y;

		while (row < 14 && board[row][x].equals(board[row + 1][x])) {
			count++;
			row++;
		}

		row = y;
		while (row > 0 && board[row][x].equals(board[row - 1][x])) {
			count++;
			row--;
		}

		return count >= 5;
	}

	private boolean checkLeftDiagonal(int x, int y) {
		int count = 1;
		int row = y;
		int col = x;

		while (row < 14 && col < 14 && board[row][col].equals(board[row + 1][col + 1])) {
			count++;
			row++;
			col++;
		}

		row = y;
		col = x;
		while (row > 0 && col > 0 && board[row][col].equals(board[row - 1][col - 1])) {
			count++;
			row--;
			col--;
		}

		return count >= 5;
	}

	private boolean checkRightDiagonal(int x, int y) {
		int count = 1;
		int row = y;
		int col = x;

		while (row < 14 && col > 0 && board[row][col].equals(board[row + 1][col - 1])) {
			count++;
			row++;
			col--;
		}

		row = y;
		col = x;
		while (row > 0 && col < 14 && board[row][col].equals(board[row - 1][col + 1])) {
			count++;
			row--;
			col++;
		}

		return count >= 5;
	}

	public boolean isWinning(int col, int row) {
		return checkHorizontal(col, row) || checkVertical(col, row) || checkLeftDiagonal(col, row)
				|| checkRightDiagonal(col, row);
	}

	public void sendAll(String data) {
		for (ClientThread p : players) {
			p.response(data);
		}

		if (spectator != null) {
			spectator.response(data);
		}
	}

	public void calculateScore(String winningPlayerId) {
		try {
			MatchHistoryBLL historyService = new MatchHistoryBLL();
			AccountBLL accountService = new AccountBLL();

			for (ClientThread p : players) {
				Account account = p.getAccount();
				MatchHistory history = new MatchHistory(p.getAccount().getId());

				if (isDraw) {
					account.setScore(account.getScore() + 1);
					history.setStatus(MatchStatus.DRAW);
				} else if (winningPlayerId.equals(account.getId())) {
					account.setScore(account.getScore() + 3);
					history.setStatus(MatchStatus.WIN);
				} else {
					account.setScore(account.getScore() - 1);
					history.setStatus(MatchStatus.LOSE);
				}

				accountService.update(account);
				historyService.create(history);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void gameOver(String winningPlayerId) {
		calculateScore(winningPlayerId);
		sendAll("GAMEOVER:" + (isDraw ? "DRAW" : winningPlayerId));
		turnTimer.interrupt();
		gameTimer.interrupt();

		Server.removeGame(id);
	}

	public String[] getPlayerNames() {
		String[] names = new String[2];

		for (int i = 0; i < players.length; i++) {
			names[i] = players[i].getAccount().getFullname();
		}

		return names;
	}

	public String getId() {
		return id;
	}
}

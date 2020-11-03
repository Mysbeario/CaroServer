package caroserver.component;

import java.util.Arrays;

import caroserver.handler.GameHandler;
import caroserver.thread.ClientThread;

public class Game {
	private ClientThread[] players = new ClientThread[2];
	private String[][] board = new String[15][15];
	private int currentPlayer = 0;

	public Game(ClientThread[] players) {
		this.players = players;

		for (ClientThread p : players) {
			p.registerHandler(new GameHandler(this));
		}

		for (String[] row : board) {
			Arrays.fill(row, "");
		}
	}

	public String getCurrentPlayerId() {
		return players[currentPlayer].getAccount().getId();
	}

	public boolean newMove(int col, int row, String fromPlayer) {
		if (!board[row][col].equals("")) {
			System.out.println("Cell filled");
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
	}

	public void nextTurn() {
		currentPlayer = (currentPlayer + 1) % 2;
	}
}

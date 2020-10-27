/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package caroserver;

import java.util.Arrays;

/**
 *
 * @author phandungtri
 */
public class Room {
    private Player[] players = new Player[2];
    private int numOfPlayers = 0;
    private String[][] board = new String[15][15];
    private int currentPlayer = 0;

    public Room() {
        for (String[] row : board) {
            Arrays.fill(row, "");
        }
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

    public String getCurrentPlayerId() {
        return players[currentPlayer].getId();
    }

    public void addPlayer(Player player) {
        if (numOfPlayers < 2) {
            player.setRoom(this);
            players[numOfPlayers] = player;
            numOfPlayers++;
        }
    }

    public boolean newMove(int col, int row, String fromPlayer) {
        if (!board[row][col].equals(""))
            return false;
        board[row][col] = fromPlayer;
        return true;
    }

    public void sendAll(String data) {
        for (Player p : players) {
            p.response(data);
        }
    }

    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % 2;
    }
}

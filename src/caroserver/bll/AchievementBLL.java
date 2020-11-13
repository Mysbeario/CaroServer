package caroserver.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import caroserver.dal.MatchHistoryDAL;
import caroserver.model.Achievement;
import caroserver.model.MatchHistory;

public class AchievementBLL {
	public Achievement get(String playerId) throws SQLException {
		ArrayList<MatchHistory> histories = MatchHistoryDAL.readByPlayerId(playerId);
		int win = 0;
		int lose = 0;
		int draw = 0;
		int longestWinChain = 0;
		int longestLoseChain = 0;
		int chainCounter = 0;
		int prevItem = -1;

		for (MatchHistory h : histories) {
			int status = h.getStatus().getValue();

			if (prevItem == -1) {
				chainCounter = 1;
				prevItem = status;
			}

			if (status != prevItem) {
				switch (status) {
					case 0: {
						if (chainCounter > longestWinChain) {
							longestWinChain = chainCounter;
						}

						break;
					}
					case 1: {
						if (chainCounter > longestLoseChain) {
							longestLoseChain = chainCounter;
						}

						break;
					}
				}

				chainCounter = 1;
			}

			prevItem = status;

			switch (status) {
				case 0: {
					win++;
					break;
				}
				case 1: {
					lose++;
					break;
				}
				case 2: {
					draw++;
					break;
				}
			}
		}

		return new Achievement(playerId, win, lose, draw, longestWinChain, longestLoseChain);
	}
}

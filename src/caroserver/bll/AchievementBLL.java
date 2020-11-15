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
		int longestWinStreak = 0;
		int longestLoseStreak = 0;
		int streakCounter = 1;
		int prevItem = -1;

		for (MatchHistory h : histories) {
			int status = h.getStatus().getValue();

			if (status == prevItem) {
				streakCounter++;
			} else {
				switch (prevItem) {
					case 0: {
						if (streakCounter > longestWinStreak) {
							longestWinStreak = streakCounter;
						}

						break;
					}
					case 1: {
						if (streakCounter > longestLoseStreak) {
							longestLoseStreak = streakCounter;
						}

						break;
					}
				}

				streakCounter = 1;
			}

			prevItem = status;

			// Count match
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

		// Check final item
		switch (prevItem) {
			case 0: {
				if (streakCounter > longestWinStreak) {
					longestWinStreak = streakCounter;
				}

				break;
			}
			case 1: {
				if (streakCounter > longestLoseStreak) {
					longestLoseStreak = streakCounter;
				}

				break;
			}
		}

		return new Achievement(playerId, win, lose, draw, longestWinStreak, longestLoseStreak);
	}
}

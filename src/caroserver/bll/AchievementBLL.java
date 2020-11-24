package caroserver.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

import caroserver.dal.AccountDAL;
import caroserver.dal.MatchHistoryDAL;
import caroserver.model.Account;
import caroserver.model.Achievement;
import caroserver.model.MatchHistory;

public class AchievementBLL {
	public ArrayList<String> rank(String rankBy) throws SQLException {
		ArrayList<Account> accounts = AccountDAL.read();
		ArrayList<Achievement> achievements = new ArrayList<>();
		ArrayList<String> result = new ArrayList<>();
		AccountBLL accountService = new AccountBLL();

		for (Account acc : accounts) {
			achievements.add(get(acc.getId()));
		}

		achievements.sort(new Comparator<Achievement>() {
			@Override
			public int compare(Achievement a, Achievement b) {
				try {
					int aScore = accountService.getById(a.getPlayerId()).getScore();
					int bScore = accountService.getById(b.getPlayerId()).getScore();
					double aRating = aScore + a.getWinRate() * (a.getTotalGame() <= 10 ? 0 : a.getWinRate())
							+ a.getLongestWinStreak();
					double bRating = bScore + b.getWinRate() * (b.getTotalGame() <= 10 ? 0 : b.getWinRate())
							+ b.getLongestWinStreak();

					return aRating > bRating ? -1 : 1;
				} catch (SQLException e) {
					e.printStackTrace();
				}

				return 0;
			}
		});

		for (int i = 0; i < achievements.size(); i++) {
			Achievement achi = achievements.get(i);
			String info = (i + 1) + ",";

			info += accountService.getById(achi.getPlayerId()).getFullname();
			result.add(info);
		}

		return result;
	}

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

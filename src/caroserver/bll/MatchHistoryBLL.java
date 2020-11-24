package caroserver.bll;

import java.sql.SQLException;

import caroserver.dal.MatchHistoryDAL;
import caroserver.model.MatchHistory;

public class MatchHistoryBLL {
	public void create(MatchHistory history) {
		MatchHistoryDAL.create(history);
	}

	public String getPlayerIdWithMostWin() throws SQLException {
		return MatchHistoryDAL.getMostWin();
	}
}

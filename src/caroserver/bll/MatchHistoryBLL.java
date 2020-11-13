package caroserver.bll;

import caroserver.dal.MatchHistoryDAL;
import caroserver.model.MatchHistory;

public class MatchHistoryBLL {
	public void create(MatchHistory history) {
		MatchHistoryDAL.create(history);
	}
}

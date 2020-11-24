package caroserver.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import caroserver.model.MatchHistory;
import caroserver.model.MatchHistory.MatchStatus;

public class MatchHistoryDAL {
	private static MatchHistory extract(ResultSet result) throws SQLException {
		MatchHistory history = new MatchHistory();

		history.setPlayerId(result.getString("playerId"));
		history.setStatus(MatchStatus.values()[result.getInt("status")]);
		history.setDate(result.getString("date"));

		return history;
	}

	public static void create(MatchHistory history) {
		try {
			Connection conn = Database.connect();
			String query = "INSERT INTO history(playerId, status, date) VALUES(?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);

			stmt.setString(1, history.getPlayerId());
			stmt.setInt(2, history.getStatus().getValue());
			stmt.setString(3, history.getDate());

			stmt.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static ArrayList<MatchHistory> readByPlayerId(String playerId) throws SQLException {
		Connection conn = Database.connect();
		ArrayList<MatchHistory> matchHistories = new ArrayList<>();
		String query = "SELECT * FROM history WHERE playerId = ? ORDER BY date DESC";
		PreparedStatement stmt = conn.prepareStatement(query);

		stmt.setString(1, playerId);

		ResultSet result = stmt.executeQuery();

		while (result.next()) {
			matchHistories.add(extract(result));
		}

		conn.close();
		return matchHistories;
	}

	public static String getMostWin() throws SQLException {
		Connection conn = Database.connect();
		String query = "SELECT playerId, COUNT(*) AS win FROM history WHERE status = 0 GROUP BY playerId order by win desc limit 1";
		Statement stmt = conn.createStatement();
		ResultSet result = stmt.executeQuery(query);

		return result.getString(1);
	}
}

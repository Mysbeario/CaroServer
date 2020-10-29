package caroserver.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

	public static Connection connect() {
		Connection conn = null;

		try {
			String url = "jdbc:sqlite:./database.db";
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return conn;
	}
}

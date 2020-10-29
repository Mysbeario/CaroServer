package caroserver.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import caroserver.model.Account;

public class AccountDAL {
	public static void create(Account account) {
		try {
			Connection conn = Database.connect();
			String query = "INSERT INTO account(id, email, password, fullname, gender, birthday) VALUES(?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);

			stmt.setString(1, account.getId());
			stmt.setString(2, account.getEmail());
			stmt.setString(3, account.getPassword());
			stmt.setString(4, account.getFullname());
			stmt.setInt(5, account.getGender());
			stmt.setString(6, account.getBirthday());

			stmt.executeUpdate();
			conn.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static ArrayList<Account> read() throws SQLException {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Connection conn = Database.connect();
		String query = "SELECT * FROM account";
		Statement stmt = conn.createStatement();
		ResultSet result = stmt.executeQuery(query);

		while (result.next()) {
			Account acc = new Account();

			acc.setId(result.getString(1));
			acc.setEmail(result.getString(2));
			acc.setPassword(result.getString(3));
			acc.setFullname(result.getString(4));
			acc.setGender(result.getInt(5));
			acc.setBirthday(result.getString(6));
			accounts.add(acc);
		}

		conn.close();
		return accounts;
	}
}

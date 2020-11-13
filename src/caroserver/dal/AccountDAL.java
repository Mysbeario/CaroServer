package caroserver.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import caroserver.model.Account;

public class AccountDAL {
	private static Account extract(ResultSet result) throws SQLException {
		Account acc = new Account();

		acc.setId(result.getString("id"));
		acc.setEmail(result.getString("email"));
		acc.setPassword(result.getString("password"));
		acc.setFullname(result.getString("fullname"));
		acc.setGender(result.getInt("gender"));
		acc.setBirthday(result.getString("birthday"));
		acc.setScore(result.getInt("score"));

		return acc;
	}

	public static void create(Account account) {
		try {
			Connection conn = Database.connect();
			String query = "INSERT INTO account(id, email, password, fullname, gender, birthday, score) VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = conn.prepareStatement(query);

			stmt.setString(1, account.getId());
			stmt.setString(2, account.getEmail());
			stmt.setString(3, account.getPassword());
			stmt.setString(4, account.getFullname());
			stmt.setInt(5, account.getGender());
			stmt.setString(6, account.getBirthday());
			stmt.setInt(7, account.getScore());

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
			accounts.add(extract(result));
		}

		conn.close();
		return accounts;
	}

	public static Account readById(String id) throws SQLException {
		Connection conn = Database.connect();
		Account account = null;
		String query = "SELECT * FROM account WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query);

		stmt.setString(1, id);

		ResultSet result = stmt.executeQuery();

		if (result.next()) {
			account = extract(result);
		}

		conn.close();
		return account;
	}

	public static Account readByEmail(String email) throws SQLException {
		Connection conn = Database.connect();
		Account account = null;
		String query = "SELECT * FROM account WHERE email = ?";
		PreparedStatement stmt = conn.prepareStatement(query);

		stmt.setString(1, email);

		ResultSet result = stmt.executeQuery();

		if (result.next()) {
			account = extract(result);
		}

		conn.close();
		return account;
	}

	public static void update(Account account) throws SQLException {
		Connection conn = Database.connect();
		String query = "UPDATE account SET email = ?, password = ?, fullname = ?, gender = ?, birthday = ?, score = ? WHERE id = ?";
		PreparedStatement stmt = conn.prepareStatement(query);

		stmt.setString(10, account.getId());
		stmt.setString(1, account.getEmail());
		stmt.setString(2, account.getPassword());
		stmt.setString(3, account.getFullname());
		stmt.setInt(4, account.getGender());
		stmt.setString(5, account.getBirthday());
		stmt.setInt(6, account.getScore());

		stmt.executeUpdate();
		conn.close();
	}
}

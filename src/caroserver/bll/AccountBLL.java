package caroserver.bll;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import caroserver.dal.AccountDAL;
import caroserver.model.Account;

public class AccountBLL {
	private String validate(Account account) {
		Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
		Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
		LocalDate currentDate = LocalDate.now();

		if (!emailPattern.matcher(account.getEmail()).find()) {
			return "Invalid email address!";
		} else if (!passwordPattern.matcher(account.getPassword()).find()) {
			return "Password must be from 8 characters,\nat least one uppercase letter, one lowercase letter and one number!";
		} else if (account.getFullname().equals("")) {
			return "Full name is required!";
		} else if (account.getGender() < 1 || account.getGender() > 3) {
			return "Gender is required!";
		} else if (account.getBirthday().equals("")) {
			return "Birthday is required!";
		} else {
			try {
				LocalDate bd = LocalDate.parse(account.getBirthday());
				if (Period.between(bd, currentDate).getYears() < 6) {
					return "You must be 6 or older!";
				}
			} catch (Exception e) {
				return "Invalid date!";
			}
		}

		return "";
	}

	public String create(Account account) {
		String error = validate(account);

		if (error.equals("")) {
			AccountDAL.create(account);
		}

		return error;
	}

	public boolean existEmail(String email) {
		try {
			for (Account acc : AccountDAL.read()) {
				if (acc.getEmail().equals(email))
					return true;
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}

		return false;
	}
}

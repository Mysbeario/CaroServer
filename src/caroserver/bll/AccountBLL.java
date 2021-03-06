package caroserver.bll;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import caroserver.dal.AccountDAL;
import caroserver.model.Account;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AccountBLL {
    public String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(password.getBytes());
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < messageDigest.length; i++) {
            sb.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public String validateInfo(Account account) {
        Pattern emailPattern = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        LocalDate currentDate = LocalDate.now();

        if (!emailPattern.matcher(account.getEmail()).find()) {
            return "Invalid email address!";
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

    public String validatePassword(String password) {
        Pattern passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

        if (!passwordPattern.matcher(password).find()) {
            return "Password must be from 8 characters,\nat least one uppercase letter, one lowercase letter and one number!";
        }

        return "";
    }

    public String create(Account account) {
        String error = validateInfo(account);

        if (error.equals("")) {
            error = validatePassword(account.getPassword());
        }

        if (error.equals("")) {
            try {
                account.setPassword(hashPassword(account.getPassword()));
                AccountDAL.create(account);
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
        }

        return error;
    }

    public Account getByEmail(String email) throws SQLException {
        return AccountDAL.readByEmail(email);
    }

    public Account getById(String id) throws SQLException {
        return AccountDAL.readById(id);
    }

    public void update(Account account) throws SQLException {
        AccountDAL.update(account);
    }

    public int getTotalAccounts() throws SQLException {
        return AccountDAL.count();
    }
}

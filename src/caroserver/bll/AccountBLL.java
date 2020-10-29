package caroserver.bll;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

import caroserver.dal.AccountDAL;
import caroserver.model.Account;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            try {
                account.setPassword(hashPassword(account.getPassword()));
                AccountDAL.create(account);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(AccountBLL.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return error;
    }

    public boolean existEmail(String email) {
        try {
            for (Account acc : AccountDAL.read()) {
                if (acc.getEmail().equals(email)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return false;
    }

    public Account getByEmail(String email) throws SQLException {
        for (Account acc : AccountDAL.read()) {
            if (acc.getEmail().equals(email)) {
                return acc;
            }
        }
        
        return null;
    }
}

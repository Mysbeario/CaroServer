package caroserver.handler;

import caroserver.Server;
import java.security.NoSuchAlgorithmException;
import caroserver.bll.AccountBLL;
import caroserver.model.Account;
import caroserver.thread.MatchMaker;
import java.sql.SQLException;

public class AccountHandler extends HandlerBase {
    private void registerAccount(String[] data) {

        try {
            AccountBLL service = new AccountBLL();
            String email = data[0];
            String password = data[1];
            String fullname = data[2];
            int gender = Integer.parseInt(data[3]);
            String birthday = data[4];
            if (service.getByEmail(email) != null) {
                thread.response("REG_ERR:Duplicated email!");
            } else {
                Account account = new Account(email, password, fullname, gender, birthday);
                String error = service.create(account);

                if (error.equals("")) {
                    thread.response("REG_OK:Created!");
                } else {
                    thread.response("REG_ERR:" + error);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void login(String[] data) {
        try {
            AccountBLL service = new AccountBLL();
            String email = data[0];
            String password = data[1];

            if (service.getByEmail(email) == null) {
                thread.response("LOGIN_ERR:Login Failed");
            } else {
                Account account = service.getByEmail(email);

                try {
                    if (!account.getPassword().equals(service.hashPassword(password))) {
                        thread.response("LOGIN_ERR:Login Failed");
                    } else {
                        thread.setAccount(account);
                        thread.response("LOGIN_OK:" + account.toString());
                    }
                } catch (NoSuchAlgorithmException e) {
                    thread.response("LOGIN_ERR:Server failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            thread.response("LOGIN_ERR:Server failed!");
        }
    }

    private void readyAccount(String[] data) {
        if (!Server.isQueueEmpty()) {
            MatchMaker mm = new MatchMaker(thread, Server.dequeueAccount());
        } else {
            Server.queueAccount(thread);
        }
    }

    @Override
    public void handleRequest(String command, String[] data) {
        switch (command) {
            case "REG": {
                registerAccount(data);
                break;
            }
            case "LOGIN": {
                login(data);
                break;
            }
            case "RDY": {
                readyAccount(data);
                break;
            }
        }
    }
}

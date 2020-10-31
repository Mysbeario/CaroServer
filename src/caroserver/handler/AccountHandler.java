package caroserver.handler;

import caroserver.Server;
import java.security.NoSuchAlgorithmException;

import caroserver.bll.AccountBLL;
import caroserver.model.Account;
import caroserver.model.MatchMaker;
import javafx.util.Pair;

import java.sql.SQLException;

public class AccountHandler extends HandlerBase {

    private final Server server;

    public AccountHandler(Server server) {
        this.server = server;
    }

    private void registerAccount(String[] data) {
        AccountBLL service = new AccountBLL();
        String email = data[0];
        String password = data[1];
        String fullname = data[2];
        int gender = Integer.parseInt(data[3]);
        String birthday = data[4];
        if (service.existEmail(email)) {
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
    }

    private void login(String[] data) {
        AccountBLL service = new AccountBLL();
        String email = data[0];
        String password = data[1];

        System.out.println(String.join("-", email, password));

        if (!service.existEmail(email)) {
            thread.response("LOGIN_ERR:Login Failed");
        } else {
            try {
                Account account = service.getByEmail(email);

                try {
                    if (!account.getPassword().equals(service.hashPassword(password))) {
                        thread.response("LOGIN_ERR:Login Failed");
                    } else {
                        thread.response("LOGIN_OK:" + account.toString());
                        if (server.getActiveAccounts().isEmpty()) {
                            server.queueAccount(thread, account);
                        } else {
                            MatchMaker mm = new MatchMaker(new Pair<>(thread, account),
                                    server.getActiveAccounts().poll());
                        }
                    }
                } catch (NoSuchAlgorithmException e) {
                    thread.response("LOGIN_ERR:Server failed!");
                }
            } catch (SQLException e) {
                thread.response("LOGIN_ERR:Server failed!");
            }
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
        }
    }
}

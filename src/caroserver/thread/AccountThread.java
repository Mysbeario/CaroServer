package caroserver.thread;

import caroserver.Server;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import caroserver.bll.AccountBLL;
import caroserver.model.Account;
import java.sql.SQLException;

public class AccountThread extends ClientThread {

    private final Server server;

    public AccountThread(Socket socket, Server server) {
        super(socket);
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
            response("REG_ERR:Duplicated email!");
        } else {
            Account account = new Account(email, password, fullname, gender, birthday);
            String error = service.create(account);

            if (error.equals("")) {
                response("REG_OK:ok");
            } else {
                response("REG_ERR:" + error);
            }
        }
    }

    private void login(String[] data) {
        AccountBLL service = new AccountBLL();
        String email = data[0];
        String password = data[1];

        System.out.println(String.join("-", email, password));

        if (!service.existEmail(email)) {
            response("LOGIN_ERR:Email not found!");
        } else {
            try {
                Account account = service.getByEmail(email);

                try {
                    if (!account.getPassword().equals(service.hashPassword(password))) {
                        response("LOGIN_ERR:Login Failed");
                    } else {
                        server.logAccountIn(account);
                        response("LOGIN_OK:ok");
                    }
                } catch (NoSuchAlgorithmException e) {
                    response("LOGIN_ERR:Server failed!");
                }
            } catch (SQLException e) {
                response("LOGIN_ERR:Server failed!");
            }
        }
    }

    @Override
    protected void handleRequest(String command, String[] data) {
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

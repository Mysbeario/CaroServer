package caroserver.handler;

import caroserver.Server;
import java.security.NoSuchAlgorithmException;
import caroserver.bll.AccountBLL;
import caroserver.component.MatchMaker;
import caroserver.model.Account;

import java.sql.SQLException;
import java.util.ArrayList;

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
                thread.response("REGISTER_ERROR:Duplicated email!");
            } else {
                Account account = new Account(email, password, fullname, gender, birthday);
                String error = service.create(account);

                if (error.equals("")) {
                    thread.response("REGISTER_OK:Created!");
                } else {
                    thread.response("REGISTER_ERROR:" + error);
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
                thread.response("LOGIN_ERROR:Login Failed");
            } else {
                Account account = service.getByEmail(email);

                try {
                    if (!account.getPassword().equals(service.hashPassword(password))) {
                        thread.response("LOGIN_ERROR:Login Failed");
                    } else {
                        thread.setAccount(account);
                        thread.response("LOGIN_OK:" + account.toString());
                    }
                } catch (NoSuchAlgorithmException e) {
                    thread.response("LOGIN_ERROR:Server failed!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            thread.response("LOGIN_ERROR:Server failed!");
        }
    }

    private void getGameList() {
        ArrayList<String> gameInfos = new ArrayList<>();

        Server.getGameList().forEach((k, v) -> {
            gameInfos.add(k + "," + String.join(",", v.getPlayerNames()));
        });
        thread.response("GAME_LIST:" + (gameInfos.isEmpty() ? ";" : String.join(";", gameInfos)));
    }

    private void readyAccount(String[] data) {
        if (!Server.isQueueEmpty()) {
            new MatchMaker(thread, Server.dequeueAccount());
        } else {
            Server.queueAccount(thread);
            getGameList();
        }
    }

    @Override
    public void handleRequest(String command, String[] data) {
        switch (command) {
            case "REGISTER": {
                registerAccount(data);
                break;
            }
            case "LOGIN": {
                login(data);
                break;
            }
            case "READY": {
                readyAccount(data);
                break;
            }
            case "SPECTATE": {
                Server.getGameList().get(data[0]).addSpectator(thread);
                break;
            }
            case "REFRESH": {
                getGameList();
                break;
            }
        }
    }
}

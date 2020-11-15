package caroserver.handler;

import caroserver.Server;

import java.security.NoSuchAlgorithmException;
import caroserver.bll.AccountBLL;
import caroserver.bll.AchievementBLL;
import caroserver.component.MatchMaker;
import caroserver.model.Account;
import caroserver.model.Achievement;

import java.sql.SQLException;
import java.util.ArrayList;

public class AccountHandler extends HandlerBase {
    private void registerAccount(String[] data) {

        try {
            AccountBLL service = new AccountBLL();
            String email = data[0];
            String fullname = data[1];
            int gender = Integer.parseInt(data[2]);
            String birthday = data[3];
            String password = data[4];
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

    private void getAchievement(String playerId) {
        try {
            AchievementBLL service = new AchievementBLL();
            Achievement achi = service.get(playerId);

            thread.response("ACHIEVEMENT:" + achi.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void readyAccount(String[] data) {
        if (!Server.isQueueEmpty()) {
            new MatchMaker(thread, Server.dequeueAccount());
        } else {
            Server.queueAccount(thread);
            getGameList();
        }
    }

    private void changePassword(String[] data) {
        try {
            AccountBLL service = new AccountBLL();
            String password = data[0];
            String newPassword = data[1];
            Account account = thread.getAccount();

            try {
                if (!account.getPassword().equals(service.hashPassword(password))) {
                    thread.response("CHANGE_PASSWORD_ERROR:Wrong password!");
                } else {
                    account.setPassword(service.hashPassword(newPassword));
                    service.update(account);
                    thread.response("CHANGE_PASSWORD_OK:Password changed");
                }
            } catch (NoSuchAlgorithmException e) {
                thread.response("CHANGE_PASSWORD_ERROR:Server failed!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            thread.response("CHANGE_PASSWORD_ERROR:Server failed!");
        }
    }

    private void changeInformation(String[] data) {
        try {
            AccountBLL service = new AccountBLL();
            Account account = thread.getAccount();

            account.setEmail(data[0]);
            account.setFullname(data[1]);
            account.setGender(Integer.parseInt(data[2]));
            account.setBirthday(data[3]);

            String error = service.validateInfo(account);

            if (error.equals("")) {
                service.update(account);
                thread.response("UPDATE_OK:" + account.toString());
            } else {
                thread.response("UPDATE_ERROR" + error);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            case "UPDATE": {
                changeInformation(data);
                break;
            }
            case "CHANGE_PASSWORD": {
                changePassword(data);
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
            case "ACHIEVEMENT": {
                getAchievement(data[0]);
                break;
            }
        }
    }
}

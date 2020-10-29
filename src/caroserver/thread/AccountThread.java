package caroserver.thread;

import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import caroserver.bll.AccountBLL;
import caroserver.model.Account;

public class AccountThread extends ClientThread {
	public AccountThread(Socket socket) {
		super(socket);
	}

	private String hashPassword(String password) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] messageDigest = md.digest(password.getBytes());
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < messageDigest.length; i++) {
			sb.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
		}

		return sb.toString();
	}

	private void registerAccount(String[] data) {
		try {
			AccountBLL service = new AccountBLL();
			String email = data[0];
			String password = data[1];
			String fullname = data[2];
			int gender = Integer.parseInt(data[3]);
			String birthday = data[4];

			if (service.existEmail(email)) {
				response("REG_ERR:Duplicated email!");
			} else {
				Account account = new Account(email, hashPassword(password), fullname, gender, birthday);
				String error = service.create(account);

				if (error.equals("")) {
					response("REG_OK");
				} else {
					response("REG_ERR:" + error);
				}
			}
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	protected void handleRequest(String command, String[] data) {
		switch (command) {
			case "REG": {
				registerAccount(data);
				break;
			}
		}
	}
}

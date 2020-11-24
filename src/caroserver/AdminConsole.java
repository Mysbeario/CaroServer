package caroserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Scanner;
import caroserver.bll.AccountBLL;
import caroserver.bll.AchievementBLL;
import caroserver.bll.MatchHistoryBLL;
import caroserver.model.Account;
import caroserver.model.Achievement;

public class AdminConsole implements Runnable {
	private Scanner scanner = new Scanner(System.in);

	private void blockAccount(String email) {
		try {
			AccountBLL service = new AccountBLL();
			Account account = service.getByEmail(email);

			if (account != null) {
				account.setIsBlocked(true);
				service.update(account);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String input = "";

		while (!input.equals("STOP")) {
			input = scanner.nextLine();
			String[] parts = input.split(":");

			switch (parts[0].toUpperCase()) {
				case "BLOCK": {
					blockAccount(parts[1]);
					System.out.println("Account blocked!");
					break;
				}
				case "STATUS": {
					try {
						AccountBLL service = new AccountBLL();
						int total = service.getTotalAccounts();
						int online = Server.countOnlineAccount();

						System.out.println(online + "/" + total + " online");
					} catch (SQLException e) {
						e.printStackTrace();
					}

					break;
				}
				case "GET": {
					switch (parts[1].toUpperCase()) {
						case "MOSTWIN": {
							try {
								AccountBLL accService = new AccountBLL();
								AchievementBLL achiService = new AchievementBLL();
								MatchHistoryBLL historyService = new MatchHistoryBLL();
								String playerId = historyService.getPlayerIdWithMostWin();
								Achievement achi = achiService.get(playerId);
								Account acc = accService.getById(playerId);

								System.out.println(acc.getFullname() + " with " + achi.getWin() + " wins.");
							} catch (SQLException e) {
								e.printStackTrace();
							}

							break;
						}
						case "SHORTESTMATCH": {
							try {
								Scanner fileReader = new Scanner(new File("./game_log.txt"));
								String p1Name = "";
								String p2Name = "";
								String date = "";
								long time = 10 * 60 * 1000 + 1;

								while (fileReader.hasNext()) {
									String[] info = fileReader.nextLine().split(",");
									long matchTime = Long.parseLong(info[3]);

									if (matchTime < time) {
										time = matchTime;
										p1Name = info[0];
										p2Name = info[1];
										date = info[2];
									}
								}

								System.out.println(p1Name + " .vs " + p2Name + " at " + date + " only took "
										+ Math.round((time / 1000d)) + " seconds");
								fileReader.close();
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}
}

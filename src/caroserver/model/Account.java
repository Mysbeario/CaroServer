package caroserver.model;

import java.util.UUID;

public class Account {
	private String id;
	private String email;
	private String password;
	private String fullname;
	private int gender;
	private String birthday;
	private int score = 0;
	private boolean isBlocked = false;

	public Account() {
	}

	public Account(String email, String password, String fullname, int gender, String birthday) {
		id = UUID.randomUUID().toString();
		this.email = email;
		this.password = password;
		this.fullname = fullname;
		this.gender = gender;
		this.birthday = birthday;
	}

	public boolean isBlocked() {
		return isBlocked;
	}

	public String getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getFullname() {
		return fullname;
	}

	public int getGender() {
		return gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public int getScore() {
		return score;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public void setScore(int score) {
		this.score = score < 0 ? 0 : score;
	}

	public void setIsBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String toString() {
		return String.join(";", getId(), getEmail(), getFullname(), Integer.toString(getGender()), getBirthday());
	}
}

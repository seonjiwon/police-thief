package police_theif.vault;

public class Vault {
	private int balance; // 현재 잔액
	private int totalStolen; // 총 도난액 누적
	private int totalRecovered; // 총 회수액 누적
	private final int x = 10; // 금고 X 좌표
	private final int y = 10; // 금고 Y 좌표

	// 생성자
	public Vault(int initialBalance) {
		if (initialBalance < 0)
			initialBalance = 0;
		this.balance = initialBalance;
		this.totalStolen = 0;
		this.totalRecovered = 0;
	}

	// 메서드
	public synchronized void steal(int amount) {
		if (amount <= 0)
			return;
		if (balance >= amount) {
			balance -= amount;
			totalStolen += amount;
		}
	}

	// 체포 시 금액 회수
	public synchronized void recover(int amount) {
		if (amount <= 0)
			return;
		balance += amount;
		totalRecovered += amount;
	}

	// 현재 잔액 조회
	public synchronized int getBalance() {
		return balance;
	}

	// 총 도난액 조회
	public synchronized int getTotalStolen() {
		return totalStolen;
	}

	// 총 회수액 조회
	public synchronized int getTotalRecovered() {
		return totalRecovered;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}

package police_thief.thief;

import java.util.Random;

import police_thief.vault.Vault;

public class Thief implements Runnable{
	// Thread(Thief-i)
	private int id; // 도둑 고유번호
	private Vault vault; // 금고
	private boolean caught = false; // 실행 제어
	private int stolenAmount; // 훔친 가격
	
	// x, y 좌표
	private int x;
	private int y;
	
	private final Object positionLock = new Object();
	private Random random;
	
	// 생성자
	public Thief(int id, Vault vault) {
		this.id = id;
		this.vault = vault;
		this.stolenAmount = 0;
		this.random = new Random();
		
		// 시작 위치 랜덤으로 지정
		this.x = random.nextInt(21);
		this.y = random.nextInt(21);
	}
	
	
	@Override
	public void run() {
		while(!caught) {
			try {
				// 이동
				move();
				
				int[] currentPos = getPosition();
				int currentX = currentPos[0];
				int currentY = currentPos[1];
				
				// 금고 위치 확인
				if(currentX == vault.getX()
					&& currentY == vault.getY()) {
					// 훔칠 가격(100 ~ 1000)
					int amount = random.nextInt(901) +100;
					vault.steal(amount); // synchronized
					stolenAmount += amount;
					System.out.println("Thief-"+id+"이(가) "
							+amount+"원을 훔쳤습니다!");
				}
				
				Thread.sleep(500); // 대기
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
	
	private void move() {
		// 도둑 랜덤 이동
		int direction = random.nextInt(4);
		
		// 값 객체 락
		synchronized(positionLock) {
			switch(direction) {
			case 0: // 위로 이동
				if (y >0) y--;
				break;
			case 1: // 아래 이동
				if(y<20) y++;
				break;
			case 2: // 왼쪽 이동
				if (x>0) x--;
				break;
			case 3: // 오른쪽 이동
				if(x<20) x++;
				break;
			}
		}
	}
	
	public void arrest() { // 체포 여부
		caught = true;
		System.out.println("Thief-" + id + " 체포되었습니다.");
	}
	
	/// getter 선언
	public boolean isCaught() { // 잡힌 여부
		return caught;
	}
	
	public int[] getPosition() { // 위치 획득
		// 각 도둑객체 위치 보호
		synchronized(positionLock) { // 락이 된 순간 x,y 복사
			return new int[] {x,y}; // 락 해제
		}
	}
	
	// 개별 좌표 읽기
	public int getX() {
		synchronized (positionLock) {
			return x;
		}
	}
	public int getY() {
		synchronized (positionLock) {
			return y;
		}
	}
	// 훔친 가격 인스턴스
	public int getStolenAmount() {
		return stolenAmount;
	}
	// 도둑 id값 반환
	public int getId() {
		return id;
	}
	
}

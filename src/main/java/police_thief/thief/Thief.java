package police_thief.thief;

import java.util.Random;

import police_thief.vault.Vault;

public class Thief implements Runnable{
	// Thread(Thief-i)
	private int id; // 도둑 고유번호
	private Vault vault; // 금고
	private int stolenAmount; // 훔친 가격
	private volatile ThiefState state; // 상태변수(훔쳤는지 여부)
	
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
		// 처음에는 금고로 이동하는 상태로 시작
		this.state = ThiefState.MOVING_TO_VAULT;
		
		// 시작 위치 랜덤으로 지정
		this.x = random.nextInt(21);
		this.y = random.nextInt(21);
	}
	
	// 도둑의 상태 정의하는 열거형
	public enum ThiefState {
		MOVING_TO_VAULT("금고로 이동 중"),
		ACTIVE("활동 중(랜덤 이동)"),
		ARRESTED("체포됨");
		
		private final String description;
		ThiefState(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}
	
	@Override
	public void run() {
		while(state != ThiefState.ARRESTED) {
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
					int amount = random.nextInt(9001) +1000;
					int stolen = vault.steal(amount);
					if (stolen > 0) {
						synchronized (positionLock) {
							stolenAmount += stolen;
						}
						System.out.println("Thief-" + id + "이(가) " + stolen + "원을 훔쳤습니다!");
					}
				}
				
				Thread.sleep(500); // 대기
				
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
	
	private void move() {
		synchronized (positionLock) {
			switch(this.state) {
			// 처음에는 금고 위치 파악 후 최단거리로 이동
			case MOVING_TO_VAULT:
				moveToTarget(vault.getX(), vault.getY());
				
				if(x == vault.getX() 
					&& y == vault.getY()) {
					this.state = ThiefState.ACTIVE;
				}
				break;
			// 금고 도착 이후에는 랜덤하게 이동
			case ACTIVE:
				moveRandom();
				break;
			case ARRESTED:
				break;
			}
		}
		
	}
	
	// 금고로 이동시키기(유도)
	private void moveToTarget(int targetX, int targetY) {
		if(x <targetX) x++;
		else if (x > targetX) x--;
        else if (y < targetY) y++;
        else if (y > targetY) y--;
    }
	
	// 랜덤으로 이동시키기(훔친 이후 적용)
	private void moveRandom() {
		int direction = random.nextInt(4);
        switch (direction) {
            case 0: if (y > 0) y--; break;
            case 1: if (y < 20) y++; break;
            case 2: if (x > 0) x--; break;
            case 3: if (x < 20) x++; break;
        }
	}
	
	
	public void arrest() { // 체포 여부
		this.state = state.ARRESTED;
		System.out.println("Thief-" + id + " 체포되었습니다.");
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
		synchronized (positionLock) {
	        return stolenAmount;
	    }
	}
	// 도둑 id값 반환
	public int getId() {
		return id;
	}
	
	public ThiefState getState() {
		return state;
	}
	
}

package police_thief.police;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import police_thief.thief.Thief;

public class Police implements Runnable {
	// 도둑의 위치를 확인하기 위한 도둑 List
	private int id;
	private List<Thief> thieves;

	private AtomicInteger arrestCount;

	// 경찰의 x, y 좌표
	private int x;
	private int y;

	// 경찰이 움질익 수 있는 최대 좌표
	private int minX;
	private int maxX;
	private int minY;
	private int maxY;

	private Random random;

	

	public Police(int id, List<Thief> thieves, int x, int y, int minX, int maxX, int minY, int maxY) {
		super();
		this.id = id;
		this.thieves = thieves;
		this.arrestCount = new AtomicInteger(0);
		this.x = x;
		this.y = y;
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
		this.random = new Random();
		
		System.out.println("[ Police" + id + "] 생성 - " + "시작: (" + x + "," + y + "), " + "구역: (" + minX + "," + minY
				+ ")~(" + maxX + "," + maxY + ")");
	}

	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();

		while (true) {
			try {
				// 1. 순찰 구역내에 가장 가까운 도둑을 찾는다.
				Thief target = findNearestThief();

				if (target != null) {
					// 2. 현재 경찰의 포지션을 가져옴
					int[] currentPosition = getPosition();

					System.out.println("[ Police" + id + "] " + "Thief-" + target + " 추적 중... " + "("
							+ currentPosition[0] + "," + currentPosition[1] + ")");

					// 3. 도둑 쪽으로 이동
					moveToward(target);

					// 4. 동일한 위치면 체포
					if (isSamePosition(target)) {
						target.arrest();
						arrestCount.incrementAndGet();

						int[] arrestPosition = getPosition();
						System.out.println(
								"[" + threadName + "] Thief-" + target.getId() + " 체포! " + "위치: (" + arrestPosition[0]
										+ "," + arrestPosition[1] + ") " + "[총: " + arrestCount.get() + "회]");
					}

				} else {
					patrol();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 순찰 구역 내에 가장 가까운 Thief 를 찾는 함수
	 * 
	 * @return
	 */
	private Thief findNearestThief() {
		Thief nearest = null;
		int minDistance = Integer.MAX_VALUE;

		int[] policePosition = getPosition();
		int policeX = policePosition[0];
		int policeY = policePosition[1];

		for (Thief thief : thieves) {
			if (thief.isCaught()) {
				continue;
			}

			int[] thiefPosition = thief.getPosition();
			int thiefX = thiefPosition[0];
			int thiefY = thiefPosition[1];

			if (thiefX >= minX && thiefX <= maxX && thiefY >= minY && thiefY <= maxY) {

				int distance = Math.abs(policeX - thiefX) + Math.abs(policeY - thiefY);

				if (distance < minDistance) {
					minDistance = distance;
					nearest = thief;
				}
			}
		}

		return nearest;
	}

	/**
	 * 현재 포지션을 가져옴 좌표 값이 변경되기 때문에 Thread Safe 하게 가져오기 위해 synchronized 메서드 사용
	 * 
	 * @return
	 */
	public synchronized int[] getPosition() {
		return new int[] { x, y };
	}

	/**
	 * 도둑이 있는 곳으로 다가감
	 * 
	 * @param thief
	 */
	private synchronized void moveToward(Thief target) {
		int[] targetPosition = target.getPosition();
        int targetX = targetPosition[0];
        int targetY = targetPosition[1];
        
        if (Math.abs(x - targetX) > Math.abs(y - targetY)) {
            if (x < targetX && x < maxX) {
                x++;
            } else if (x > targetX && x > minX) {
                x--;
            }
        } else {
            if (y < targetY && y < maxY) {
                y++;
            } else if (y > targetY && y > minY) {
                y--;
            }
        }
	}

	/**
	 * 도둑과 같은 포지션인지 확인
	 * 
	 * @param thief
	 */
	private boolean isSamePosition(Thief target) {
		int[] policePosition = getPosition();
        int[] thiefPosition = target.getPosition();
        
        return policePosition[0] == thiefPosition[0] && policePosition[1] == thiefPosition[1];
	}

	/**
	 * 순찰
	 */
	private synchronized void patrol() {
		int direction = random.nextInt(4);

		switch (direction) {
			case 0:
				if (y > minY)
					y--;
				break;
			case 1:
				if (y < maxY)
					y++;
				break;
			case 2:
				if (x > minX)
					x--;
				break;
			case 3:
				if (x < maxX)
					x++;
				break;
		}
	}
	
	public int getArrestCount() {
		return arrestCount.get();
	}
}

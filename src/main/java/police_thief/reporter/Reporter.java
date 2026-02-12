package police_thief.reporter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;

public class Reporter implements Runnable {
	// === 변수 ===
	private Vault vault; // 금고 참조
	private List<Thief> thieves; // 도둑 목록 참조
	private Police police; // 경찰 참조
	private static final int MAP_SIZE = 21; // 맵 크기 (0~20 좌표)
		
	// === 생성자 ===

	public Reporter(Vault vault, List<Thief> thieves, Police police) {
		this.vault = vault;
		this.thieves = thieves;
		this.police = police;
	}

	// === 메서드 ===
	/**
	* 스레드 메인 로직
	* 1. 무한 반복
	* 2. printMap() 호출
	* 3. 0.5초 대기 (Thread.sleep)
	*/
	@Override
	public void run() {
		
		// 무한 반복
		while(!Thread.currentThread().isInterrupted()) { // 인터럽트 신호 받았는지 확인
			try {
				printMap(); // printMap() 호출
				Thread.sleep(500); // 0.5초 대기 (Thread.sleep)
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();  // 인터럽트 상태 복구
			}
		}
	}
		/**
		 * 21x21 2D 맵 출력
		 * 1. clearScreen() 호출 (화면 지우기)
		 * 2. 상단 통계 출력:
		 * - 현재 시간
		 * - 금고 잔액
		 * - 체포 횟수
		 * - 총 도난액
		 * 3. 21x21 사각형 맵 그리기:
		 * - 2중 for문 (y: 0~20, x: 0~20)
		 * - getSymbol(x, y) 호출하여 심볼 출력
		 * - 심볼 뒤에 공백 추가
		 * 4. 하단 범례 출력
		 * 5. 각 도둑 상세 정보 출력:
		 * - ID, 상태, 좌표, 훔친 금액
		 * 6. 경찰 위치 출력
		 */
	private void printMap() {
		
		// clearScreen() 호출 (화면 지우기)
		clearScreen();
		
		// 상단 통계 출력
		System.out.println("===== Police vs Thief Simulation =====");
	    System.out.println("현재 시간: " + getCurrentTime());
	    System.out.println("금고 잔액: " + vault.getBalance());
	    System.out.println("체포 횟수: " + police.getArrestCount());
	    System.out.println("총 도난액: " + vault.getTotalStolen());
	    System.out.println();
	    
	    // 21x21 사각형 맵 그리기
	    for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                System.out.print(getSymbol(x, y) + " ");
            }
            System.out.println();
        }
	    
	    // 하단 범례 출력
	    System.out.println();
	    System.out.println("V: 금고 | P: 경찰 | 숫자: 도둑 | X: 체포");
	    System.out.println();
	    
	    // 각 도둑 상세 정보 출력
	    for (Thief thief : thieves) {
            System.out.println("도둑 " + thief.getId()
                    + " | 상태: " + thief.isCaught()
                    + " | 위치: (" + thief.getX() + "," + thief.getY() + ")"
                    + " | 훔친 금액: " + thief.getStolenAmount());
        }
	    
	    // 경찰 위치 출력
	    System.out.println();
        System.out.println("경찰 위치: (" + police.getX() + "," + police.getY() + ")");
	    
	        
	}
		/**
		 * (x, y) 좌표에 표시할 심볼 결정
		 * - 금고 위치면: 'V'
		 * - 경찰 위치면: 'P'
		 * - 도둑 위치면:
		 * * 체포됨: 'X'
		 * * 활동중: 도둑 ID (1, 2, 3)
		 * - 빈 공간: '□'
		 * @param x X 좌표
		 * @param y Y 좌표
		 * @return 표시할 문자
		 */
	private char getSymbol(int x, int y) {
		// 금고 위치 'V' 심볼 반환 
		if (vault.getX() == x && vault.getY() == y) {
            return 'V';
        }
		
		// 경찰 위치 'P' 심볼 반환 
		if (police.getX() == x && police.getY() == y) {
            return 'P';
        }
		
		// 도둑 위치 심볼 반환 
        for (Thief thief : thieves) {
            if (thief.getX() == x && thief.getY() == y) {
            	
            	// 체포된 상태면 'X' 반환 
                if (thief.isCaught()) {
                    return 'X';
                }
                // 활동중: 도둑 ID (1, 2, 3)
                return Character.forDigit(thief.getId(), 10);
            }
        }
        
        // 빈 공간
        return '□';
	}
		/**
		* 콘솔 화면 지우기
		* - ANSI 이스케이프 코드 사용
		* - "\033[H\033[2J" 출력
		* - System.out.flush() 호출
		*/
	
	// ANSI 이스케이프 코드를 사용해 화면을 지우고, flush()로 즉시 반영
	private void clearScreen() { // 
		System.out.print("\033[H\033[2J"); // ANSI 이스케이프 코드를 출력
        System.out.flush();  // 출력 버퍼를 즉시 비우는 역할
	}
		/**
		 * 현재 시간 문자열 반환
		 * - SimpleDateFormat 사용
		 * @return "HH:mm:ss" 형식 문자열
		 */
	private String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date());
	}
	
}


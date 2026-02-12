package police_thief;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;
import police_thief.reporter.Reporter;

public class Main {
    public static void main(String[] args) {
        // 1. 초기 설정: 금고 생성 (초기 잔액 100,000원)
        Vault vault = new Vault(100000);
        Random random = new Random();
        
        // 2. 도둑 리스트 생성 및 초기화 (3명)
        List<Thief> thieves = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            thieves.add(new Thief(i, vault));
        }

        // 3. 경찰 생성
        // 순찰 구역은 맵 전체(0~20)로 설정하고, 시작 위치는 (0,0)으로 지정합니다.
        List<Police> polices = new ArrayList<>();
        polices.add(new Police(1, thieves, 0, 10, 0, 10));    // 좌상
        polices.add(new Police(2, thieves, 11, 20, 0, 10));   // 우상
        polices.add(new Police(3, thieves, 0, 10, 11, 20));   // 좌하
        polices.add(new Police(4, thieves, 11, 20, 11, 20));  // 우하

        

        // 4. 리포터(중계원) 생성
        Reporter reporter = new Reporter(vault, thieves, polices);

        System.out.println("시뮬레이션 준비 완료. 스레드를 시작합니다...");

        // 5. 각 역할을 수행할 스레드 시작
        
        // 도둑 스레드 시작
        for (Thief thief : thieves) {
            new Thread(thief, "Thief-" + thief.getId()).start();
        }

        // 경찰 스레드 시작
        for (Police police : polices) {
            new Thread(police, "Police-" + police.getId()).start();
        }

        // 리포터 스레드 시작 (화면 출력 담당)
        Thread reporterThread = new Thread(reporter, "Reporter-Thread");
        reporterThread.start();
        
        System.out.println("시뮬레이션이 진행 중입니다. 콘솔을 확인하세요!");
    }
}
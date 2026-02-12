package police_thief.gui;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;

public class GuiMain {
	public static void main(String[] args) {
		// 1) 모델 생성
		Vault vault = new Vault(100000);

		List<Thief> thieves = new ArrayList<>();
		for (int i = 1; i <= 15; i++) {
			thieves.add(new Thief(i, vault));
		}

		// 2) 경찰 4명 (Police 생성자 8파라미터 버전)
		List<Police> polices = new ArrayList<>();
		polices.add(new Police(1, thieves, 0, 10, 0, 10));      // 좌상
		polices.add(new Police(2, thieves, 11, 20, 0, 10));     // 우상
		polices.add(new Police(3, thieves, 0, 10, 11, 20));     // 좌하
		polices.add(new Police(4, thieves, 11, 20, 11, 20));    // 우하

		// 3) 스레드 시작
		for (Thief t : thieves)
			new Thread(t, "Thief-" + t.getId()).start();
		for (Police p : polices)
			new Thread(p, "Police-" + p.getId()).start();

		// 4) GUI 실행 (EDT)
		SwingUtilities.invokeLater(() -> {
			SimulationFrame frame = new SimulationFrame(vault, thieves, polices);
			frame.setVisible(true);
		});
	}
}

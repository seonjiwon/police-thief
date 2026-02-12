package police_thief.gui;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;

public class GuiMain {
    public static void main(String[] args) {
        // 1) 로직 객체 생성
        Vault vault = new Vault(100000);

        List<Thief> thieves = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            thieves.add(new Thief(i, vault));
        }

        List<Police> polices = new ArrayList<>();
        // Police 생성자(8개 파라미터) 버전으로 맞춤
        polices.add(new Police(1, thieves, 0, 0, 0, 10, 0, 10));
        polices.add(new Police(2, thieves, 11, 0, 11, 20, 0, 10));
        polices.add(new Police(3, thieves, 0, 11, 0, 10, 11, 20));
        polices.add(new Police(4, thieves, 11, 11, 11, 20, 11, 20));

        // 2) 스레드 시작 (시뮬레이션)
        for (Thief t : thieves) new Thread(t, "Thief-" + t.getId()).start();
        for (Police p : polices) new Thread(p, "Police-" + p.getId()).start();

        // 3) GUI 생성/표시 (EDT에서)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Police & Thief Simulation (GUI)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MapPanel panel = new MapPanel(vault, thieves, polices);
            frame.setContentPane(panel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // 0.2초마다 화면 갱신
            new Timer(200, e -> panel.repaint()).start();
        });
    }
}

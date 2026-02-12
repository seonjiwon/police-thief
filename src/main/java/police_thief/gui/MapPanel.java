package police_thief.gui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;

public class MapPanel extends JPanel {
    private final Vault vault;
    private final List<Thief> thieves;
    private final List<Police> polices;

    private static final int MAP_SIZE = 21;
    private static final int CELL = 24; // 칸 크기(px)
    private static final int OFFSET_Y = 40;

    public MapPanel(Vault vault, List<Thief> thieves, List<Police> polices) {
        this.vault = vault;
        this.thieves = thieves;
        this.polices = polices;
        setPreferredSize(new Dimension(MAP_SIZE * CELL, OFFSET_Y + MAP_SIZE * CELL + 30));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 상단 정보
        int totalArrests = 0;
        for (Police p : polices) totalArrests += p.getArrestCount();

        g.drawString("Balance: " + vault.getBalance()
                + " | Stolen: " + vault.getTotalStolen()
                + " | Recovered: " + vault.getTotalRecovered()
                + " | Arrests: " + totalArrests, 10, 18);

        // 격자
        for (int y = 0; y < MAP_SIZE; y++) {
            for (int x = 0; x < MAP_SIZE; x++) {
                int px = x * CELL;
                int py = OFFSET_Y + y * CELL;
                g.drawRect(px, py, CELL, CELL);
            }
        }

        // 금고
        drawText(g, vault.getX(), vault.getY(), "V");

        // 경찰
        for (Police p : polices) {
            int[] pos = p.getPosition();
            drawText(g, pos[0], pos[1], "P");
        }

        // 도둑
        for (Thief t : thieves) {
            String s = t.isCaught() ? "X" : String.valueOf(t.getId());
            drawText(g, t.getX(), t.getY(), s);
        }

        // 하단 범례
        g.drawString("V=Vault, P=Police, 1~9=Thief, X=Caught", 10, OFFSET_Y + MAP_SIZE * CELL + 18);
    }

    private void drawText(Graphics g, int x, int y, String text) {
        int px = x * CELL;
        int py = OFFSET_Y + y * CELL;

        // 글자 위치를 셀 가운데로 대충 맞춤
        g.drawString(text, px + CELL / 2 - 4, py + CELL / 2 + 5);
    }
}

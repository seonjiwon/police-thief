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
	private static final int CELL = 28;

	// 축/여백
	private static final int LEFT_PAD = 55; // y축 숫자 + " | " 자리
	private static final int TOP_PAD = 35; // 제목/상단 여백
	private static final int BOTTOM_PAD = 45; // x축 숫자 자리
	private static final int RIGHT_PAD = 15;

	private final Font font = new Font("Dialog", Font.PLAIN, 14);
	private final Font mono = new Font("Monospaced", Font.PLAIN, 13);
	private final Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);

	public MapPanel(Vault vault, List<Thief> thieves, List<Police> polices) {
		this.vault = vault;
		this.thieves = thieves;
		this.polices = polices;

		int w = LEFT_PAD + MAP_SIZE * CELL + RIGHT_PAD;
		int h = TOP_PAD + MAP_SIZE * CELL + BOTTOM_PAD;
		setPreferredSize(new Dimension(w, h));
		setBackground(Color.WHITE);
	}

	@Override
	protected void paintComponent(Graphics g0) {
		super.paintComponent(g0);

		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// 상단 제목
		g.setFont(font);
		g.drawString("Police vs Thief Simulation", 10, 20);

		// 그리드 영역 시작점
		int gridX0 = LEFT_PAD;
		int gridY0 = TOP_PAD;

		// y축 라벨 + " | "
		g.setFont(mono);
		for (int y = MAP_SIZE - 1; y >= 0; y--) {
			int row = (MAP_SIZE - 1) - y; // 화면 row: 0이 y=20
			int cy = gridY0 + row * CELL + CELL / 2 + 5;

			String label = String.format("%2d |", y);
			g.drawString(label, 10, cy);
		}

		// 격자 그리기
		g.setColor(new Color(220, 220, 220));
		for (int r = 0; r <= MAP_SIZE; r++) {
			int y = gridY0 + r * CELL;
			g.drawLine(gridX0, y, gridX0 + MAP_SIZE * CELL, y);
		}
		for (int c = 0; c <= MAP_SIZE; c++) {
			int x = gridX0 + c * CELL;
			g.drawLine(x, gridY0, x, gridY0 + MAP_SIZE * CELL);
		}

		// 셀 심볼 그리기
		g.setFont(emojiFont);
		g.setColor(Color.BLACK);

		for (int y = MAP_SIZE - 1; y >= 0; y--) {
			for (int x = 0; x < MAP_SIZE; x++) {
				String symbol = getSymbol(x, y);

				int row = (MAP_SIZE - 1) - y;
				int col = x;

				int px = gridX0 + col * CELL + CELL / 2 - 6;
				int py = gridY0 + row * CELL + CELL / 2 + 6;

				g.drawString(symbol, px, py);
			}
		}

		// x축 라벨 (0~20)
		g.setFont(mono);
		g.setColor(Color.BLACK);
		int xLabelY = gridY0 + MAP_SIZE * CELL + 25;
		g.drawString("     ", 10, xLabelY); // 왼쪽 여백 느낌만
		for (int x = 0; x < MAP_SIZE; x++) {
			int px = gridX0 + x * CELL + CELL / 2 - 4;
			g.drawString(String.valueOf(x), px, xLabelY);
		}

		g.dispose();
	}

	/**
	 * Reporter의 우선순위를 그대로 가져옴: 금고 > 경찰 > 도둑(체포) > 도둑(활동) > 빈칸
	 */
	private String getSymbol(int x, int y) {
		// 금고
		if (vault.getX() == x && vault.getY() == y)
			return "\uD83D\uDCB0"; // 💰

		// 경찰
		for (Police p : polices) {
			int[] pos = p.getPosition();
			if (pos[0] == x && pos[1] == y)
				return "👮";
		}

		// 도둑
		for (Thief t : thieves) {
			if (t.getX() == x && t.getY() == y) {
				if (t.getState() == Thief.ThiefState.ARRESTED)
					return "\u274C"; // ❌
				return "\uD83E\uDD77"; // 🥷
			}
		}

		// 빈칸
		return "⬜";
	}
}

package police_thief.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import police_thief.police.Police;
import police_thief.thief.Thief;
import police_thief.vault.Vault;

public class InfoPanel extends JPanel {
	private final Vault vault;
	private final List<Thief> thieves;
	private final List<Police> polices;

	private final JLabel timeLabel = new JLabel();
	private final JLabel balanceLabel = new JLabel();
	private final JLabel arrestLabel = new JLabel();
	private final JLabel stolenLabel = new JLabel();
	private final JLabel recoveredLabel = new JLabel();

	private final JTextArea detailArea = new JTextArea(18, 24);

	public InfoPanel(Vault vault, List<Thief> thieves, List<Police> polices) {
		this.vault = vault;
		this.thieves = thieves;
		this.polices = polices;

		setPreferredSize(new Dimension(320, 0));
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(12, 12, 12, 12));

		JPanel top = new JPanel();
		top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

		Font titleFont = new Font("Dialog", Font.BOLD, 14);
		JLabel title = new JLabel("상태");
		title.setFont(titleFont);

		top.add(title);
		top.add(Box.createVerticalStrut(8));
		top.add(timeLabel);
		top.add(balanceLabel);
		top.add(arrestLabel);
		top.add(stolenLabel);
		top.add(recoveredLabel);

		top.add(Box.createVerticalStrut(10));
		top.add(new JLabel("범례"));
		top.add(new JLabel("💰 금고 | 👮 경찰 | 🥷 도둑 | ❌ 체포 | ⬜ 빈 공간"));

		add(top, BorderLayout.NORTH);

		detailArea.setEditable(false);
		detailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		detailArea.setLineWrap(false);

		JScrollPane scroll = new JScrollPane(detailArea);
		add(scroll, BorderLayout.CENTER);

		refresh();
	}

	public void refresh() {
		timeLabel.setText("현재 시간: " + getCurrentTime());
		balanceLabel.setText("금고 잔액: " + vault.getBalance());
		arrestLabel.setText("체포 횟수: " + getTotalArrestCount());
		stolenLabel.setText("총 도난액: " + vault.getTotalStolen());
		recoveredLabel.setText("총 회수액: " + vault.getTotalRecovered());

		StringBuilder sb = new StringBuilder();

		sb.append("[도둑]\n");
		for (Thief t : thieves) {
			sb.append("도둑 ").append(t.getId())
			  .append(" | 상태: ").append(t.getState().getDescription())
			  .append(" | 위치: (")
			  .append(t.getX()).append(",").append(t.getY()).append(")")
			  .append(" | 훔친 금액: ")
			  .append(t.getStolenAmount()).append("\n");
		}

		sb.append("\n[경찰]\n");
		for (Police p : polices) {
			int[] pos = p.getPosition();
			sb.append("경찰 ").append(p.getId()).append(" | 위치: (").append(pos[0]).append(",").append(pos[1]).append(")")
					.append(" | 체포: ").append(p.getArrestCount()).append("\n");
		}

		detailArea.setText(sb.toString());
		detailArea.setCaretPosition(0);
	}

	private String getCurrentTime() {
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	private int getTotalArrestCount() {
		int total = 0;
		for (Police p : polices)
			total += p.getArrestCount();
		return total;
	}
}

package jp.gr.java_conf.t_era.view.parts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTextField;

import jp.gr.java_conf.t_era.automation.model.Cell;
import jp.gr.java_conf.t_era.automation.model.Cell.CellChoiceChangedListener;

import jp.gr.java_conf.t_era.view.dialog.NumberInput;

public class NumberPlate {
	private static final Color DEFAULT = Color.WHITE;

	private final Cell cell;
	public final JTextField field;
	private int number;

	public NumberPlate(final JFrame parent, KeyListener listener, final Cell cell) {
		this.cell = cell;
		field = new JTextField(" ");
		field.setMargin(new Insets(0, 0, 0, 0));
		field.setEditable(false);
		field.setBackground(DEFAULT);
		field.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					final Integer num = NumberInput.getANumber(parent);
					if (num != null) {
						try {
							setNumber(num);
							cell.occuredUserControl(num, null);
						} catch (RuntimeException ex) {
							cell.occuredUserControl(num, ex);
						}
					}
				}
			}
		});
		field.addKeyListener(listener);
		field.setPreferredSize(new Dimension(12, 20));

		cell.addCellChoiceChangedListener(new CellChoiceChangedListener() {
			@Override
			public void cellChoiceChanged(Cell from, List<Integer> newChoice) {
				if (from.isDecided()) {
					field.setBackground(Color.WHITE);
				} else {
					field.setToolTipText(from.toString());
					field.setBackground(COLOR_MAP[newChoice.size()]);
				}
			}
		});
	}
	public void refresh() {
		if (cell.isDecided()) {
			field.setBackground(Color.WHITE);
		} else {
			field.setToolTipText(cell.toString());
			field.setBackground(COLOR_MAP[cell.choiceSize()]);
		}
	}
	private static final Color[] COLOR_MAP = new Color[]{
		Color.RED, new Color(255, 200, 200), Color.WHITE, new Color(210, 210, 210), new Color(190, 190, 190), new Color(170, 170, 170), new Color(150, 150, 150)
		, new Color(128, 128, 128), new Color(128, 128, 128)
	};

	public void setNumber(int number) {
		cell.setValue(number);
		this.number = number;
		field.setText(Integer.toString(number));
	}
	public void resetNumber() {
		cell.init();
		this.number = 0;
		field.setText("");
	}
	public int getNumber() {
		return this.number;
	}

	public void setColor(Color c) {
		field.setBackground(c);
	}

	public void setMayBe(List<Integer> list) {
		StringBuilder sb = new StringBuilder();
		for (Integer i : list) {
			sb.append(i + ", ");
		}
		field.setToolTipText(new String(sb));
	}
}

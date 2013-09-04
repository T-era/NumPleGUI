package jp.gr.java_conf.t_era.view.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

import jp.gr.java_conf.t_era.view.BorderLayoutBuilder;

public class NumberInput {
	public static final int UNKNOWN = -1;

	private final JTextField field;
	private Integer inputed = null;;

	private NumberInput(JFrame parent) {
		final JDialog window = new JDialog(parent);
		window.setModal(true);
		this.field = new JTextField();
		final JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String str = field.getText();
				if (str.matches("[1-9]")) {
					inputed = Integer.parseInt(str);
					window.dispose();
				}
			}
		});

		BorderLayoutBuilder blb = new BorderLayoutBuilder();

		blb.add(field, BorderLayoutBuilder.Direction.Center);
		blb.add(ok, BorderLayoutBuilder.Direction.South, BorderLayoutBuilder.Direction.East);

		blb.layoutComponent(window.getContentPane());
		window.pack();
		window.setVisible(true);
	}

	public static Integer getANumber(JFrame parent) {
		return new NumberInput(parent).inputed;
	}
}

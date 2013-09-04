package jp.gr.java_conf.t_era.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ToolTipManager;
import javax.swing.border.LineBorder;

import jp.gr.java_conf.t_era.automation.ForceSolve;
import jp.gr.java_conf.t_era.automation.model.Cell;

import jp.gr.java_conf.t_era.view.parts.NumberPlate;
import jp.gr.java_conf.t_era.view.version.NumPleNode;
import jp.gr.java_conf.t_era.view.version.NumPleTag;
import jp.gr.java_conf.t_era.tview.NodeCreator;
import jp.gr.java_conf.t_era.tview.NodeThread;
import jp.gr.java_conf.t_era.tview.TagCreator;
import jp.gr.java_conf.t_era.tview.viewer.swing.draw.ExpandingSwingThreadViewer;
import jp.gr.java_conf.t_era.tview.viewer.swing.draw.MyMouseListener;
import jp.gr.java_conf.t_era.view.BorderLayoutBuilder;
import jp.gr.java_conf.t_era.view.BorderLayoutBuilder.Direction;

public class NumPleView extends KeyAdapter {
	private static final Integer N = null;

	public static void main(String[] args) {
		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(1);
		ttm.setReshowDelay(1);

		Integer[][] init = new Integer[][] {
			new Integer[] { N, N, 5, 3, N, N, N, N, N },
			new Integer[] { 8, N, N, N, N, N, N, 2, N },
			new Integer[] { N, 7, N, N, 1, N, 5, N, N },
			new Integer[] { 4, N, N, N, N, 5, 3, N, N },
			new Integer[] { N, 1, N, N, 7, N, N, N, 6 },
			new Integer[] { N, N, 3, 2, N, N, N, 8, N },
			new Integer[] { N, 6, N, 5, N, N, N, N, 9 },
			new Integer[] { N, N, 4, N, N, N, N, 3, N },
			new Integer[] { N, N, N, N, N, 9, 7, N, N },
		};
		NumPleView npv = new NumPleView(init);
		npv.ShowFrame();
	}

	private final NumberPlate[][] plates = new NumberPlate[9][9];
	private Integer[][] data;
	private final Cell[][] cells;
	private final NodeThread<NumPleNode, NumPleTag> thread;
	private final ExpandingSwingThreadViewer<NumPleNode, NumPleTag> threadView;

	private NumPleView(final Integer[][] data) {
		this.data = data;
		cells = Cell.getFiled();
		thread = new NodeThread<NumPleNode, NumPleTag>();
		final JFrame frame = new JFrame();
		threadView = new ExpandingSwingThreadViewer<NumPleNode, NumPleTag>(30, 150, Color.WHITE, new Color(255, 240, 240));
		JScrollPane sp = new JScrollPane(threadView, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(sp);

		threadView.addMyMouseListener(new MyMouseListener<NumPleNode, NumPleTag>() {
			@Override
			public void mouseClicked(MyMouseEvent<NumPleNode, NumPleTag> e) {
				if (e.node != null) {
					if (e.origin.getButton() == MouseEvent.BUTTON1) {
						int answer = JOptionPane.showConfirmDialog(frame, "Rollback to this vewsion?\n" + e.node.toString());
						if (answer == JOptionPane.OK_OPTION) {
							frame.setTitle(e.node.toString());
							setData(e.node.getData());
							thread.setCurrentInHistory(e.node);
						}
					}
				}
			}
		});
		thread.addNode(new NodeCreator<NumPleNode>() {
			@Override
			public NumPleNode newNode(NumPleNode parent) {
				return new NumPleNode(parent, data, "initial data.");
			}
		});
		thread.show(threadView);

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	private static final MessageFormat VERSION_FORMAT = new MessageFormat("{0} @ ({1}, {2})");
	private void ShowFrame() {
		for (int y = 0; y < 9; y ++) {
			for (int x = 0; x < 9; x ++) {
				final int xHere = x;
				final int yHere = y;
				cells[y][x].addCellValueDecideListener(new Cell.CellValueDecideListener() {
					@Override
					public void cellValueDecide(Cell from, int value) {
						data[yHere][xHere] = value;
						plates[yHere][xHere].setNumber(value);
					}
					@Override
					public void userControled(Cell from, final int value, final Exception ex) {
						thread.addNode(new NodeCreator<NumPleNode>() {
							@Override
							public NumPleNode newNode(NumPleNode parent) {
								return new NumPleNode(parent, data, VERSION_FORMAT.format(new Object[] {value, xHere, yHere}));
							}
						});
						if (ex != null) {
							thread.addTag(new TagCreator<NumPleNode, NumPleTag>() {
								@Override
								public NumPleTag newTag(NumPleNode node) {
									return new NumPleTag(node, ex.getMessage());
								}
							});
						}
						thread.show(threadView);
						threadView.repaint();
					}
				});
			}
		}

		JFrame mainFrame = new JFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container out = mainFrame.getContentPane();
		Container con = new Container();
		con.setLayout(new GridLayout(3, 3));
		final JPanel[][] panels = new JPanel[3][3];
		for (int my = 0; my < 3; my ++) {
			for (int mx = 0; mx < 3; mx ++) {
				JPanel panel = new JPanel();
				panels[my][mx] = panel;
				panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
				panel.setLayout(new GridLayout(3, 3));

				for (int dy = 0; dy < 3; dy ++) {
					for (int dx = 0; dx < 3; dx ++) {
						int y = my * 3 + dy;
						int x = mx * 3 + dx;
						NumberPlate np = new NumberPlate(mainFrame, this, cells[y][x]);
						plates[y][x] = np;

						// 蛻晄悄蛹悶�繝��繧ｿ縺ｯ繝翫す

						List<Integer> l = new ArrayList<Integer>();
						l.add(12);
						panel.add(np.field);
					}
				}
				con.add(panel);
			}
		}

		con.addKeyListener(this);
		JButton refresh = new JButton("Re");
		refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int y = 0; y < 9; y ++) {
					for (int x = 0; x < 9; x ++) {
						plates[y][x].refresh();
					}
				}
			}
		});
		JButton force = new JButton("Force resolve");
		force.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new ForceSolve(cells).solve();
			}
		});

		BorderLayoutBuilder blb = new BorderLayoutBuilder();
		blb.add(con, Direction.Center);
		blb.add(refresh, Direction.South);
		blb.add(force, Direction.South);
		blb.layoutComponent(out);

		mainFrame.pack();
		mainFrame.setVisible(true);
		setData(this.data);
	}

	private void setData(Integer[][] data) {
		for (int y = 0; y < 9; y ++) {
			for (int x = 0; x < 9; x ++) {
				plates[y][x].resetNumber();
			}
		}
		this.data = data;
		for (int y = 0; y < 9; y ++) {
			for (int x = 0; x < 9; x ++) {
				NumberPlate np = plates[y][x];
				if (data[y][x] != null) {
					np.setNumber(data[y][x]);
				}
			}
		}
	}

	private static final Color c1 = new Color(255, 255, 192);
	private static final Color c2 = new Color(255, 192, 192);
	@Override
	public void keyPressed(KeyEvent e) {
		char cc = e.getKeyChar();
		String str = Character.toString(cc);
		if (str.matches("[1-9]")) {
			Integer itg = Integer.parseInt(str);
			for (int xy = 0; xy < 9; xy ++) {
				ifIsXLine(xy, itg, c1);
				ifIsYLine(xy, itg, c1);
			}

			for (int my = 0; my < 3; my ++) {
				for (int mx = 0; mx < 3; mx ++) {
					ifIsMass(mx, my, itg, c1);
				}
			}
			for (int y = 0; y < 9; y ++) {
				for (int x = 0; x < 9; x ++) {
					ifIsSame(x, y, itg, c2);
				}
			}
		}
	}
	private void ifIsSame(int x, int y, Integer itg, Color c) {
		if (plates[y][x].getNumber() == itg) {
			plates[y][x].setColor(c);
		}
	}
	private void ifIsXLine(int x, Integer itg, Color c) {
		boolean has = false;
		for (int y = 0; y < 9; y ++) {
			if (plates[y][x].getNumber() == itg) {
				has = true;
			}
		}
		if (has) {
			for (int y = 0; y < 9; y ++) {
				plates[y][x].setColor(c);
			}
		}
	}
	private void ifIsYLine(int y, Integer itg, Color c) {
		boolean has = false;
		for (int x = 0; x < 9; x ++) {
			if (plates[y][x].getNumber() == itg) {
				has = true;
			}
		}
		if (has) {
			for (int x = 0; x < 9; x ++) {
				plates[y][x].setColor(c);
			}
		}
	}
	private void ifIsMass(int mx, int my, Integer itg, Color c) {
		boolean has = false;
		for (int dy = 0; dy < 3; dy ++) {
			for (int dx = 0; dx < 3; dx ++) {
				int x = mx * 3 + dx;
				int y = my * 3 + dy;
				if (plates[y][x].getNumber() == itg) {
					has = true;
				}
			}
		}
		if (has) {
			for (int dy = 0; dy < 3; dy ++) {
				for (int dx = 0; dx < 3; dx ++) {
					int x = mx * 3 + dx;
					int y = my * 3 + dy;
					plates[y][x].setColor(c);
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (int y = 0; y < 9; y ++) {
			for (int x = 0; x < 9; x ++) {
				plates[y][x].refresh();
			}
		}
	}
}

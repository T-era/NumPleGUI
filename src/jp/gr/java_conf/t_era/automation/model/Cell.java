package jp.gr.java_conf.t_era.automation.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cell {
	private Integer value = null;
	private Set<Cell> budys = new HashSet<Cell>();
	private List<Integer> maybe;

	private Set<CellValueDecideListener> cellValueDecideListeners = new HashSet<CellValueDecideListener>();
	private Set<CellChoiceChangedListener> cellChoiceChangedListeners = new HashSet<CellChoiceChangedListener>();

	private String error;
	private Cell(int x, int y) {
		error = String.format("error @(%d, %d)", x, y);
	}

	public void init() {
		this.value = null;
		this.maybe = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9));
	}
	private void addBudy(Cell budy) {
		budys.add(budy);
		budy.addCellValueDecideListener(new CellValueDecideListener() {
			@Override
			public void cellValueDecide(Cell from, int value) {
				addDenied(value);
			}
			@Override
			public void userControled(Cell from, int value, Exception ex) { }
		});
	}

	public static Cell[][] getFiled() {
		Cell[][] ret = new Cell[9][9];
		for (int y = 0; y < 9; y ++) {
			for (int x = 0; x < 9; x ++) {
				ret[y][x] = new Cell(x, y);
			}
		}
		group(ret, true);
		group(ret, false);
		groupCell(ret);
		return ret;
	}
	private static void group(Cell[][] field, boolean ybyx) {
		for (int ax1 = 0; ax1 < 9; ax1 ++) {
			for (int ax2 = 0; ax2 < 9; ax2 ++) {
				for (int ax2_f = 0; ax2_f < 9; ax2_f ++) {
					if (ax2 != ax2_f) {
						if (ybyx) {
							field[ax1][ax2].addBudy(field[ax1][ax2_f]);
						} else {
							field[ax2][ax1].addBudy(field[ax2_f][ax1]);
						}
					}
				}

			}
		}
	}
	private static void groupCell(Cell[][] field) {
		for (int y = 0; y < 9; y ++) {
			int posY = y / 3;
			for (int x = 0; x < 9; x ++) {
				int posX = x / 3;
				for (int y_f = 0; y_f < 3; y_f ++) {
					for (int x_f = 0; x_f < 3; x_f ++) {
						if (y % 3 != y_f || x % 3 != x_f) {
							field[y][x].addBudy(field[posY * 3 + y_f][posX * 3 + x_f]);
						}
					}
				}
			}
		}
	}

	public void setValue(int value) {
		if (this.value == null) {
			this.value = value;

			for (CellValueDecideListener l : cellValueDecideListeners) {
				l.cellValueDecide(this, value);
			}
		} else if (this.value != value) throw new RuntimeException(error);
	}
	public void occuredUserControl(int value, Exception ex) {
		for (CellValueDecideListener l : cellValueDecideListeners) {
			l.userControled(this, value, ex);
		}
	}


	private void addDenied(int value) {
		if (maybe.contains(value)) {
			maybe.remove((Object)value);
			if (maybe.size() == 1) {
				setValue(maybe.get(0));
			}

			for (CellChoiceChangedListener l : cellChoiceChangedListeners) {
				l.cellChoiceChanged(this, maybe);
			}
			if (maybe.size() == 0) {
				throw new RuntimeException(error);
			}
		}
	}

	public void addCellValueDecideListener(CellValueDecideListener listener) {
		cellValueDecideListeners.add(listener);
	}
	public void addCellChoiceChangedListener(CellChoiceChangedListener listener) {
		cellChoiceChangedListeners.add(listener);
	}
	public interface CellValueDecideListener {
		void cellValueDecide(Cell from, int value);
		void userControled(Cell from, int value, Exception ex);
	}
	public interface CellChoiceChangedListener {
		void cellChoiceChanged(Cell from, List<Integer> newChoice);
	}
	public boolean isDecided() {
		return value != null;
	}
	public Integer getValue() {
		return value;
	}
	public Set<Cell> getBudys() {
		return budys;
	}
	public int choiceSize() {
		return maybe.size();
	}
	@Override
	public String toString() {
		if (this.value == null) {
			return maybe.toString();
		} else {
			return Integer.toString(this.value);
		}
	}
}

package jp.gr.java_conf.t_era.automation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jp.gr.java_conf.t_era.automation.model.Cell;

public class ForceSolve {
	private final Cell[][] cells;
	private final Map<Cell, LinkedCell> cellMap = new HashMap<Cell, LinkedCell>();

	public ForceSolve(Cell[][] cells) {
		this.cells = cells;
	}

	public void solve() {
		LinkedCell cell = parse(cells);
		boolean done = cell.solve();
		if (! done) throw new RuntimeException();
		else {
			System.out.println(cells);
		}
	}

	public boolean showCellMap() {
		for (Cell[] line : cells) {
			for (Cell c : line) {
				System.out.print(cellMap.get(c).value + ", ");
			}
			System.out.println("");
		}
		System.out.println("");
		return true;
	}
	class LinkedCell {
		final boolean fixed;
		Integer value;
		LinkedCell next;
		final Set<Cell> budys;
		LinkedCell(boolean fixed
				, Integer value
				, Set<Cell> budys) {
			this.fixed  =fixed;
			this.value = value;
			this.budys = budys;
		}

		public boolean solve() {
			boolean hasAns = false;
			int temp = -1;
			for (int i = 1; i <= 9; i ++) {
				value = i;
				if (! sameValueInBudy(i)) {
					if (next == null) return showCellMap();
					else {
						boolean ret = next.solve();
						if (ret) {
							if (hasAns) throw new RuntimeException(temp + ", " + i);
							else hasAns = true;
						}
					}
					temp = i;
				}
			}

			value = null;
			return hasAns;
		}
		private boolean sameValueInBudy(int i) {
			for (Cell cell : budys) {
				if (cellMap.get(cell) == null) {
					System.out.println("?");
				}
				Integer v = cellMap.get(cell).value;
				if (v != null && v == i) {
					return true;
				}
			}
			return false;
		}
	}
	private LinkedCell parse(Cell[][] cells) {
		LinkedCell previous = null;
		LinkedCell head = null;
		for (Cell[] line : cells) {
			for (Cell cell : line) {
				if (cell.isDecided()) {
					head = new LinkedCell(true, cell.getValue(), cell.getBudys());
					cellMap.put(cell, head);
					if (previous != null) {
						previous.next = head;
					}
					previous = head;
				}
			}
		}
		LinkedCell prevOfRet = head;
		for (Cell[] line : cells) {
			for (Cell cell : line) {
				if (! cell.isDecided()) {
					head = new LinkedCell(false, null, cell.getBudys());
					cellMap.put(cell, head);
					previous.next = head;
					previous = head;
				}
			}
		}
		return prevOfRet.next;
	}
}

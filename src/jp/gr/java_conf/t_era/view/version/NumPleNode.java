package jp.gr.java_conf.t_era.view.version;

import jp.gr.java_conf.t_era.tview.Node;

public class NumPleNode extends Node<NumPleNode> {
	private final Integer[][] data;
	private final String message;

	public NumPleNode(NumPleNode parent, Integer[][] origin, String message) {
		super(parent);
		this.data = deepCopy(origin);
		this.message = message;
	}

	public Integer[][] getData() {
		return deepCopy(data);
	}

	@Override
	public String toString() {
		return message;
	}
	private static Integer[][] deepCopy(Integer[][] origin) {
		Integer[][] ret = new Integer[origin.length][];
		for (int i = 0, size = origin.length; i < size; i ++) {
			ret[i] = new Integer[origin[i].length];
			for (int j = 0, jSize = origin[i].length; j < jSize; j ++) {
				ret[i][j] = origin[i][j];
			}
		}
		return ret;
	}
}

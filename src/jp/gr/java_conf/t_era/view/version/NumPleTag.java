package jp.gr.java_conf.t_era.view.version;

import jp.gr.java_conf.t_era.tview.Tag;

public class NumPleTag extends Tag<NumPleNode> {
	private final String message;

	public NumPleTag(NumPleNode node, String message) {
		super(node);
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}
}

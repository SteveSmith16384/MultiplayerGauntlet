package ssmith.awt.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractMenu extends ArrayList<GLMenuItem> {

	private static final int MENU_LINE_SIZE = 30;

	private GLMenuItem current_item;
	private boolean default_item_selected = false;
	private Font font;
	private Color col_normal, col_selected, col_deselected;
	private Point pos;

	public AbstractMenu(int x, int y, Font f, Color col_norm, Color col_sel, Color col_desel) {
		super();
		pos = new Point(x, y);
		this.font = f;
		this.col_normal = col_norm;
		this.col_selected = col_sel;
		this.col_deselected = col_desel;

	}

	public void setNormalColour(Graphics g) {
		g.setColor(this.col_normal);
	}

	public void setSelectedColour(Graphics g) {
		g.setColor(this.col_selected);
	}

	public void setDeselectedColour(Graphics g) {
		g.setColor(this.col_deselected);
	}

	public void addMenuItem(GLMenuItem item) {
		add(item);
		item.menu = this;
		item.font = font;

		if (default_item_selected == false) {
			if (item.selectable) {
				default_item_selected = true;
				this.current_item = item;
			}
		}
	}

	public void nextItem() {
		int selected_item = indexOf(current_item);
		while (true) {
			selected_item++;
			if (selected_item >= this.size()) {
				selected_item = 0;
			}
			current_item = (GLMenuItem)this.get(selected_item);
			if (current_item.selectable) {
				break;
			}
		}
		this.menuItemSelectionChanged(this.current_item);
	}

	public void prevItem() {
		int selected_item = indexOf(current_item);
		while (true) {
			selected_item--;
			if (selected_item < 0) {
				selected_item = size() - 1;
			}
			current_item = (GLMenuItem)this.get(selected_item);
			if (current_item.selectable) {
				break;
			}
		}
		this.menuItemSelectionChanged(this.current_item);
	}

	protected abstract void menuItemSelected(GLMenuItem menu_item);

	protected void menuItemValueChanged(GLMenuItem menu_item) {

	}

	protected void menuItemSelectionChanged(GLMenuItem menu_item) {

	}

	public void receivedChar(int e) {
		current_item.receivedChar(e);
		this.menuItemValueChanged(current_item);
	}

	protected GLMenuItem getCurrentMenuItem() {
		return current_item;
	}

	public void selectCurrentItem() {
		this.current_item.itemSelected();
	}

	public void draw(Graphics g) {
		int x = pos.x;
		int y = pos.y;
		Iterator<GLMenuItem> enumr = this.iterator();
		GLMenuItem opt;
		while (enumr.hasNext()) {
			opt = (GLMenuItem) enumr.next();
			opt.draw(g, font, x, y);
			y += MENU_LINE_SIZE;
		}
	}

	public boolean shouldBeDrawn() {
		return true;
	}

	public void setCurrentItem(GLMenuItem item) {
		this.current_item = item;
	}
}

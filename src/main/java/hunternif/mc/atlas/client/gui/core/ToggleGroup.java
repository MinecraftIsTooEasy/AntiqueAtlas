package hunternif.mc.atlas.client.gui.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToggleGroup<B extends GuiToggleButton> implements Iterable<B> {
    private final List<B> buttons = new ArrayList();
    private final List<ISelectListener<? extends B>> listeners = new ArrayList();
    private B selectedButton = null;
    private final ClickListener clickListener = new ClickListener();

    public boolean addButton(B button) {
        if (!this.buttons.contains(button)) {
            this.buttons.add(button);
            button.addListener(this.clickListener);
            button.setRadioGroup(this);
            return true;
        }
        return false;
    }

    public boolean removeButton(B button) {
        if (this.buttons.remove(button)) {
            button.removeListener(this.clickListener);
            button.setRadioGroup(null);
            return true;
        }
        return false;
    }

    public void removeAllButtons() {
        Iterator<B> iter = this.buttons.iterator();
        while (iter.hasNext()) {
            B button = iter.next();
            button.removeListener(this.clickListener);
            button.setRadioGroup(null);
            iter.remove();
        }
    }

    public B getSelectedButton() {
        return this.selectedButton;
    }

    public void setSelectedButton(B button) {
        if (this.buttons.contains(button)) {
            if (this.selectedButton != null) {
                this.selectedButton.setSelected(false);
            }
            button.setSelected(true);
            this.selectedButton = button;
        }
    }

    @Override
    public Iterator<B> iterator() {
        return this.buttons.iterator();
    }

    public void addListener(ISelectListener<? extends B> listener) {
        this.listeners.add(listener);
    }

    public void removeListener(ISelectListener<? extends B> listener) {
        this.listeners.remove(listener);
    }

	private class ClickListener implements IButtonListener<B> {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void onClick(B button) {
			if (button != selectedButton) {
				if (selectedButton != null) {
					selectedButton.setSelected(false);
				}
				selectedButton = button;
				for (ISelectListener listener : listeners) {
					listener.onSelect(selectedButton);
				}
			}
		}
	}
}

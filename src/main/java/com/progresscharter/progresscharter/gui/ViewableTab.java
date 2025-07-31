package com.progresscharter.progresscharter.gui;

import javafx.scene.control.Tab;
import javafx.scene.layout.Region;

public abstract class ViewableTab extends Viewable {
    private final Tab tab;

    public ViewableTab(String tabName, Region region) {
        super(region);
        tab = new Tab();
        tab.setText(tabName);
    }

    public ViewableTab(String tabName, Viewable viewable) {
        super(viewable);
        tab = new Tab();
        tab.setText(tabName);
    }

    public final String getTabName() {
        return tab.getText();
    }

    public final Tab getTab() {
        tab.setContent(getView());
        return tab;
    }

    public abstract void reload();
}

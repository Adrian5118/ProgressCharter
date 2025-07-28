package com.progresscharter.progresscharter.gui;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainGUI extends Viewable {
    private final VBox root;

    public MainGUI(Region region) {
        super(region);

        root = new VBox();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);
    }

    @Override
    public Region getView() {
        return null;
    }
}

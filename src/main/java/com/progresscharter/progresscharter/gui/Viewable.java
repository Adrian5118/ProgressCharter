package com.progresscharter.progresscharter.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.layout.Region;

public abstract class Viewable {
    protected DoubleProperty width;
    protected DoubleProperty height;

    public Viewable(Region region) {
        this.width = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();

        this.width.bind(region.widthProperty());
        this.height.bind(region.heightProperty());
    }

    public Viewable(Scene scene) {
        this.width = new SimpleDoubleProperty();
        this.height = new SimpleDoubleProperty();

        this.width.bind(scene.widthProperty());
        this.height.bind(scene.heightProperty());
    }

    public Viewable(Viewable viewable) {
        this(viewable.getView());
    }

    public abstract Region getView();

    public ReadOnlyDoubleProperty getWidthProperty() {
        return width;
    }
    public double getWidth() {
        return width.get();
    }

    public ReadOnlyDoubleProperty getHeightProperty() {
        return height;
    }
    public double getHeight() {
        return height.get();
    }
}

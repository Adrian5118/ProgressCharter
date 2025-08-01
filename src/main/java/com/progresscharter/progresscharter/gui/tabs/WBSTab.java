package com.progresscharter.progresscharter.gui.tabs;

import com.progresscharter.progresscharter.gui.Viewable;
import com.progresscharter.progresscharter.gui.ViewableTab;
import com.progresscharter.progresscharter.gui.holders.ActivityHolder;
import com.progresscharter.progresscharter.lib.Activity;
import com.progresscharter.progresscharter.lib.GlobalAccessor;
import com.progresscharter.progresscharter.lib.ProjectHandler;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class WBSTab extends ViewableTab {
    private final VBox root;
    private final ScrollPane scrollPane;
    private ActivityHolder rootHolder;

    public WBSTab(String name, Viewable viewable) {
        super(name, viewable);
        GlobalAccessor.addToAccessList("wbsTab", this);
        GlobalAccessor.addPerm("wbsTab", OverviewTab.class);

        root = new VBox();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);

        scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("wbsPane");
        scrollPane.prefWidthProperty().bind(width);
        scrollPane.prefHeightProperty().bind(height);

        rootHolder = new ActivityHolder(this, ProjectHandler.getCurrentActivity());

        scrollPane.setFitToWidth(true);
        scrollPane.setContent(
                rootHolder.getView()
        );

        root.getChildren().add(
                scrollPane
        );
    }

    @Override
    public Region getView() {
        return root;
    }

    public void setRootHolderText(String text) {
        rootHolder.setHolderText(text);
    }

    @Override
    public void reload() {
        rootHolder.clear();
        rootHolder = new ActivityHolder(this, ProjectHandler.getCurrentActivity());
        scrollPane.setContent(rootHolder.getView());
    }
}

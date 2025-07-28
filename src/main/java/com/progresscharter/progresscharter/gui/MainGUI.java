package com.progresscharter.progresscharter.gui;

import com.progresscharter.progresscharter.HelloApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class MainGUI extends Viewable {
    private final BorderPane root;

    private final HBox header;
    private final Menu filesMenu;
    private final MenuBar filesMenuBar;

    private final VBox activitiesList;

    public MainGUI(Region region) {
        super(region);
        throw new UnsupportedOperationException("MainGUI can only be bound to scene");
    }

    public MainGUI(Scene scene) {
        super(scene);

        root = new BorderPane();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);

        header = new HBox();
        header.prefWidthProperty().bind(width);
        header.prefHeightProperty().bind(height.multiply(0.05));
        header.getStyleClass().add("header");

        filesMenu = new Menu("Files");
        filesMenu.getItems().addAll(
                createMenuItem("Open", event -> {}),
                createMenuItem("Save", event -> {}),
                createMenuItem("Save As", event -> {}),
                new SeparatorMenuItem(),
                createMenuItem("Exit", event -> {
                    Platform.exit();
                    System.exit(0);
                })
        );

        filesMenuBar = new MenuBar(filesMenu);
        filesMenuBar.prefHeightProperty().bind(height);

        header.getChildren().addAll(
                filesMenuBar
        );

        activitiesList = new VBox();
        activitiesList.prefWidthProperty().bind(width.multiply(0.05));
        activitiesList.prefHeightProperty().bind(height);
        activitiesList.getStyleClass().add("activitiesList");

        Image overviewButtonImage = new Image(HelloApplication.class.getResourceAsStream("placeholder.png"));
        Image WBSButtonImage = new Image(HelloApplication.class.getResourceAsStream("placeholder.png"));
        Image CMButtonImage = new Image(HelloApplication.class.getResourceAsStream("placeholder.png"));
        Image PERTButtonImage = new Image(HelloApplication.class.getResourceAsStream("placeholder.png"));
        Image GanttButtonImage = new Image(HelloApplication.class.getResourceAsStream("placeholder.png"));

        activitiesList.getChildren().addAll(
                createActionButton(overviewButtonImage,
                        activitiesList,
                        event -> {}
                ),
                createActionButton(WBSButtonImage,
                        activitiesList,
                        event -> {}
                ),
                createActionButton(CMButtonImage,
                        activitiesList,
                        event -> {}
                ),
                createActionButton(PERTButtonImage,
                        activitiesList,
                        event -> {}
                ),
                createActionButton(GanttButtonImage,
                        activitiesList,
                        event -> {}
                )
        );

        root.setTop(header);
        root.setLeft(activitiesList);
    }

    @Override
    public Region getView() {
        return root;
    }

    // Private methods
    private MenuItem createMenuItem(String itemName, EventHandler<ActionEvent> event) {
        MenuItem item = new MenuItem(itemName);
        item.setOnAction(event);

        return item;
    }

    private Button createActionButton(String buttonName, Region bindTo, boolean bindHeightToWidth, EventHandler<ActionEvent> event) {
        Button button = new Button(buttonName);
        button.prefWidthProperty().bind(bindTo.widthProperty());
        if(bindHeightToWidth) button.prefHeightProperty().bind(bindTo.widthProperty());
        button.setOnAction(event);

        return button;
    }

    private Button createActionButton(Image image, Region bindTo, EventHandler<ActionEvent> event) {
        Button button = new Button();
        button.prefWidthProperty().bind(bindTo.maxWidthProperty());
        button.prefHeightProperty().bind(bindTo.maxWidthProperty());
        button.setOnAction(event);

        ImageView imageView = new ImageView(image);
        button.setGraphic(imageView);

        return button;
    }
}

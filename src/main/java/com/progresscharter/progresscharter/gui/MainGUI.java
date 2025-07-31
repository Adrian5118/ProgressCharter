package com.progresscharter.progresscharter.gui;

import com.progresscharter.progresscharter.gui.tabs.OverviewTab;
import com.progresscharter.progresscharter.gui.tabs.WBSTab;
import com.progresscharter.progresscharter.lib.FileHandler;
import com.progresscharter.progresscharter.lib.ProjectHandler;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.IOException;

public class MainGUI extends Viewable {
    private final BorderPane root;
    private final Stage currentStage;

    private final HBox header;
    private final Menu filesMenu;
    private final MenuBar filesMenuBar;

    private TabPane tabPane;
    private final OverviewTab overviewTab;
    private final WBSTab wbsTab;

    public MainGUI(Region region) {
        super(region);
        throw new UnsupportedOperationException("MainGUI can only be bound to scene");
    }

    public MainGUI(Scene scene, Stage stage) {
        super(scene);
        this.currentStage = stage;

        root = new BorderPane();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);

        overviewTab = new OverviewTab("Overview", this);
        wbsTab = new WBSTab("Work Breakdown Structure", this);

        header = new HBox();
        header.prefWidthProperty().bind(width);
        header.prefHeightProperty().bind(height.multiply(0.05));
        header.getStyleClass().add("header");

        filesMenu = new Menu("Files");
        filesMenu.getItems().addAll(
                createMenuItem("Open", event -> {
                    try {
                        openOpenFileWindow();
                        overviewTab.reload();
                        wbsTab.reload();
                    } catch (Exception e) {
                        Alert loadErrAlert = new Alert(Alert.AlertType.ERROR, "Unable to load project file");
                        System.err.println(e.getMessage());
                        loadErrAlert.showAndWait();
                    }
                }),
                createMenuItem("Save", event -> {
                    saveProject();
                }),
                createMenuItem("Save As", event -> {
                    try {
                        openSaveAsWindow();
                    } catch (Exception e) {
                        Alert saveErrAlert = new Alert(Alert.AlertType.ERROR, "Unable to load project file");
                        System.err.println("Unable to save project. If you're seeing this and it's not related to file permissions or directory issues, you should report to developer immediately.\nReason: " + e.getMessage());
                        saveErrAlert.showAndWait();
                    }
                }),
                new SeparatorMenuItem(),
                createMenuItem("Exit", event -> {
                    Platform.exit();
                    System.exit(0);
                })
        );

        filesMenuBar = new MenuBar(filesMenu);
        filesMenuBar.prefHeightProperty().bind(header.heightProperty());

        tabPane = new TabPane();
        tabPane.prefWidthProperty().bind(width);
        tabPane.prefHeightProperty().bind(height.multiply(0.95));
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        tabPane.getTabs().addAll(
                overviewTab.getTab(),
                wbsTab.getTab()
        );

        header.getChildren().addAll(
                filesMenuBar
        );

        root.setTop(header);
        root.setCenter(tabPane);
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

    private void saveProject() {
        try {
            FileHandler.save(FileHandler.getCurrentFilePath());
        } catch (JSONException jsonException) {
            System.err.println("Cannot save due to problem parsing JSON. You should report this to developer directly.\nReason: " + jsonException.getMessage());
        } catch (IOException ioException) {
            System.err.println("Unable to save.\nReason: " + ioException.getMessage());
        } catch(IllegalArgumentException illegalArgumentException) {
            try {
                openSaveAsWindow();
            } catch (Exception e) {
                Alert saveErrAlert = new Alert(Alert.AlertType.ERROR, "Unable to load project file");
                System.err.println("Unable to save project. If you're seeing this and it's not related to file permissions or directory issues, you should report to developer immediately.\nReason: " + e.getMessage());
                saveErrAlert.showAndWait();
            }

        }
    }

    private void openSaveAsWindow() throws JSONException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(FileHandler.getCurrentFileName());
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Save Project File As");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON File", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File saveFile = fileChooser.showSaveDialog(currentStage);

        if(saveFile == null) return;

        FileHandler.save(saveFile);

    }

    private void openOpenFileWindow() throws JSONException, IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        fileChooser.setTitle("Open Project File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON File", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File openFile = fileChooser.showOpenDialog(currentStage);

        if(openFile == null) return;

        FileHandler.open(openFile);
    }
}

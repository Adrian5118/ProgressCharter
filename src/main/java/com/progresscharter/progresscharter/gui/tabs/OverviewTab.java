package com.progresscharter.progresscharter.gui.tabs;

import com.progresscharter.progresscharter.gui.Viewable;
import com.progresscharter.progresscharter.gui.ViewableTab;
import com.progresscharter.progresscharter.lib.Activity;
import com.progresscharter.progresscharter.lib.GlobalAccessor;
import com.progresscharter.progresscharter.lib.ProjectHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class OverviewTab extends ViewableTab {
    private final VBox root;
    private final Label overviewLabel;

    private final GridPane overviewPane;

    private final Label titleLabel;
    private final TextField titleField;
    private final Label creatorLabel;
    private final TextField creatorField;
    private final Label descriptionLabel;
    private final TextArea descriptionField;
    private final Label startDateLabel;
    private final TextField startDateField;
    private final Label endDateLabel;
    private final TextField endDateField;

    private final Button refreshButton;
    private final Button syncButton;

    public OverviewTab(String name, Viewable viewable) {
        super(name, viewable);

        root = new VBox();
        root.getStyleClass().add("overview");

        overviewLabel = new Label("Project Overview");
        overviewLabel.getStyleClass().add("title");

        overviewPane = new GridPane();
        overviewPane.getStyleClass().add("infoPane");

        titleLabel = new Label("Title");
        titleField = new TextField(ProjectHandler.getProjectName());
        titleField.setOnAction(event -> {
            ProjectHandler.setProjectName(titleField.getText());
            ProjectHandler.getCurrentActivity().setName(titleField.getText());
            WBSTab wbsTabRef = (WBSTab) GlobalAccessor.access("wbsTab", this);
            wbsTabRef.setRootHolderText(titleField.getText());
        });

        creatorLabel = new Label("Creator");
        creatorField = new TextField(ProjectHandler.getCreatorName());
        creatorField.setOnAction(event -> {
            ProjectHandler.setCreatorName(creatorField.getText());
        });

        descriptionLabel = new Label("Description");
        descriptionField = new TextArea(ProjectHandler.getProjectDescription());
        descriptionField.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                ProjectHandler.setProjectDescription(descriptionField.getText());
            }
        });

        startDateLabel = new Label("Start Date");
        startDateField = new TextField(ProjectHandler.getCurrentActivity() == null ? "" : ProjectHandler.getCurrentActivity().getStartDate().toString());
        startDateField.setEditable(false);

        endDateLabel = new Label("End Date");
        endDateField = new TextField(ProjectHandler.getCurrentActivity() == null ? "" : ProjectHandler.getCurrentActivity().getEndDate().toString());
        endDateField.setEditable(false);

        refreshButton = new Button("Refresh");
        refreshButton.setOnAction(event -> {
            Activity activityReference = ProjectHandler.getCurrentActivity();

            activityReference.synchronizeCost(false);
            activityReference.synchronizeDate(false);
            startDateField.setText(activityReference.getStartDate().toString());
            endDateField.setText(activityReference.getEndDate().toString());
        });

        syncButton = new Button("Sync with Changes");
        syncButton.setOnAction(event -> {
            ProjectHandler.setProjectName(titleField.getText());
            ProjectHandler.setCreatorName(creatorField.getText());
            ProjectHandler.setProjectDescription(descriptionField.getText());
        });

        overviewPane.add(titleLabel, 0, 0);
        overviewPane.add(new Label(":"), 1, 0);
        overviewPane.add(titleField, 2, 0);
        overviewPane.add(creatorLabel, 0, 1);
        overviewPane.add(new Label(":"), 1, 1);
        overviewPane.add(creatorField, 2, 1);
        overviewPane.add(descriptionLabel, 0, 2);
        overviewPane.add(new Label(":"), 1, 2);
        overviewPane.add(descriptionField, 2, 2);
        overviewPane.add(startDateLabel, 0, 3);
        overviewPane.add(new Label(":"), 1, 3);
        overviewPane.add(startDateField, 2, 3);
        overviewPane.add(endDateLabel, 0, 4);
        overviewPane.add(new Label(":"), 1, 4);
        overviewPane.add(endDateField, 2, 4);
        overviewPane.add(refreshButton, 0, 5);
        overviewPane.add(syncButton, 2, 5);

        root.getChildren().addAll(
                overviewLabel,
                overviewPane
        );
    }

    @Override
    public Region getView() {
        return root;
    }

    @Override
    public void reload() {
        Activity activityReference = ProjectHandler.getCurrentActivity();

        titleField.setText(ProjectHandler.getProjectName());
        creatorField.setText(ProjectHandler.getCreatorName());
        descriptionField.setText(ProjectHandler.getProjectDescription());
        startDateField.setText(activityReference.getStartDate().toString());
        endDateField.setText(activityReference.getEndDate().toString());
    }
}

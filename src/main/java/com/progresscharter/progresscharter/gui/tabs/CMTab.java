package com.progresscharter.progresscharter.gui.tabs;

import com.progresscharter.progresscharter.gui.Viewable;
import com.progresscharter.progresscharter.gui.ViewableTab;
import com.progresscharter.progresscharter.lib.Activity;
import com.progresscharter.progresscharter.lib.GlobalAccessor;
import com.progresscharter.progresscharter.lib.ProjectHandler;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;


// Cost Management Tab
public class CMTab extends ViewableTab {
    private final VBox root;

    private final Label costManagementTitle;

    private final GridPane costManagementSummaryPane;

    private final TableView costManagementTable;
    private final TableColumn<Activity, String> activityNameColumn;
    private final TableColumn<Activity, String> activityTimeColumn;
    private final TableColumn<Activity, String> activityCostColumn;
    private final TableColumn<Activity, String> activityAverageCostColumn;


    public CMTab(String name, Viewable viewable) {
        super(name, viewable);

        GlobalAccessor.addToAccessList("cmTab", this);
        GlobalAccessor.addPerm("cmTab", WBSTab.class);

        root = new VBox();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);
        root.getStyleClass().add("costManagement");

        costManagementTitle = new Label("Cost Management");
        costManagementTitle.getStyleClass().add("title");

        costManagementSummaryPane = new GridPane();

        costManagementTable = new TableView<Activity>();
        activityNameColumn = new TableColumn<>("Activity");
        activityNameColumn.setCellValueFactory((cellData) -> {
            if(cellData.getValue() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(cellData.getValue().getName());
        });
        activityTimeColumn = new TableColumn<>("Time");
        activityTimeColumn.setCellValueFactory((cellData) -> {
            if(cellData.getValue() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(cellData.getValue().getDuration() + "d");
        });
        activityCostColumn = new TableColumn<>("Cost");
        activityCostColumn.setCellFactory(column -> {
            return new TableCell<>() {
                TextField field;

                @Override
                protected void updateItem(String item, boolean empty) {
                    Activity activity = this.getTableRow().getItem();
                    if(activity == null || empty) {
                        setGraphic(null);
                        return;
                    }

                    field = new TextField(activity.getCost() + "");
                    if(!activity.hasChildren()) {
                        activity.synchronizeCost(false);
                        field.setDisable(true);
                        field.setText(activity.getCost() + "");
                    }
                    field.setOnAction(event -> {
                        activity.setCost(Long.parseLong(field.getText()));
                    });

                    setGraphic(field);
                }
            };
        });
        activityAverageCostColumn = new TableColumn<>("Average Cost");
        activityAverageCostColumn.setCellValueFactory(cellData -> {
            if(cellData.getValue() == null) return new SimpleStringProperty("");
            return new SimpleStringProperty(cellData.getValue().getCost() / cellData.getValue().getDuration() + "");
        });

        costManagementTable.getColumns().addAll(
                activityNameColumn,
                activityTimeColumn,
                activityCostColumn,
                activityAverageCostColumn
        );

        for(Object object: costManagementTable.getColumns()) {
            TableColumn<Activity, String> column = (TableColumn<Activity, String>) object;
            column.prefWidthProperty().bind(
                    costManagementTable.widthProperty().divide(
                            costManagementTable.getColumns().size()
                    )
            );
        }

        costManagementTable.setRowFactory(row -> new TableRow<Activity>() {
            @Override
            protected void updateItem(Activity item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) {

                    setStyle("");
                    return;
                }

                if(!item.hasChildren() || ProjectHandler.getCurrentActivity().contains(item)) {
                    setStyle("-fx-background-color: #dbdbdb;");
                }
            }
        });

        root.getChildren().addAll(
                costManagementTitle,
                costManagementSummaryPane,
                costManagementTable
        );
    }

    @Override
    public Region getView() {
        return root;
    }

    @Override
    public void reload() {
        ObservableList<Activity> currentActivities = FXCollections.observableList(
                getAllConnectedActivities(
                        ProjectHandler.getCurrentActivity()
                )
        );

        costManagementTable.getItems().clear();
        costManagementTable.getItems().addAll(currentActivities);
    }

    private ArrayList<Activity> getAllConnectedActivities(Activity activity) {
        ArrayList<Activity> returnActivities = new ArrayList<>();
        ArrayList<Activity> childActivities = new ArrayList<>(activity.getChildActivities());

        for(Activity childActivity: childActivities) {
            returnActivities.add(childActivity);
            returnActivities.addAll(getAllConnectedActivities(childActivity));
        }

        return returnActivities;
    }
}

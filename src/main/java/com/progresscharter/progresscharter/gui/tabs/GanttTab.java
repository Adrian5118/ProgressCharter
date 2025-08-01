package com.progresscharter.progresscharter.gui.tabs;

import com.progresscharter.progresscharter.gui.Viewable;
import com.progresscharter.progresscharter.gui.ViewableTab;
import com.progresscharter.progresscharter.lib.Activity;
import com.progresscharter.progresscharter.lib.ProjectHandler;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class GanttTab extends ViewableTab {
    private final VBox root;

    private final ScrollPane chartScrollPane;

    private final ArrayList<Activity> currentActivities;

    private final HBox chartComponents;
    private final VBox chartActivities;
    private final VBox chartDates;

    public GanttTab(String tabName, Viewable viewable) {
        super(tabName, viewable);

        root = new VBox();
        root.prefWidthProperty().bind(width);
        root.prefHeightProperty().bind(height);

        chartScrollPane = new ScrollPane();
        chartScrollPane.prefWidthProperty().bind(width);
        chartScrollPane.prefWidthProperty().bind(height);

        chartComponents = new HBox();
        chartComponents.prefWidthProperty().bind(width);
        chartComponents.prefHeightProperty().bind(height);

        chartActivities = new VBox();
        chartActivities.getStyleClass().add("chartComponent");
        chartActivities.prefWidthProperty().bind(chartComponents.widthProperty().multiply(0.1));

        chartDates = new VBox();
        chartDates.getStyleClass().add("chartComponent");
        chartDates.prefWidthProperty().bind(chartComponents.widthProperty().multiply(0.9));

        currentActivities = new ArrayList<>();
        Activity activityReference = ProjectHandler.getCurrentActivity();

        currentActivities.add(activityReference);
        currentActivities.addAll(getAllConnectedActivities(activityReference));

        chartComponents.getChildren().addAll(
                chartActivities,
                chartDates
        );

        generateChart();

        chartScrollPane.setContent(chartComponents);

        root.getChildren().add(chartScrollPane);
    }

    private void generateChart() {
        chartActivities.getChildren().clear();
        chartDates.getChildren().clear();

        for(Activity activity: currentActivities) {
            VBox activityVBox = new VBox();
            activityVBox.getStyleClass().add("activity");
            activityVBox.prefWidthProperty().bind(chartActivities.widthProperty());
            activityVBox.prefHeightProperty().bind(height.multiply(0.1));
            activityVBox.setOnMouseClicked(event -> {
                if(!activity.getChildActivities().isEmpty()) {
                    Alert hasParentAlert = new Alert(Alert.AlertType.WARNING, activity.getName() + "'s date properties are bound to its children date properties");
                    hasParentAlert.showAndWait();
                    return;
                }

                if(event.getClickCount() == 2) {
                    Stage changeDatePopupStage = new Stage();
                    GridPane changeDatePane = new GridPane();

                    Label startDateLabel = new Label("Start Date");
                    TextField startDateField = new TextField(activity.getStartDate().toString());

                    Label endDateLabel = new Label("End Date");
                    TextField endDateField = new TextField(activity.getEndDate().toString());

                    Button cancelButton = new Button("Cancel");
                    Button confirmButton = new Button("Confirm");

                    changeDatePane.add(startDateLabel, 0 ,0);
                    changeDatePane.add(new Label(":"), 1 ,0);
                    changeDatePane.add(startDateField, 2 ,0);

                    changeDatePane.add(endDateLabel, 0 ,1);
                    changeDatePane.add(new Label(":"), 1 ,1);
                    changeDatePane.add(endDateField, 2 ,1);

                    cancelButton.setOnAction(popupEvent -> {
                        changeDatePopupStage.close();
                    });

                    confirmButton.setOnAction(popupEvent -> {
                        try {
                            activity.setStartDate(LocalDate.parse(startDateField.getText()));
                            activity.setEndDate(LocalDate.parse(endDateField.getText()));
                            activity.synchronizeDate(false);
                            reload();
                            changeDatePopupStage.close();
                        } catch(DateTimeParseException dtpe) {
                            Alert parseErrorAlert = new Alert(Alert.AlertType.ERROR, "The entered date format is invalid!");
                            dtpe.printStackTrace();
                            parseErrorAlert.showAndWait();
                        } catch(DateTimeException dte) {
                            Alert parseErrorAlert = new Alert(Alert.AlertType.ERROR, "The entered end date is behind start date!");
                            dte.printStackTrace();
                            parseErrorAlert.showAndWait();
                        }
                    });

                    changeDatePane.add(cancelButton,0, 2);
                    changeDatePane.add(confirmButton, 2, 2);

                    Scene popupScene = new Scene(changeDatePane);
                    changeDatePopupStage.setTitle("Change " + activity.getName() + " dates");
                    changeDatePopupStage.setScene(popupScene);
                    changeDatePopupStage.showAndWait();
                }
            });

            Label activityLabel = new Label(activity.getName());
            activityLabel.prefWidthProperty().bind(activityVBox.widthProperty());
            activityLabel.prefHeightProperty().bind(activityVBox.heightProperty());
            activityLabel.setCenterShape(true);
            activityLabel.setAlignment(Pos.BASELINE_LEFT);
            activityLabel.setWrapText(true);
            activityVBox.getChildren().add(activityLabel);

            chartActivities.getChildren().add(activityVBox);

            VBox datesVBox = new VBox();
            datesVBox.getStyleClass().add("date");
            datesVBox.prefWidthProperty().bind(chartActivities.widthProperty());
            datesVBox.prefHeightProperty().bind(height.multiply(0.1));
            datesVBox.setAlignment(Pos.CENTER_LEFT);



            VBox datesBar = new VBox();
            datesBar.setStyle("-fx-background-color: #1298ff;");
            datesBar.prefHeightProperty().bind(datesVBox.heightProperty().multiply(0.5));
            datesBar.setPrefWidth(activity.getDuration() * 10);
            datesBar.setMaxWidth(activity.getDuration() * 10);
            datesBar.setTranslateX(
                    datesVBox.getTranslateX() +
                    (Math.abs(ChronoUnit.DAYS.between(
                            activity.getStartDate(),
                            ProjectHandler.getCurrentActivity().getStartDate())) *
                            10
                    )
            );

            Tooltip hoverTooltip = new Tooltip();
            datesBar.setOnMouseMoved(event -> {
                hoverTooltip.setText(activity.getStartDate().plusDays(Math.round(event.getX() / 10)).toString());
            });
            Tooltip.install(datesBar, hoverTooltip);

            datesVBox.getChildren().add(datesBar);

            chartDates.getChildren().add(datesVBox);
        }
    }

    @Override
    public Region getView() {
        return root;
    }

    @Override
    public void reload() {
        currentActivities.clear();
        currentActivities.add(ProjectHandler.getCurrentActivity());
        currentActivities.addAll(getAllConnectedActivities(ProjectHandler.getCurrentActivity()));
        ProjectHandler.getCurrentActivity().synchronizeDate(false);
        generateChart();
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

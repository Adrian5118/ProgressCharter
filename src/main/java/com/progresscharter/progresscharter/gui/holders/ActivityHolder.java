package com.progresscharter.progresscharter.gui.holders;

import com.progresscharter.progresscharter.gui.Viewable;
import com.progresscharter.progresscharter.lib.Activity;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Stack;

public class ActivityHolder extends Viewable {
    private VBox root;
    private Activity activityReference;
    private ActivityHolder parentHolder;

    private BorderPane holderDetail;
    private Button hideButton;
    private TextField activityName;
    private Button deleteButton;

    private VBox centerFiller;

    private VBox childHolders;
    private ArrayList<ActivityHolder> childHoldersList;

    private Button addButton;

    public ActivityHolder(Viewable viewable, Activity activity) {
        super(viewable);

        root = new VBox();
        root.getStyleClass().add("activity");
        activityReference = activity;
        width.multiply(0.9);
        root.prefWidthProperty().bind(width);
        parentHolder = null;

        holderDetail = new BorderPane();

        HBox holderDetail_left = new HBox();
        activityName = new TextField(activity.getName());
        activityName.setEditable(false);
        activityName.setOnAction(event -> {
            activity.setName(activityName.getText());
        });
        hideButton = new Button("v");
        hideButton.setOnAction(event -> {
            boolean isVisible = childHolders.isVisible();

            childHolders.setVisible(!isVisible);
            hideButton.setText(isVisible ? "^" : "v");
        });

        holderDetail_left.getChildren().addAll(
                hideButton,
                activityName
        );

        centerFiller = new VBox();
        centerFiller.setStyle("-fx-background-color: #dbdbdb;");

        deleteButton = new Button("X");
        deleteButton.setDisable(parentHolder == null);
        deleteButton.setOnAction(event -> {
            clear();
        });

        centerFiller.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
                    return root.getWidth() - holderDetail_left.getWidth() - deleteButton.getWidth();
                },
                root.widthProperty(),
                holderDetail_left.widthProperty(),
                deleteButton.widthProperty()
        ));

        holderDetail.setLeft(holderDetail_left);
        holderDetail.setCenter(centerFiller);
        holderDetail.setRight(deleteButton);

        childHolders = new VBox();
        childHolders.prefWidthProperty().bind(width.multiply(0.9));
        childHoldersList = new ArrayList<>();


        for(Activity childActivity: activity.getChildActivities()) {
            childHoldersList.add(new ActivityHolder(childActivity, this));
        }

        addButton = new Button("Add");
        addButton.prefWidthProperty().bind(width);
        addButton.setOnAction(event -> {
            Activity childActivity = new Activity(
                getParentsAsString() + (childHoldersList.size() + 1) + ".",
                "",
                activityReference
            );

            childHoldersList.add(new ActivityHolder(childActivity, this));
            reloadChildHolder();
        });

        root.getChildren().addAll(
                holderDetail,
                childHolders,
                addButton
        );
    }

    // Don't ask why
    public ActivityHolder(Activity childActivity, ActivityHolder parentHolder) {
        this(parentHolder, childActivity);
        this.parentHolder = parentHolder;
        deleteButton.setDisable(false);
        activityName.setEditable(true);
    }

    @Override
    public Region getView() {
        reloadChildHolder();
        return root;
    }

    public Activity getActivityReference() {
        return activityReference;
    }

    public void clear() {
        // Note: Do not use foreach method or function here. Use good ole indexed for loop to avoid error
        for(int i = 0; i < childHoldersList.size(); i++) {
            childHoldersList.get(i).clear();
        }

        if(parentHolder != null) {
            parentHolder.childHoldersList.remove(this);
            parentHolder.reloadChildHolder();
        }

        activityReference.clearChildActivities();
        if(activityReference.hasParent()) activityReference.removeParentActivity();
        childHoldersList.clear();
        childHolders.getChildren().clear();
    }

    private void reloadChildHolder() {
        ObservableList<Node> childHoldersChildren = childHolders.getChildren();

        childHoldersChildren.clear();
        for(ActivityHolder childHolder: childHoldersList) {
            childHoldersChildren.add(childHolder.getView());
        }
    }

    private String getParentsAsString() {
        StringBuilder builder = new StringBuilder();
        Stack<Integer> stack = new Stack<>();
        ActivityHolder currentHolder = this;
        ActivityHolder nextParent = parentHolder;

        while(nextParent != null) {
            stack.push(nextParent.childHoldersList.indexOf(currentHolder));

            currentHolder = nextParent;
            nextParent = nextParent.parentHolder;
        }
        while(!stack.isEmpty()) {
            builder.append((stack.pop() + 1) + ".");
        }

        return builder.toString();
    }
}

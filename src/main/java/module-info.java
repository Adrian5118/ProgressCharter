module com.progresscharter.progresscharter {
    requires javafx.controls;
    requires javafx.fxml;
    requires jettison;
    requires jdk.compiler;


    opens com.progresscharter.progresscharter to javafx.fxml;
    exports com.progresscharter.progresscharter;
}
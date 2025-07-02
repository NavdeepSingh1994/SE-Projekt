package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Project;

public class Projectcontroller {

    @FXML private TextField nameField;
    @FXML private TextField budgetField;
    @FXML private TextField statusField;
    @FXML private TextField durationField;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> nameCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, Double> budgetCol;
    @FXML private TableColumn<Project, Integer> durationCol;

    private final ObservableList<Project> projects = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        budgetCol.setCellValueFactory(data -> data.getValue().budgetProperty().asObject());
        durationCol.setCellValueFactory(data -> data.getValue().durationProperty().asObject());

        projectTable.setItems(projects);
    }

    @FXML
    public void addProject() {
        String name = nameField.getText();
        String status = statusField.getText();
        double budget = Double.parseDouble(budgetField.getText());
        int duration = Integer.parseInt(durationField.getText());

        projects.add(new Project(name, status, budget, duration));

        nameField.clear();
        statusField.clear();
        budgetField.clear();
        durationField.clear();
    }
}

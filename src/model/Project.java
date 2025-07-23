package model;

import javafx.beans.property.*;

public class Project {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final DoubleProperty budget = new SimpleDoubleProperty();
    private final StringProperty startDate = new SimpleStringProperty();
    private final StringProperty endDate = new SimpleStringProperty();
    private final StringProperty priority = new SimpleStringProperty();
    private final StringProperty department = new SimpleStringProperty();
    private final StringProperty owner = new SimpleStringProperty();

    public Project(String name, String status, double budget, String startDate, String endDate,
                   String priority, String department, String owner) {
        this.name.set(name);
        this.status.set(status);
        this.budget.set(budget);
        this.startDate.set(startDate);
        this.endDate.set(endDate);
        this.priority.set(priority);
        this.department.set(department);
        this.owner.set(owner);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty statusProperty() { return status; }
    public DoubleProperty budgetProperty() { return budget; }
    public StringProperty startDateProperty() { return startDate; }
    public StringProperty endDateProperty() { return endDate; }
    public StringProperty priorityProperty() { return priority; }
    public StringProperty departmentProperty() { return department; }
    public StringProperty ownerProperty() { return owner; }
}

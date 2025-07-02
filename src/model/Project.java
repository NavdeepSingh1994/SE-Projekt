package model;

import javafx.beans.property.*;

public class Project {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final DoubleProperty budget = new SimpleDoubleProperty();
    private final IntegerProperty duration = new SimpleIntegerProperty();

    public Project(String name, String status, double budget, int duration) {
        this.name.set(name);
        this.status.set(status);
        this.budget.set(budget);
        this.duration.set(duration);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty statusProperty() { return status; }
    public DoubleProperty budgetProperty() { return budget; }
    public IntegerProperty durationProperty() { return duration; }
}

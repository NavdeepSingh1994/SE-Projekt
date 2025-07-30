package gui;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Project;
import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class Projectcontroller {

    @FXML private TextField nameField;
    @FXML private TextField budgetField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> priorityComboBox;
    @FXML private TextField departmentField;
    @FXML private TextField ownerField;

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> nameCol;
    @FXML private TableColumn<Project, String> statusCol;
    @FXML private TableColumn<Project, Double> budgetCol;
    @FXML private TableColumn<Project, String> startCol;
    @FXML private TableColumn<Project, String> endCol;
    @FXML private TableColumn<Project, String> priorityCol;
    @FXML private TableColumn<Project, String> departmentCol;
    @FXML private TableColumn<Project, String> ownerCol;

    private final ObservableList<Project> projects = FXCollections.observableArrayList();

    private final String sender = "singh.navdeep.htl.donaustadt@gmail.com";
    private final String password = "kcvk hhnf qlrp lddm";
    private final String recipient = "navdeepsingh@live.at";
    //Ines.Lanner@translogica.net
    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList("Geplant", "In Umsetzung", "Abgeschlossen"));
        priorityComboBox.setItems(FXCollections.observableArrayList("Hoch", "Mittel", "Niedrig"));

        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        budgetCol.setCellValueFactory(data -> data.getValue().budgetProperty().asObject());
        startCol.setCellValueFactory(data -> data.getValue().startDateProperty());
        endCol.setCellValueFactory(data -> data.getValue().endDateProperty());
        priorityCol.setCellValueFactory(data -> data.getValue().priorityProperty());
        departmentCol.setCellValueFactory(data -> data.getValue().departmentProperty());
        ownerCol.setCellValueFactory(data -> data.getValue().ownerProperty());

        projectTable.setItems(projects);
        projectTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    @FXML
    public void addProject() {
        String name = nameField.getText();
        String status = statusComboBox.getValue();
        String budgetText = budgetField.getText();
        String startDate = startDatePicker.getValue() != null ? startDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
        String endDate = endDatePicker.getValue() != null ? endDatePicker.getValue().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "";
        String priority = priorityComboBox.getValue();
        String department = departmentField.getText();
        String owner = ownerField.getText();

        if (name.isEmpty() || status == null || budgetText.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || priority == null || department.isEmpty() || owner.isEmpty()) {
            showAlert("Fehlende Angaben", "Bitte füllen Sie alle Felder vollständig aus.");
            return;
        }

        try {
            double budget = Double.parseDouble(budgetText);
            projects.add(new Project(name, status, budget, startDate, endDate, priority, department, owner));

            nameField.clear();
            statusComboBox.setValue(null);
            budgetField.clear();
            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            priorityComboBox.setValue(null);
            departmentField.clear();
            ownerField.clear();
        } catch (NumberFormatException e) {
            showAlert("Ungültige Eingabe", "Budget muss eine Zahl sein.");
        }
    }

    @FXML
    private void handleExport() {
        Stage dialog = createLoadingDialog("Bitte warten", "Die Projektübersicht wird erstellt und versendet...");
        dialog.show();

        new Thread(() -> {
            try {
                exportAsPdf();
                Platform.runLater(() -> {
                    dialog.close();
                    showInfo("Erfolg", "Die Projektübersicht wurde erfolgreich versendet.");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    dialog.close();
                    showAlert("Fehler", e.getMessage());
                });
            }
        }).start();
    }

    private void exportAsPdf() throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("Projekte.pdf"));
        document.open();

        document.add(new Paragraph("Projektübersicht\n"));
        document.add(new Paragraph("Diese automatisch generierte Liste enthält alle eingegebenen Projektinformationen.\n\n"));

        for (Project p : projects) {
            document.add(new Paragraph(
                    "Projektname: " + p.nameProperty().get() +
                            " | Status: " + p.statusProperty().get() +
                            " | Budget: €" + p.budgetProperty().get() +
                            " | Start: " + p.startDateProperty().get() +
                            " | Ende: " + p.endDateProperty().get() +
                            " | Priorität: " + p.priorityProperty().get() +
                            " | Bereich: " + p.departmentProperty().get() +
                            " | Verantwortlich: " + p.ownerProperty().get()
            ));
        }

        document.close();
        sendMailWithPdfAttachment("Projekte.pdf");
    }

    private void sendMailWithPdfAttachment(String filename) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            message.setSubject("Bewerbungsprojekt – Projektübersicht");

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(
                    "Lieber Frau Lanner,\n\n" +
                            "anbei erhalten Sie wie angekündigt meine automatisch generierte Projektübersicht als PDF.\n" +
                            "Dieses Beispielprojekt zeigt mein Prozessverständnis sowie die Fähigkeit, komplexe Abläufe zu strukturieren und automatisieren.\n\n" +
                            "Ich freue mich auf unser Gespräch und sende herzliche Grüße,\n\nNavdeep Singh"
            );

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(filename));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);
            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Mailversand fehlgeschlagen: " + e.getMessage());
        }
    }

    private Stage createLoadingDialog(String title, String message) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        ProgressIndicator spinner = new ProgressIndicator();

        VBox vbox = new VBox(15, label, spinner);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene scene = new Scene(vbox, 320, 150);
        dialog.setScene(scene);
        return dialog;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> alert.close());
        delay.play();
    }
}

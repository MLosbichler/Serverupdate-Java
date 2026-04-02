package com.dvhaus;

import java.io.File;

import com.dvhaus.hostreader.HostsEntry;
import com.dvhaus.hostreader.HostsReader;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ServerUpdateApp extends Application {
    public static HostsReader hostsReader = new HostsReader();
    private final TableView<HostsEntry> hosts = new TableView<>();

    @Override
    public void start(final Stage stage) {
        TableColumn<HostsEntry, String> firstCol = new TableColumn<>("Ip");
        TableColumn<HostsEntry, String> secondCol = new TableColumn<>("Hostname");
        hosts.getColumns().addAll(firstCol, secondCol);

        firstCol.setCellValueFactory(new PropertyValueFactory<>("Ip"));
        secondCol.setCellValueFactory(new PropertyValueFactory<>("Hostname"));
        hosts.setItems(hostsReader.getHosts());
        hosts.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Label usernameTitle = new Label("Nutzername:");
        TextField tfUsername = new TextField();
        tfUsername.setPromptText("Nutzername eingeben");

        Label passwordTitle = new Label("Passwort:");
        PasswordField pfPassword = new PasswordField();
        pfPassword.setPromptText("Passwort eingeben");

        Label sudoTitle = new Label("Sudo-Passwort:");
        PasswordField pfSudo = new PasswordField();
        pfSudo.setPromptText("Sudo-Passwort eingeben");

        Label fileTitle = new Label("update.tar:");
        TextField tfFilePath = new TextField();
        tfFilePath.setPromptText("Pfad zur Datei...");
        tfFilePath.setEditable(false);

        Button btnBrowse = new Button("Durchsuchen");
        btnBrowse.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("update.tar auswählen");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TAR-Dateien", "*.tar"),
                new FileChooser.ExtensionFilter("Alle Dateien", "*.*")
            );
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                tfFilePath.setText(selectedFile.getAbsolutePath());
            }
        });

        Button update = new Button("Update starten");
        update.setOnAction(e -> {
            String username = tfUsername.getText();
            String password = pfPassword.getText();
            String sudo = pfSudo.getText();
            String filePath = tfFilePath.getText();
            ObservableList<HostsEntry> selectedServers = hosts.getSelectionModel().getSelectedItems();

            for (HostsEntry entry : selectedServers) {
                ServerConnector connector = new ServerConnector(
                    entry.getIp(),
                    username,
                    password,
                    sudo,
                    22,
                    filePath,
                    "/home/arcisadm/"
                );
                connector.setDaemon(true);
                connector.setName("Connector-" + entry.getIp());
                connector.start();
                System.out.println("Thread gestartet für: " + entry.getIp());
            }
        });

        HBox fileBox = new HBox(8, tfFilePath, btnBrowse);
        HBox.setHgrow(tfFilePath, Priority.ALWAYS);
        fileBox.setAlignment(Pos.CENTER_LEFT);
        VBox inputPanel = new VBox(10,
            usernameTitle, tfUsername,
            passwordTitle, pfPassword,
            sudoTitle, pfSudo,
            fileTitle, fileBox
        );
        inputPanel.getChildren().add(update);
        inputPanel.setPadding(new Insets(10));
        inputPanel.setPrefWidth(250);
        inputPanel.setAlignment(Pos.TOP_LEFT);

        HBox root = new HBox(10, hosts, inputPanel);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("Serverupdate");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(ServerUpdateApp.class, args);
    }
}

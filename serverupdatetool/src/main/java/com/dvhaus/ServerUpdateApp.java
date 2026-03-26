package com.dvhaus;

import com.dvhaus.hostreader.HostsEntry;
import com.dvhaus.hostreader.HostsReader;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerUpdateApp extends Application {
    public static HostsReader hostsReader = new HostsReader();
    private final TableView<HostsEntry> hosts = new TableView<>();

    @Override
    public void start(final Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setTitle("Serverupdate");
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.show();

        Label hostsTitle = new Label("Hosts:");
        TableColumn<HostsEntry, String> firstCol = new TableColumn<>("Ip");
        TableColumn<HostsEntry, String> secondCol = new TableColumn<>("Hostname");
        hosts.getColumns().addAll(firstCol, secondCol);

        firstCol.setCellValueFactory(new PropertyValueFactory<>("Ip"));
        secondCol.setCellValueFactory(new PropertyValueFactory<>("Hostname"));
        hosts.setItems(hostsReader.getHosts());

        final VBox vbox = new VBox();
        vbox.setSpacing(4);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(hostsTitle, hosts);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);
 
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(ServerUpdateApp.class, args);
    }
}

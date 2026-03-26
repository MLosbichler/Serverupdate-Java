package com.dvhaus.hostreader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * 
 * @author Manuel Losbichler
 * @version 1.0, 26.03.2026
 */
public class HostsReader {
    private final String HOSTSPATH = "C:\\Windows\\System32\\drivers\\etc\\hosts";
    private List<HostsEntry> hosts;

    public HostsReader() {
        try {
            readHosts();
        } catch (IOException ex) {
            System.getLogger(HostsReader.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public ObservableList<HostsEntry> getHosts() {
        return FXCollections.observableList(hosts);
    }

    private void readHosts() throws IOException {
        hosts = Files.readAllLines(Paths.get(HOSTSPATH))
                    .stream()
                    .map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.contains("#"))
                    .map(line -> line.split("\\s+", 2))
                    .filter(parts -> parts.length == 2)
                    .map(parts -> new HostsEntry(parts[0], parts[1]))
                    .collect(Collectors.toList());
    }
}

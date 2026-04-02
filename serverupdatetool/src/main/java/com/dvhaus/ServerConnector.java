package com.dvhaus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;

/**
 * An instance of this class handles a thread with a server connection.
 * Any operation on the server are executed by an instance of this class, including 
 * establishing the connection, uploading files, executing commands and reading the servers command line.
 * This class extends the class Thread in order to allow for multiple server connections at a time.
 * 
 * @author Manuel Losbichler
 * @version 0.1, 19.03.2026
 */
public class ServerConnector extends Thread {
    private final String COMMAND = "sudo ./MakeUpdate.sh"; //maybe make this adjustable in the UI later.
    private final String ip;
    private final String username;
    private final String password;
    private final String sudoPassword;
    private final int port;
    private final String uploadFilePath;
    private final String remoteFilePath;
    private volatile boolean running = true;

    public ServerConnector(final String ip, final String username, final String password, final String sudoPassword,
        final int port, final String uploadFilePath, final String remoteFilePath) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.sudoPassword = sudoPassword;
        this.port = port;
        this.uploadFilePath = uploadFilePath;
        this.remoteFilePath = remoteFilePath;
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run() {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();

        try (ClientSession session = client.connect(username, ip, port)
                .verify(10, TimeUnit.SECONDS)
                .getSession()) {
            //Creating a session with the server.
            session.addPasswordIdentity(password);
            session.auth().verify(10, TimeUnit.SECONDS);
            //Uploading the selected update.tar to the server and starting the updating process.
            uploadFile(session, uploadFilePath, remoteFilePath);
//            executeUpdate(session, COMMAND);
        } catch (IOException e) {
            System.err.println("Fehler bei Verbindung zu " + ip + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.stop();
        }
    }

    private void uploadFile(final ClientSession session, final String localPath, final String remotePath) throws IOException {
        // TODO : check for already existing update file.
        ScpClientCreator creator = ScpClientCreator.instance();
        ScpClient scpClient = creator.createScpClient(session);
        Path local = Paths.get(localPath);
        scpClient.upload(local, remotePath);
        System.out.println("update.tar erfolgreich hochgeladen!");
    }

    private String executeUpdate(final ClientSession session, final String command) throws IOException {
        // TODO : implement a listener for the server command lines.
        try (ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
             ChannelExec channel = session.createExecChannel(command)) {
            channel.setOut(responseStream);
            channel.setErr(responseStream);
            channel.open().verify(5, TimeUnit.SECONDS);
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(10));
            return responseStream.toString();
        }
    }
}

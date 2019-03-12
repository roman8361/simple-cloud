package ru.kravchenko.cloud.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ru.kravchenko.cloud.common.*;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Roman Kravchenko
 */

public class MainController implements Initializable {

    private boolean autorized;

    public void setAutorized(boolean autorized) {
        this.autorized = autorized;
        if (autorized) {
            authPanel.setVisible(false);
            authPanel.setManaged(false);
            cloudPanel.setVisible(true);
            cloudPanel.setManaged(true);
            filesListClient.setVisible(true);
            filesListClient.setManaged(true);
            filesListClient.setVisible(true);
            filesListClient.setManaged(true);
            getFilesFromServerStorage();
        } else {
            authPanel.setVisible(true);
            authPanel.setManaged(true);
            cloudPanel.setVisible(false);
            cloudPanel.setManaged(false);
            filesListClient.setVisible(false);
            filesListClient.setManaged(false);
            filesListClient.setVisible(false);
            filesListClient.setManaged(false);
        }
    }

    @FXML
    HBox authPanel, cloudPanel;

    @FXML
    TextField loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    ListView<String> filesListClient;

    @FXML
    ListView<String> filesListServer;

    @FXML
    Button buttonAuth, buttonDelFileServer, buttonDownloadFileServer, buttonGetFilesFromServer,
            buttonDelFileClient, buttonSendFileToServer;

    EventHandler<KeyEvent> buttonKeyEnter = (event -> { // Нажатие кнопки по  Enter
        if(event.getCode() == KeyCode.ENTER) {
            ((Button)event.getSource()).getOnAction().handle(new ActionEvent());
        }
    });

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Network.start();
        threadStart();
        onMouseClickHandler(filesListClient);
        onMouseClickHandler(filesListServer);
        setAutorized(false);
        initializedButtonEnterPress();
    }

    public void refreshLocalFilesList() {
        if (Platform.isFxApplicationThread()) {
            try {
                fileListRefreshClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Platform.runLater(() -> {
                try {
                    fileListRefreshClient();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void fileListRefreshClient() throws IOException {
        filesListClient.getItems().clear();
        Files.list(Paths.get("storage/client_storage/")).map(p -> p.getFileName().toString())
                .forEach(o -> filesListClient.getItems().add(o));
    }

    public void delFileFromClientStorage() {
        try {
            Files.delete(Paths.get("storage/client_storage/" + filesListClient.getSelectionModel()
                    .getSelectedItem()));
            refreshLocalFilesList();
            System.out.println("delFileFromClientStorage");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMouseClickHandler(ListView<String> filesList) {
        filesList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 1) {
                    System.out.println(filesListClient.getSelectionModel().getSelectedItem());
                    System.out.println(filesListServer.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void threadStart() {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    AbstractMessage am = Network.readObject();

                    if (am instanceof FileMessage) {
                        FileMessage fm = (FileMessage) am;
                        Files.write(Paths.get("storage/client_storage/" + fm.getFilename()), fm.getData(), StandardOpenOption.CREATE);
                        refreshLocalFilesList();
                    }

                    if (am instanceof FileListFromServerStorage) {
                        FileListFromServerStorage fileListFromServerStorage = (FileListFromServerStorage) am;
                        List<String> fileListToServer = fileListFromServerStorage.getFilesListFromServerStorage();
                        ObservableList<String> list = FXCollections.observableArrayList();
                        list.addAll(fileListToServer);
                        Platform.runLater(() -> { filesListServer.setItems(list); });
                    }

                    if (am instanceof AutoMessageToClient) { // блок авторизации
                        AutoMessageToClient autoMessageToClient = (AutoMessageToClient) am;
                        if (autoMessageToClient.isChekRegistry()) setAutorized(true);
                   }

                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                Network.stop();
            }
        });
        t.setDaemon(true);
        t.start();
        refreshLocalFilesList();
    }

    public void sendFileToServer() throws IOException {
        Path dataPath = Paths.get( "storage/client_storage/" + filesListClient.getSelectionModel().getSelectedItem());
        FileMessage fileMessageObject = new FileMessage(dataPath);
        Network.sendMessage(fileMessageObject);
    }

    public void getFilesFromServerStorage() {
        FileRequest cmdMessage = new FileRequest("CMD_getFiles");
        Network.sendMessage(cmdMessage);
        filesListServer.getItems().clear();
    }

    public void delFileFromServerStorage() {
        FileRequest cmdMessage = new FileRequest("CMD_delFiles", filesListServer.getSelectionModel().getSelectedItem());
        Network.sendMessage(cmdMessage);
        filesListServer.getItems().clear();
        getFilesFromServerStorage();
    }

    public void sendAuthorizationMessage(ActionEvent actionEvent) {
        AutoMessageToServer messageFromClient = new AutoMessageToServer(loginField.getText(), passwordField.getText());
        Network.sendMessage(messageFromClient);
    }

    public void downloadFileFromServer(ActionEvent actionEvent) {
        FileRequest cmdMessage = new FileRequest("CMD_downloadFiles", filesListServer.getSelectionModel().getSelectedItem());
        Network.sendMessage(cmdMessage);
    }

    public void initializedButtonEnterPress() {
        buttonAuth.setOnKeyReleased(buttonKeyEnter);
        buttonDelFileServer.setOnKeyReleased(buttonKeyEnter);
        buttonDownloadFileServer.setOnKeyReleased(buttonKeyEnter);
        buttonGetFilesFromServer.setOnKeyReleased(buttonKeyEnter);
        buttonDelFileClient.setOnKeyReleased(buttonKeyEnter);
        buttonSendFileToServer.setOnKeyReleased(buttonKeyEnter);
    }

}



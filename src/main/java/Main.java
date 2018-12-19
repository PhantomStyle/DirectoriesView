import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {

    private List<String> findedFiles = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        ScrollPane scrollPane = new ScrollPane();
        GridPane b = new GridPane();
        TextField textField = new TextField();
        Button textButton = new Button();
        b.add(textField, 0, 5);
        b.add(textButton, 0, 6);
        textButton.setText("Find");
        Button c = new Button("Load Folder");
        ListViewChanger listViewChanger = new ListViewChanger();
        listViewChanger.buttonList.add(textButton);
        listViewChanger.textFieldList.add(textField);
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                DirectoryChooser dc = new DirectoryChooser();
                dc.setInitialDirectory(new File(System.getProperty("user.home")));
                File choice = dc.showDialog(primaryStage);
                listViewChanger.currentPath.setText(choice.getAbsolutePath());
                listViewChanger.filesList.add(choice);
                if (choice == null || !choice.isDirectory()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");

                    alert.showAndWait();
                } else {
                    b.getChildren().clear();
                    listViewChanger.column = 2;
                    //TODO: на начальную вьюху повесить функционал с удалением
                    if (listViewChanger.viewList.get(0).getItems().size() != 0) {
                        listViewChanger.viewList.get(0).getItems().clear();
                    }
                    b.add(c, 0, 0);
                    b.add(listViewChanger.viewList.get(0), 0, 1);
                    b.add(textField, 0, 5);
                    b.add(textButton, 0, 6);
//                    for (int i = listViewChanger.viewList.size() - 1; i >= 1; i--) {
//                        ListView<String> view = listViewChanger.viewList.get(i);
//                        b.getChildren().remove(view);
//                        listViewChanger.viewList.remove(i);
//                    }
//                    for (int i = listViewChanger.buttonList.size() - 1; i >= 1; i--) {
//                        Button button = listViewChanger.buttonList.get(i);
//                        b.getChildren().remove(button);
//                        listViewChanger.buttonList.remove(i);
//                    }
//                    for (int i = listViewChanger.textFieldList.size() - 1; i >= 1; i--) {
//                        TextField text = listViewChanger.textFieldList.get(i);
//                        b.getChildren().remove(text);
//                        listViewChanger.textFieldList.remove(i);
//                    }
//                    b.setGridLinesVisible(false);
//                    textField.setVisible(true);
//                    textButton.setVisible(true);
                    listViewChanger.viewList.get(0).setVisible(true);

                    listViewChanger.clearAllLists(b);
//                    listViewChanger.filesList.clear();
                    String name = choice.getName().equals("") ? choice.getAbsolutePath() : choice.getName();
                    name = name.replace("\\", "");
                    listViewChanger.viewList.get(0).getItems().addAll(name);
                    listViewChanger.filesList.add(new File(choice.getAbsolutePath()));
                }
            }
        });
        b.add(c, 0, 0);
        b.add(listViewChanger.viewList.get(0), 0, 1);
        listViewChanger.viewList.get(0).getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        ListView<String> listView = listViewChanger.getNewListView(b);
                        listViewChanger.viewList.add(listView);
//                        try {
                        File[] files = listViewChanger.filesList.get(0).listFiles();
                        for (File f : files) {
                            listViewChanger.viewList.get(1).getItems().add(f.getName());
                        }
                        b.add(listView, 1, 1);
                    }
                });
        textButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                findedFiles.clear();
                if (textField.getText().isEmpty()) {
                    Stage st = new Stage();
                    st.initModality(Modality.APPLICATION_MODAL);
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("Field is empty!"));
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    st.setScene(dialogScene);
                    st.show();
                    return;
                }
                String desiredFileName = textField.getText();
                File file = listViewChanger.filesList.get(listViewChanger.filesList.size() - 2);
                if (file.getName().contains(".")) {
                    file = listViewChanger.filesList.get(listViewChanger.filesList.size() - 3);
                }
                searchFile(file, desiredFileName);
                if (findedFiles.isEmpty()) {
                    Stage st = new Stage();
                    st.initModality(Modality.APPLICATION_MODAL);
                    VBox dialogVbox = new VBox(20);
                    dialogVbox.getChildren().add(new Text("No files found!"));
                    Scene dialogScene = new Scene(dialogVbox, 300, 200);
                    st.setScene(dialogScene);
                    st.show();
                } else {
                    Stage st = new Stage();
                    st.initModality(Modality.APPLICATION_MODAL);
                    VBox dialogVbox = new VBox(20);
                    ListView view = new ListView();
                    view.getItems().addAll(findedFiles);
                    dialogVbox.getChildren().add(view);
                    Scene dialogScene = new Scene(dialogVbox, 600, 500);
                    st.setScene(dialogScene);
                    st.show();
                }
                textField.clear();
            }
        });

        VBox vBox = new VBox();
        vBox.getChildren().add(listViewChanger.currentPath);
        vBox.getChildren().add(b);
        scrollPane.setContent(vBox);
        scrollPane.hvalueProperty().bind(vBox.widthProperty());
        primaryStage.setScene(new Scene(scrollPane, 600, 500));
        primaryStage.setTitle("Folder View");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void searchFile(File directory, String desiredFileName) {
        File[] directories = directory.listFiles();
        try {
            for (File file : directories) {
                if (file.getName().equalsIgnoreCase(desiredFileName)) {
                    findedFiles.add(file.getAbsolutePath());
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("Just nullpointer");
        }
    }
}

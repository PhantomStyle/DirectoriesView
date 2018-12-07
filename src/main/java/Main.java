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
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        ScrollPane scrollPane = new ScrollPane();
        GridPane b = new GridPane();
        TextField textField = new TextField();
        Button textButton = new Button();
//        b.add(textField, 0, 2);
//        b.add(textButton, 1, 2);
        textButton.setText("Find");
        Button c = new Button("Load Folder");
        ListViewChanger listViewChanger = new ListViewChanger();
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
//                listViewChanger.viewList.clear();
//                listViewChanger.filesList.clear();
                DirectoryChooser dc = new DirectoryChooser();
                dc.setInitialDirectory(new File(System.getProperty("user.home")));
                File choice = dc.showDialog(primaryStage);
                listViewChanger.filesList.add(choice);
                if (choice == null || !choice.isDirectory()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Could not open directory");
                    alert.setContentText("The file is invalid.");

                    alert.showAndWait();
                } else {
                    listViewChanger.column = 2;
                    //TODO: когда заново выбираем, косячим
                    //TODO: на начальную вьюху повесить функционал с удалением
                    if (listViewChanger.viewList.get(0).getItems().size() != 0) {
                        listViewChanger.viewList.get(0).getItems().clear();
                    }
                    int size = listViewChanger.viewList.size();
                    for (int i = listViewChanger.viewList.size() - 1; i >= 1; i--) {
                        b.getChildren().remove(listViewChanger.viewList.remove(i));
                    }
                    for (int i = listViewChanger.filesList.size() - 1; i >= 1; i--) {
                        listViewChanger.filesList.remove(i);
                    }
                    String name = choice.getName().equals("") ? choice.getAbsolutePath() : choice.getName();
                    name = name.replace("\\", "");
                    listViewChanger.viewList.get(0).getItems().addAll(name);
                }
            }
        });

        //TODO:когда возвращаемся назад, он не обновляет почему-то следующую папку
//        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
//                    if (mouseEvent.getClickCount() == 2) {
//                        for (int i = listViewChanger.viewList.get(0).size() - 1; i > viewList.indexOf(listView); i--) {
//                            viewList.remove(i);
//                            try {
//                                filesList.remove(i - 1);
//                            } catch (IndexOutOfBoundsException ex) {
//                                System.out.println("Last file");
//                            }
//                            b.getChildren().remove(column);
//                            column--;
//                        }
//                    }
//                }
//            }
//        });
        b.add(c, 0, 0);
        b.add(listViewChanger.viewList.get(0), 0, 1);
        listViewChanger.viewList.get(0).getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        ListView<String> listView = listViewChanger.getNewListView(b);
                        listViewChanger.viewList.add(listView);
                        File[] files = listViewChanger.filesList.get(0).listFiles();
                        for (File f : files) {
                            listViewChanger.viewList.get(1).getItems().add(f.getName());
                        }
                        b.add(listView, 1, 1);
                    }
                });
        VBox vBox = new VBox();
        vBox.getChildren().add(b);
        scrollPane.setContent(vBox);
        scrollPane.hvalueProperty().bind(vBox.widthProperty());
        primaryStage.setScene(new Scene(scrollPane, 600, 400));
        primaryStage.setTitle("Folder View");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

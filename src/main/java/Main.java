import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane b = new GridPane();
        Button c = new Button("Load Folder");
        ListViewChanger listViewChanger = new ListViewChanger();
        c.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
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
                    if (listViewChanger.viewList.get(0).getItems().size() != 0) {
                        listViewChanger.viewList.get(0).getItems().clear();
                    }
                    for (int i = 1; i < listViewChanger.viewList.size(); i++) {
                        listViewChanger.viewList.remove(i);
                    }
                    String name = choice.getName().equals("") ? choice.getAbsolutePath() : choice.getName();
                    name = name.replace("\\", "");
                    listViewChanger.viewList.get(0).getItems().addAll(name);
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
                        File[] files = listViewChanger.filesList.get(0).listFiles();
                        for (File f : files) {
                            listViewChanger.viewList.get(1).getItems().add(f.getName());
                        }
                        b.add(listView, 1, 1);
                    }
                });
        primaryStage.setScene(new Scene(b, 600, 400));
        primaryStage.setTitle("Folder View");
        primaryStage.show();
    }
}

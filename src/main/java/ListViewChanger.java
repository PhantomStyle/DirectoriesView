import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ListViewChanger {

    public int column = 2;

    public final List<ListView<String>> viewList;
    public final List<File> filesList;

    public ListViewChanger() {
        viewList = new ArrayList<>();
        viewList.add(new ListView<>());
        filesList = new ArrayList<>();
    }

    public ListView<String> getNewListView(GridPane b) {
        ListView<String> listView = new ListView<>();
        listView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        if(viewList.indexOf(listView) < viewList.size() - 1){
                            for(int i = viewList.indexOf(listView); i < viewList.size(); i++){
                                viewList.remove(i);
                                filesList.remove(i);
                                b.getChildren().remove(column);
                                column--;
                            }
                        }
                        ListView<String> listViewNew = getNewListView(b);
//                        if (listView.getSelectionModel().getSelectedItems().size() == 1) {

                        String name = "";
                        if(filesList.get(filesList.size() - 1).getAbsolutePath().endsWith("\\")){
                            name = filesList.get(filesList.size() - 1).getAbsolutePath()
                                    + viewList.get(column - 1).getSelectionModel().getSelectedItems().get(0);
                        } else {
                            name = filesList.get(filesList.size() - 1).getAbsolutePath() + "\\"
                                    + viewList.get(column - 1).getSelectionModel().getSelectedItems().get(0);
                        }
                            File selectedFile = new File(name);
                            viewList.add(listViewNew);
                            if(selectedFile.isDirectory()) {
                                File[] files = selectedFile.listFiles();
                                for (File f : files) {
                                    viewList.get(column).getItems().add(f.getName());
                                }
                                filesList.add(selectedFile);
                                b.add(listViewNew, column++, 1);
                            }
//                        }
                    }
                });
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println("Double clicked");
                    }
                }
            }
        });
        return listView;
    }
    ///if folder is empty neeed

}

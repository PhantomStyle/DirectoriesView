import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import jdk.nashorn.internal.objects.NativeUint8Array;

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
                        if (viewList.indexOf(listView) < viewList.size() - 1) {
                            int size = viewList.size();
                            for (int i = viewList.size() - 1; i >= viewList.indexOf(listView) + 1; i--) {
                                viewList.remove(i);
//                                b.getChildren().remove(viewList.remove(i));
                                try {
                                    filesList.remove(i - 1);
                                } catch (IndexOutOfBoundsException ex) {
                                    System.out.println("Last file");
                                }
//                                b.getChildren().remove(column);
                                b.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == column);
                                column--;
                            }
                        }
                        ListView<String> listViewNew = getNewListView(b);

                        String name = "";
                        if (filesList.get(filesList.size() - 1).getAbsolutePath().endsWith("\\")) {
                            name = filesList.get(filesList.size() - 1).getAbsolutePath()
                                    + viewList.get(column - 1).getSelectionModel().getSelectedItems().get(0);
                        } else {
                            name = filesList.get(filesList.size() - 1).getAbsolutePath() + "\\"
                                    + viewList.get(column - 1).getSelectionModel().getSelectedItems().get(0);
                        }
                        File selectedFile = new File(name);
                        if (selectedFile.isDirectory()) {
                            viewList.add(listViewNew);
                            File[] files = selectedFile.listFiles();
                            try {
                                for (File f : files) {
                                    viewList.get(column).getItems().add(f.getName());
                                }
                            } catch (NullPointerException ex){
                                System.out.println("Just nullpointer. Don't worry");
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
                if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
//                    if (mouseEvent.getClickCount() == 2) {
                        int size = viewList.size();
                        int index = viewList.indexOf(listView);
                        for (int i = viewList.size() - 1; i >= index + 1; i--) {
                            viewList.remove(i);
//                            b.getChildren().remove(viewList.remove(i));
                            try {
                                filesList.remove(i - 1);
                            } catch (IndexOutOfBoundsException ex) {
                                System.out.println("Last file");
                            }
                            b.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == column - 1);
                            column--;
                        }
//                    }
                }
            }
        });
        return listView;
    }
    ///if folder is empty neeed

}

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    public final List<Button> buttonList;
    public final List<TextField> textFieldList;

    public Text currentPath = new Text();

    public ListViewChanger() {
        viewList = new ArrayList<>();
        viewList.add(new ListView<>());
        filesList = new ArrayList<>();
        buttonList = new ArrayList<>();
        textFieldList = new ArrayList<>();
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
                                buttonList.remove(i);
                                textFieldList.remove(i);
                                try {
                                    filesList.remove(i - 1);
                                } catch (IndexOutOfBoundsException ex) {
                                    System.out.println("Last file");
                                }
                                b.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == column);
                                column--;
                            }
                        }
                        ListView<String> listViewNew = getNewListView(b);

                        String name = "";
                        try {
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
                                } catch (NullPointerException ex) {
                                    System.out.println("Just nullpointer. Don't worry");
                                }
                                filesList.add(selectedFile);
                                b.add(listViewNew, column++, 1);
                            } else {
                                b.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == column);
                            }
                        } catch (IndexOutOfBoundsException ex) {
                            System.out.println("Just IndexOutOfBoundsException");
                        }
                        currentPath.setText(name);
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
                    viewList.get(column - 1).getSelectionModel().clearSelection();
                    currentPath.setText(filesList.get(filesList.size() - 1).getAbsolutePath());
                }
            }
        });
        TextField textField = new TextField();
        Button textButton = new Button();
        b.add(textField, column - 1, 5);
        b.add(textButton, column - 1, 6);
        textButton.setText("Find");
        buttonList.add(textButton);
        textFieldList.add(textField);
        String tempPath = filesList.get(filesList.size() - 1).getAbsolutePath();
        textButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                List<String> findedFiles = null;
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
                File file = new File(tempPath);
//                File file = filesList.get(textFieldList.indexOf(textField)/2 - 1);
                findedFiles = searchFile(file, desiredFileName);
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

        File tempFile = new File(tempPath + "\\" + viewList.get(viewList.size() - 1).getSelectionModel().getSelectedItems().get(0));
        if (tempFile.isDirectory()) {
//        if (!filesList.get(filesList.size() - 1).getName().contains(".")) {
//        if (viewList.size() > filesList.size()) {
            TextField textFieldNext = new TextField();
            Button textButtonNext = new Button();
            b.add(textFieldNext, column, 5);
            b.add(textButtonNext, column, 6);
            textButtonNext.setText("Find");
            buttonList.add(textButtonNext);
            textFieldList.add(textFieldNext);
            textButtonNext.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    List<String> findedFiles = null;
                    if (textFieldNext.getText().isEmpty()) {
                        Stage st = new Stage();
                        st.initModality(Modality.APPLICATION_MODAL);
                        VBox dialogVbox = new VBox(20);
                        dialogVbox.getChildren().add(new Text("Field is empty!"));
                        Scene dialogScene = new Scene(dialogVbox, 300, 200);
                        st.setScene(dialogScene);
                        st.show();
                        return;
                    }
                    String desiredFileName = textFieldNext.getText();
                    File file = filesList.get(filesList.indexOf(tempFile));
//                    File file = filesList.get(textFieldList.indexOf(textFieldNext)/2 - 1);
                    findedFiles = searchFile(file, desiredFileName);
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
                    textFieldNext.clear();
                }
            });
        }
        return listView;
    }

    private List<String> searchFile(File directory, String desiredFileName) {
        List<String> findedFiles = new ArrayList<>();
        File[] files = directory.listFiles();
        try {
            for (File file : files) {
                if (file.getName().equalsIgnoreCase(desiredFileName)) {
                    findedFiles.add(file.getAbsolutePath());
                }
            }
        } catch (NullPointerException ex) {
            System.out.println("Just nullpointer");
        }
        return findedFiles;
    }

    private void clearViews(GridPane b){
        for (int i = viewList.size() - 1; i >= 1; i--) {
            ListView<String> view = viewList.get(i);
            b.getChildren().remove(view);
            viewList.remove(i);
        }
    }

    private void clearButtons(GridPane b){
        for (int i = buttonList.size() - 1; i >= 1; i--) {
            Button button = buttonList.get(i);
            b.getChildren().remove(button);
            buttonList.remove(i);
        }
    }

    private void clearTextFields(GridPane b){
        for (int i = textFieldList.size() - 1; i >= 1; i--) {
            TextField textField = textFieldList.get(i);
            b.getChildren().remove(textField);
            textFieldList.remove(i);
        }
    }

    public void clearAllLists(GridPane b){
        for(int i = 0; i < 10; i++) {
            clearViews(b);
            clearButtons(b);
            clearTextFields(b);
            filesList.clear();
        }
    }
}

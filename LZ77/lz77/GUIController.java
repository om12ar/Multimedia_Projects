/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lz77;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author omar
 */
public class GUIController implements Initializable {

    @FXML
    private Label Status;
    @FXML
    private TextField pathView;

    String path = "in.txt";

    @FXML
    private void compressButton(ActionEvent event) {
        Status.setText("DeCompressing...");
        try {
            LZ77.compress(path);
        } catch (Exception FilException) {
            Status.setText("File not found");
        }
        Status.setText("DONE !");
    }

    @FXML
    private void decompressButton(ActionEvent event) {
        Status.setText("DeCompressing...");
        LZ77.deCompress();
        Status.setText("DONE !");
    }

    @FXML
    private void changePath(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());

        if (file != null) {
            path = file.getPath();
            System.out.println("---------------" + pathView.getId());
            pathView.setText(path);
            System.err.println(path);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

}

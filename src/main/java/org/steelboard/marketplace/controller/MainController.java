package org.steelboard.marketplace.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainController implements Initializable {

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private FlowPane productsFlowPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}

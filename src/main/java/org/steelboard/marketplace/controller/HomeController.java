package org.steelboard.marketplace.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.FlowPane;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.service.ProductService;
import org.steelboard.marketplace.ui.ProductCard;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class HomeController implements Initializable {

    @FXML
    private FlowPane productsFlowPane;
    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initProductList();
    }

    private void initProductList() {
        Page<Product> productsPage = productService.getAllProducts(0, 20);
        Platform.runLater(() -> {
            for (Product product : productsPage.stream().toList()) {
                productsFlowPane.getChildren().add(new ProductCard(product));
            }
        });

    }

    @FXML
    private void refresh() {
        initProductList();
    }
}

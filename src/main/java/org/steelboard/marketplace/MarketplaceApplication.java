package org.steelboard.marketplace;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.steelboard.marketplace.ui.JavaFXApplication;

@SpringBootApplication
public class MarketplaceApplication {

    public static void main(String[] args) {
        Application.launch(JavaFXApplication.class, args);
    }
}

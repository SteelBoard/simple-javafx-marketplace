package org.steelboard.marketplace;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UiApplication {

    public static void main(String[] args) {
        Application.launch(MarketplaceApplication.class, args);
    }
}

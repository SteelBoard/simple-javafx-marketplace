package org.steelboard.marketplace.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import org.steelboard.marketplace.entity.ImageType;
import org.steelboard.marketplace.entity.Product;

import java.util.Objects;

public class ProductCard extends VBox {

    // Геттер для продукта
    @Getter
    private Product product;
    private ImageView productImage;
    private Label titleLabel;
    private Label sellerLabel;
    private HBox ratingBox;
    private Label descriptionLabel;
    private Label priceLabel;
    // Метод для получения кнопки корзины (для внешнего управления)
    @Getter
    private Button cartButton;

    public ProductCard(Product product) {
        this.product = product;
        initializeCard();
        setupData();
        setupEventHandlers();
    }

    private void initializeCard() {
        // Основные настройки карточки
        this.setPrefWidth(200);
        this.setStyle("-fx-background-color: rgba(255,255,255,0.95); " +
                "-fx-background-radius: 15; " +
                "-fx-padding: 15; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);");

        // Создание элементов
        productImage = createRoundedImage();

        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(180);

        sellerLabel = new Label();
        sellerLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11; -fx-font-style: italic;");

        ratingBox = new HBox(5);
        ratingBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        descriptionLabel = new Label();
        descriptionLabel.setStyle("-fx-text-fill: #34495e; -fx-font-size: 12;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(180);
        descriptionLabel.setMaxHeight(45);

        // Контейнер для цены и кнопки
        HBox bottomBox = new HBox(10);
        bottomBox.setAlignment(javafx.geometry.Pos.CENTER);

        priceLabel = new Label();
        priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 16;");

        cartButton = new Button("В корзину");
        cartButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 30; " +
                "-fx-font-size: 14;");

        bottomBox.getChildren().addAll(priceLabel, cartButton);

        // Добавление элементов в карточку
        this.getChildren().addAll(
                productImage, titleLabel, sellerLabel,
                ratingBox, descriptionLabel, bottomBox
        );
    }

    private ImageView createRoundedImage() {
        ImageView imageView;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                    product.getImages().stream()
                            .filter(n -> n.getType() == ImageType.MAIN)
                            .findFirst().get().getFilepath())));
            imageView = new ImageView(image);
        } catch (Exception e) {
            // Заглушка если изображение не найдено
            imageView = new ImageView();
            imageView.setStyle("-fx-background-color: #ecf0f1; -fx-min-width: 150; -fx-min-height: 150;");
        }

        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Закругление изображения
        Rectangle clip = new Rectangle(150, 150);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imageView.setClip(clip);

        return imageView;
    }

    private void setupData() {
        // Установка данных из продукта
        titleLabel.setText(limitText(product.getName(), 40));
        sellerLabel.setText("Продавец: " + product.getSeller().getName());
        descriptionLabel.setText(limitText(product.getDescription(), 80) + "...");
        priceLabel.setText(formatPrice(product.getPrice()));

        // Создание рейтинга
        createRatingStars();
    }

    private void createRatingStars() {
        double rating = product.getRating();
        int fullStars = (int) rating;
        boolean hasHalfStar = (rating - fullStars) >= 0.5;

        // Звёзды рейтинга
        for (int i = 0; i < fullStars; i++) {
            Label star = new Label("★");
            star.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14;");
            ratingBox.getChildren().add(star);
        }

        if (hasHalfStar) {
            Label halfStar = new Label("★");
            halfStar.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14; -fx-opacity: 0.7;");
            ratingBox.getChildren().add(halfStar);
        }

        int emptyStars = 5 - fullStars - (hasHalfStar ? 1 : 0);
        for (int i = 0; i < emptyStars; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 14;");
            ratingBox.getChildren().add(star);
        }

        // Количество отзывов
        Label count = new Label("(" + product.getRating() + ")");
        count.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");
        ratingBox.getChildren().add(count);
    }

    private void setupEventHandlers() {
        // Обработчик наведения на карточку
        this.setOnMouseEntered(e -> {
            this.setStyle("-fx-background-color: rgba(255,255,255,1); " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 15, 0, 0, 5);");
        });

        this.setOnMouseExited(e -> {
            this.setStyle("-fx-background-color: rgba(255,255,255,0.95); " +
                    "-fx-background-radius: 15; " +
                    "-fx-padding: 15; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);");
        });

        // Обработчик кнопки корзины
        cartButton.setOnAction(e -> {
            System.out.println("Товар добавлен в корзину: " + product.getName());
            // Здесь можно добавить логику добавления в корзину
        });

        cartButton.setOnMouseEntered(e -> {
            cartButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; " +
                    "-fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 30; " +
                    "-fx-font-size: 14;");
        });

        cartButton.setOnMouseExited(e -> {
            cartButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                    "-fx-background-radius: 20; -fx-min-width: 40; -fx-min-height: 30; " +
                    "-fx-font-size: 14;");
        });
    }

    private String limitText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength);
    }

    private String formatPrice(Double price) {
        if (price >= 1000) {
            return String.format("%.0f ₽", price);
        } else {
            return String.format("%.2f ₽", price);
        }
    }

    // Метод для обновления данных карточки
    public void updateProduct(Product updatedProduct) {
        this.product = updatedProduct;
        setupData();
    }

}
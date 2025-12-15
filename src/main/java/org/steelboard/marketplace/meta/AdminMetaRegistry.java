package org.steelboard.marketplace.meta;

import org.springframework.stereotype.Component;
import org.steelboard.marketplace.entity.Product;
import org.steelboard.marketplace.entity.ProductImage;
import org.steelboard.marketplace.entity.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Component
public class AdminMetaRegistry {

    private final Map<String, AdminEntityMeta> registry = new LinkedHashMap<>();

    public AdminMetaRegistry() {

        registry.put("products",
                new AdminEntityMeta(
                        "products",
                        Product.class,
                        Set.of("name", "price", "description", "active"),
                        Set.of("id", "createdAt")
                )
        );

        registry.put("product_images",
                new AdminEntityMeta(
                        "product_images",
                        ProductImage.class,
                        Set.of("filepath", "type", "sortOrder"),
                        Set.of("id", "createdAt")
                )
        );

        registry.put("users",
                new AdminEntityMeta(
                        "users",
                        User.class,
                        Set.of("email", "enabled"),
                        Set.of("id", "password", "createdAt")
                )
        );
    }

    public AdminEntityMeta get(String table) {
        return registry.get(table);
    }

    public Iterable<String> tables() {
        return registry.keySet();
    }
}

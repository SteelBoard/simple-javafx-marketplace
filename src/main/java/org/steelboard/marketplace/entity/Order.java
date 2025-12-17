    package org.steelboard.marketplace.entity;

    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;
    import org.hibernate.annotations.UpdateTimestamp;

    import java.math.BigDecimal;
    import java.util.Date;
    import java.util.Set;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Entity
    @Table(name = "orders")
    public class Order {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @EqualsAndHashCode.Include
        private Long id;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private User user;
        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private OrderStatus status = OrderStatus.PENDING;
        @Column(name = "total_amount")
        private BigDecimal totalAmount;
        @OneToMany(
                fetch = FetchType.EAGER,
                mappedBy = "order",
                cascade = CascadeType.ALL,
                orphanRemoval = true
        )
        private Set<OrderItem> orderItems;
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "pickup_point_id")
        private PickupPoint pickupPoint;
        @CreationTimestamp
        @Column(name = "created_at", updatable = false)
        private Date createdAt;
        @UpdateTimestamp
        @Column(name = "updated_at")
        private Date updatedAt;
    }

package org.steelboard.marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String country = "Россия";

    @Column(nullable = false)
    private String city;              

    @Column(nullable = false)
    private String street;            

    @Column(name = "house_number", nullable = false)
    private String houseNumber;       

    @Column(name = "apartment_number")
    private String apartmentNumber;   

    @Column(name = "postal_code", nullable = false)
    private String postalCode;        

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}

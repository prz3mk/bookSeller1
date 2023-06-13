package com.PK.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String user;

    private String products;

    private double total;

    private LocalDateTime orderDate;

    // konstruktory, gettery, settery

    // ...
}

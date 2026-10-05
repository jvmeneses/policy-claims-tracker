package com.example.claims.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Policyholder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String fullName;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
}

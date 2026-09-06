package com.SecurityApp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "posts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @PrePersist
    public void beforeSave() {
    }

    @PreUpdate
    public void beforeUpdate() {
    }

    @PreRemove
    public void beforeDelete() {
    }
}

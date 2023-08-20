package com.example.moviesandtvseriesrestapi.models;

import jakarta.persistence.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotEmpty
    @Column
    private String name;

    @NotNull
    @Min(0)
    @Column
    private Integer availableContent;

    @NotNull
    @Min(0)
    @Column
    private Double price;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;

    public Category() {}

    public Category(Long id, String name, Integer availableContent, Double price) {
        this.id = id;
        this.name = name;
        this.availableContent = availableContent;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAvailableContent() {
        return availableContent;
    }

    public void setAvailableContent(Integer availableContent) {
        this.availableContent = availableContent;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(List<Subscription> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", availableContent=" + availableContent +
                ", price=" + price +
                '}';
    }
}

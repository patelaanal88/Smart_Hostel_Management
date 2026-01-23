package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "mess_menu")
@Data
public class MessMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;        // Monday, Tuesday, etc.
    private String mealType;   // Breakfast, Lunch, Dinner
    private String itemName;   // e.g., Thepla, Roti, Pani Puri

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
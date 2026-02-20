package com.hostel.smart_hostel.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate eventDate;
    private String category;

    @ElementCollection
    @CollectionTable(name = "event_images", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "image_data", columnDefinition = "LONGTEXT")
    private List<String> images = new ArrayList<>();

    private int registrationCount = 0;
    private boolean archived = false;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public int getRegistrationCount() { return registrationCount; }
    public void setRegistrationCount(int registrationCount) { this.registrationCount = registrationCount; }
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
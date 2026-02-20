package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByArchivedFalse();
    List<Event> findByArchivedTrue();
}
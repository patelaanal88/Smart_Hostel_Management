package com.hostel.smart_hostel.repository;

import com.hostel.smart_hostel.model.MessMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessMenuRepository extends JpaRepository<MessMenu, Long> {
}
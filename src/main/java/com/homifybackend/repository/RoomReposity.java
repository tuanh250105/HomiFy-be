package com.homifybackend.repository;

import com.homifybackend.model.Property;
import com.homifybackend.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomReposity extends JpaRepository<Room, Long> {
    List<Room> findByProperty_PropertyId(Long propertyId);
}

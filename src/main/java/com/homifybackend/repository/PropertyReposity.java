package com.homifybackend.repository;

import com.homifybackend.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PropertyReposity extends JpaRepository<Property, Long> {


}

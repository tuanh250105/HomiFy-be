package com.homifybackend.manageRentals.controller;

import com.homifybackend.manageRentals.dto.RentalPropertyDTO;
import com.homifybackend.manageRentals.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rental")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class RentalController {

  @Autowired
  private RentalService rentalService;

  @GetMapping("/properties")
  public List<RentalPropertyDTO> getAllProperties() {
    return rentalService.getAllProperties();
  }

  @GetMapping("/properties/{id}")
  public ResponseEntity<RentalPropertyDTO> getPropertyById(@PathVariable Long id) {
    RentalPropertyDTO dto = rentalService.getPropertyById(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @PostMapping("/properties")
  public RentalPropertyDTO addProperty(@RequestBody RentalPropertyDTO dto) {
    return rentalService.addProperty(dto);
  }

  @PutMapping("/properties/{id}")
  public ResponseEntity<RentalPropertyDTO> updateProperty(@PathVariable Long id, @RequestBody RentalPropertyDTO dto) {
    RentalPropertyDTO updated = rentalService.updateProperty(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @PostMapping("/properties/{id}/deactivate")
  public ResponseEntity<Void> deactivateProperty(@PathVariable Long id) {
    return rentalService.deactivateProperty(id) ? ResponseEntity.ok().build()
        : ResponseEntity.badRequest().build();
  }

  @PostMapping("/properties/{id}/activate")
  public ResponseEntity<Void> activateProperty(@PathVariable Long id) {
    return rentalService.activateProperty(id) ? ResponseEntity.ok().build()
        : ResponseEntity.badRequest().build();
  }

  @DeleteMapping("/properties/{id}")
  public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
    return rentalService.deleteProperty(id) ? ResponseEntity.noContent().build()
        : ResponseEntity.notFound().build();
  }
}
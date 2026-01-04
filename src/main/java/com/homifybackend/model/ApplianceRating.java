package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "appliance_ratings")
@Getter
@Setter // BẮT BUỘC PHẢI CÓ để gán dữ liệu
@NoArgsConstructor // BẮT BUỘC CHO JPA
@AllArgsConstructor
public class ApplianceRating {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "rating_id")
  private Long ratingId;

  // Khi new ApplianceRating(), giá trị này tự động là false
  @Column(name = "dishwasher")
  private Boolean dishwasher = false;

  @Column(name = "dryer")
  private Boolean dryer = false;

  @Column(name = "microwave")
  private Boolean microwave = false;

  @Column(name = "oven")
  private Boolean oven = false;

  @Column(name = "refrigerator")
  private Boolean refrigerator = false;

  @Column(name = "washer")
  private Boolean washer = false;
}
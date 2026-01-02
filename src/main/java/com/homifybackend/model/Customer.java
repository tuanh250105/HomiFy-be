package com.homifybackend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
// Chỉ dùng 1 annotation này để tạo constructor rỗng duy nhất
@NoArgsConstructor
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {
  // Để trống hoàn toàn phần thân
}
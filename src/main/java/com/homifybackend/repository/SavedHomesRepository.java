package com.homifybackend.repository;

import com.homifybackend.model.CustomerFavorite;
import com.homifybackend.model.CustomerFavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SavedHomesRepository extends JpaRepository<CustomerFavorite, CustomerFavoriteId> {

    // ===== LIST saved homes =====
    @Query(value = """
    SELECT
      cf.property_id AS id,                 -- đây là sell_request_id (đang dùng làm key remove)
      sl.id AS listingId,                   -- sale_listings.id
      'BUY' AS listingType,
      a.street AS title,
      CONCAT(a.street, ', ', a.city, ', ', a.province) AS address,
      sl.current_price AS price,
      COALESCE(sl.sale_status, sr.status) AS status,
      COALESCE(p.beds, sr.est_beds) AS beds,
      COALESCE(p.baths, sr.est_baths) AS baths,
      COALESCE(p.area, sr.estimated_area) AS sqft,
      NULL AS img,
      'For sale' AS type,
      cf.date_added AS addedAt
    FROM customer_favorites cf
    JOIN sell_requests sr ON sr.id = cf.property_id
    JOIN addresses a ON a.address_id = sr.address_id
    LEFT JOIN properties p ON p.address_id = sr.address_id
    LEFT JOIN sale_listings sl ON sl.property_id = p.property_id
    WHERE cf.customer_id = :customerId
    ORDER BY cf.date_added DESC
  """, nativeQuery = true)
    List<SavedHomeCardRow> findSavedHomes(@Param("customerId") Long customerId);


    // ===== DELETE favorite =====
    @Modifying
    @Transactional
    @Query(value = """
        DELETE FROM customer_favorites
        WHERE customer_id = :customerId AND property_id = :propertyId
      """, nativeQuery = true)
    int deleteFavorite(@Param("customerId") Long customerId,
                       @Param("propertyId") Long propertyId);

    // projection để map native query (không cần tạo thêm file khác)
    interface SavedHomeCardRow {
        Long getId();
        Long getListingId();
        String getListingType();
        String getTitle();
        String getAddress();
        BigDecimal getPrice();
        String getStatus();
        Integer getBeds();
        Integer getBaths();
        Double getSqft();
        String getImg();
        String getType();
        LocalDateTime getAddedAt();
    }
}

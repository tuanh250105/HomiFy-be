package com.homifybackend.repository;

import com.homifybackend.dto.CustomerTourItemDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomerToursRepository {

    private final JdbcTemplate jdbc;

    public CustomerToursRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CustomerTourItemDTO> findAllByCustomer(long customerId) {
        // NOTE:
        // - tour_requests.requester_id: customer(user_id)
        // - tour_requests.sale_listing_id -> sale_listings.id
        // - sale_listings.agent_id -> agents.user_id (v3 script)
        // - users.user_id dùng để lấy full_name (agent name)
        // - properties/address: tuỳ schema nhưng thường property_id, address_id
        String sql = """
            SELECT
              tr.id                         AS id,
              tr.sale_listing_id            AS sale_listing_id,
              tr.requested_date             AS requested_date,
              tr.time_slot                  AS time_slot,
              tr.status                     AS status,

              sl.agent_id                   AS agent_id,
              u.full_name                   AS agent_name,

              CONCAT_WS(', ', a.street, a.city, a.province) AS address_text
            FROM tour_requests tr
            JOIN sale_listings sl ON sl.id = tr.sale_listing_id
            LEFT JOIN properties p ON p.property_id = sl.property_id
            LEFT JOIN addresses a ON a.address_id = p.address_id
            LEFT JOIN users u ON u.user_id = sl.agent_id
            WHERE tr.requester_id = ?
            ORDER BY tr.requested_date DESC, tr.id DESC
        """;

        return jdbc.query(sql, (rs, rowNum) -> {
            CustomerTourItemDTO dto = new CustomerTourItemDTO();
            dto.setId(rs.getLong("id"));
            dto.setSaleListingId(rs.getLong("sale_listing_id"));

            Date d = rs.getDate("requested_date");
            dto.setRequestedDate(d != null ? d.toLocalDate() : null);

            dto.setTimeSlot(rs.getString("time_slot"));
            dto.setStatus(rs.getString("status"));

            long agentId = rs.getLong("agent_id");
            dto.setAgentId(rs.wasNull() ? null : agentId);

            dto.setAgentName(rs.getString("agent_name"));
            dto.setAddressText(rs.getString("address_text"));
            return dto;
        }, customerId);
    }

    public int cancelIfOwnedByCustomer(long tourId, long customerId) {
        String sql = """
          UPDATE tour_requests
          SET status = 'CANCELED'
          WHERE id = ? AND requester_id = ?
        """;
        return jdbc.update(sql, tourId, customerId);
    }

    public int rescheduleIfOwnedByCustomer(long tourId, long customerId, LocalDate date, String timeSlot) {
        // reschedule => về lại PENDING (UI sẽ thấy PENDING)
        String sql = """
          UPDATE tour_requests
          SET requested_date = ?, time_slot = ?, status = 'PENDING'
          WHERE id = ? AND requester_id = ?
        """;
        return jdbc.update(sql, date, timeSlot, tourId, customerId);
    }
}

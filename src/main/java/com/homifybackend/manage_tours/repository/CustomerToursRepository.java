package com.homifybackend.manage_tours.repository;

import com.homifybackend.manage_tours.dto.CustomerTourItemDTO;
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

    public List<CustomerTourItemDTO> findToursByCustomerId(long customerId) {
        String sql = """
            SELECT
              tr.id,
              tr.sale_listing_id,
              tr.requested_date,
              tr.time_slot,
              tr.status,
              sl.agent_id,
              u.full_name AS agent_name,
              (addr.street || ', ' || addr.city) AS address_text
            FROM tour_requests tr
            JOIN sale_listings sl ON sl.id = tr.sale_listing_id
            JOIN users u ON u.user_id = sl.agent_id
            JOIN properties p ON p.property_id = sl.property_id
            JOIN addresses addr ON addr.address_id = p.address_id
            WHERE tr.requester_id = ?
            ORDER BY tr.requested_date DESC NULLS LAST, tr.id DESC
        """;

        return jdbc.query(sql, (rs, i) -> {
            Long id = rs.getLong("id");
            Long saleListingId = rs.getLong("sale_listing_id");

            Date d = rs.getDate("requested_date");
            LocalDate requestedDate = (d != null) ? d.toLocalDate() : null;

            String timeSlot = rs.getString("time_slot");
            String status = rs.getString("status");
            Long agentId = rs.getLong("agent_id");
            String agentName = rs.getString("agent_name");
            String addressText = rs.getString("address_text");

            return new CustomerTourItemDTO(
                    id, saleListingId, requestedDate, timeSlot, status, agentId, agentName, addressText
            );
        }, customerId);
    }

    public int cancelIfOwnedByCustomer(long tourId, long customerId) {
        String sql = """
          UPDATE tour_requests
          SET status = 'CANCELED_BY_CUSTOMER'
          WHERE id = ? AND requester_id = ?
        """;
        return jdbc.update(sql, tourId, customerId);
    }

    public int rescheduleIfOwnedByCustomer(long tourId, long customerId, LocalDate date, String timeSlot) {
        String sql = """
          UPDATE tour_requests
          SET requested_date = ?, time_slot = ?, status = 'RESCHEDULED_PENDING'
          WHERE id = ? AND requester_id = ?
        """;
        return jdbc.update(sql, date, timeSlot, tourId, customerId);
    }
}

package com.homifybackend.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ManagePersonalInfoRepository {

    @PersistenceContext
    private EntityManager em;

    /**
     * Trả về: full_name, phone_number, avatar_url, address_id, email, city
     * Lưu ý: chỉ join users + accounts + addresses, KHÔNG đụng customers/agents.
     */
    public Object[] getProfileRow(Long userId) {
        List<?> rows = em.createNativeQuery("""
                SELECT
                    u.full_name,
                    u.phone_number,
                    u.avatar_url,
                    u.address_id,
                    a.email,
                    ad.city
                FROM users u
                LEFT JOIN accounts a ON a.user_id = u.user_id
                LEFT JOIN addresses ad ON ad.address_id = u.address_id
                WHERE u.user_id = :uid
                """)
                .setParameter("uid", userId)
                .getResultList();

        if (rows == null || rows.isEmpty()) return null;
        return (Object[]) rows.get(0);
    }

    public boolean userExists(Long userId) {
        Object v = em.createNativeQuery("SELECT COUNT(*) FROM users WHERE user_id = :uid")
                .setParameter("uid", userId)
                .getSingleResult();
        return ((Number) v).longValue() > 0;
    }

    public boolean accountExists(Long userId) {
        Object v = em.createNativeQuery("SELECT COUNT(*) FROM accounts WHERE user_id = :uid")
                .setParameter("uid", userId)
                .getSingleResult();
        return ((Number) v).longValue() > 0;
    }

    public boolean emailExistsForOtherUser(String email, Long userId) {
        Object v = em.createNativeQuery("""
                SELECT COUNT(*)
                FROM accounts
                WHERE LOWER(email) = LOWER(:email)
                  AND user_id <> :uid
                """)
                .setParameter("email", email)
                .setParameter("uid", userId)
                .getSingleResult();
        return ((Number) v).longValue() > 0;
    }

    public int updateUsers(Long userId, String fullName, String phoneNumber, String avatarUrl) {
        return em.createNativeQuery("""
                UPDATE users
                SET full_name = :fullName,
                    phone_number = :phoneNumber,
                    avatar_url = :avatarUrl
                WHERE user_id = :uid
                """)
                .setParameter("fullName", fullName)
                .setParameter("phoneNumber", phoneNumber)
                .setParameter("avatarUrl", avatarUrl)
                .setParameter("uid", userId)
                .executeUpdate();
    }

    public int updateAccountEmail(Long userId, String email) {
        return em.createNativeQuery("UPDATE accounts SET email = :email WHERE user_id = :uid")
                .setParameter("email", email)
                .setParameter("uid", userId)
                .executeUpdate();
    }

    /**
     * Nếu user chưa có address_id: tạo address mới chỉ với city, trả về address_id
     * (các cột khác của addresses nullable theo schema bạn gửi).
     */
    public Long insertAddressReturnId(String city) {
        Object id = em.createNativeQuery("""
                INSERT INTO addresses (city)
                VALUES (:city)
                RETURNING address_id
                """)
                .setParameter("city", city)
                .getSingleResult();

        return id == null ? null : ((Number) id).longValue();
    }

    public int updateAddressCity(Long addressId, String city) {
        return em.createNativeQuery("UPDATE addresses SET city = :city WHERE address_id = :aid")
                .setParameter("city", city)
                .setParameter("aid", addressId)
                .executeUpdate();
    }

    public int updateUserAddressId(Long userId, Long addressId) {
        return em.createNativeQuery("UPDATE users SET address_id = :aid WHERE user_id = :uid")
                .setParameter("aid", addressId)
                .setParameter("uid", userId)
                .executeUpdate();
    }
}

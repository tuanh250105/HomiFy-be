package com.homifybackend.config;

import com.homifybackend.model.Customer;
import com.homifybackend.model.Tour;
import com.homifybackend.repository.CustomerRepository;
import com.homifybackend.repository.TourRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CustomerRepository custRepo, TourRepository tourRepo) {
        return args -> {
            if (custRepo.count() == 0) {
                saveCustomer(custRepo, "Trần Thị Bích", "0912223334", "leads", 30, "Tìm hiểu chung cư Thủ Đức");
                saveCustomer(custRepo, "Lê Minh Cường", "0988777666", "nurturing", 50, "Biệt thự Quận 9");
                saveCustomer(custRepo, "Nguyễn Văn A", "0901234567", "viewing", 85, "Căn hộ 2 phòng ngủ Quận 2");
                saveCustomer(custRepo, "Phạm Huy Hoàng", "0933444555", "closing", 100, "Penthouse Vinhomes");
                saveCustomer(custRepo, "Đặng Thu Thảo", "0944555666", "success", 100, "Đã chốt hợp đồng");
                System.out.println(">>> Đã nạp dữ liệu!");
            }

            if (tourRepo.count() == 0) {
                List<Customer> customers = custRepo.findAll();
                if (!customers.isEmpty()) {
                    Customer nguyenVanA = findCustomer(customers, "Nguyễn Văn A");
                    Customer tranThiBich = findCustomer(customers, "Trần Thị Bích");
                    Customer leMinhCuong = findCustomer(customers, "Lê Minh Cường");
                    Customer phamHuyHoang = findCustomer(customers, "Phạm Huy Hoàng");
                    Customer dangThuThao = findCustomer(customers, "Đặng Thu Thảo");

                    saveTour(tourRepo, "2025-12-31", "10:00 AM", "CONFIRMED", nguyenVanA, "Căn hộ Quận 2 - Block A");
                    saveTour(tourRepo, "2025-12-31", "10:00 AM", "PENDING", tranThiBich, "Chung cư mini Thủ Đức");
                    saveTour(tourRepo, "2025-12-29", "09:00 AM", "CANCELLED", leMinhCuong, "Nhà phố liên kế Quận 9");
                    saveTour(tourRepo, "2025-12-31", "03:30 PM", "CONFIRMED", phamHuyHoang, "Penthouse Landmark 81");
                    saveTour(tourRepo, "2026-01-04", "11:00 AM", "PENDING", dangThuThao, "Biệt thự Valora Island");
                    System.out.println(">>> Đã nạp thành công!");
                }
            }
        };
    }

    private void saveCustomer(CustomerRepository repo, String name, String phone, String status, int score, String demand) {
        Customer c = new Customer();
        c.setFullName(name);
        c.setPhoneNumber(phone);
        c.setRole("CUSTOMER");
        c.setPipelineStatus(status);
        c.setInterestScore(score);
        c.setDemand(demand);
        repo.save(c);
    }

    private void saveTour(TourRepository repo, String date, String time, String status, Customer buyer, String property) {
        Tour t = new Tour();
        t.setDate(date);
        t.setTime(time);
        t.setStatus(status);
        t.setRequester(buyer);
        t.setBuyer(buyer.getFullName());
        t.setProperty(property);

        repo.save(t);
    }

    private Customer findCustomer(List<Customer> list, String name) {
        return list.stream()
                .filter(c -> c.getFullName().equals(name))
                .findFirst().orElse(list.get(0));
    }
}
package com.homifybackend.manageRentalPayments.controller;

import com.homifybackend.manageRentalPayments.dto.RentalPaymentScheduleDTO;
import com.homifybackend.manageRentalPayments.service.RentalPaymentScheduleService;
import com.homifybackend.model.BankTransaction;
import com.homifybackend.manageRentalPayments.repository.BankTransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class PaymentController {

  @Autowired
  private RentalPaymentScheduleService service;

  @Autowired
  private BankTransactionRepository bankTxnRepo;

  @GetMapping("/schedules")
  public List<RentalPaymentScheduleDTO> getSchedules() {
    return service.getAllSchedules();
  }

  @GetMapping("/bank-transactions")
  public List<BankTransaction> getBankTransactions() {
    return bankTxnRepo.findAll();
  }

  @PostMapping("/import-bank-statement")
  public ResponseEntity<Map<String, Object>> importStatement(@RequestParam("file") MultipartFile file) {
    Map<String, Object> response = new HashMap<>();
    try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
         CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

      List<BankTransaction> txns = new ArrayList<>();
      for (CSVRecord record : parser) {
        BankTransaction txn = new BankTransaction();
        txn.setTxnDate(LocalDate.parse(record.get("txn_date")));
        txn.setAmount(new BigDecimal(record.get("amount")));
        txn.setDirection(record.get("direction"));
        txn.setDescription(record.get("description"));
        txn.setReference(record.get("reference"));
        txns.add(txn);
      }
      bankTxnRepo.saveAll(txns);

      response.put("success", true);
      response.put("importedCount", txns.size());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/match")
  public ResponseEntity<Map<String, Object>> match(@RequestBody Map<String, Long> body) {
    Long scheduleId = body.get("scheduleId");
    Long txnId = body.get("txnId");
    Map<String, Object> response = new HashMap<>();

    try {
      service.matchTransaction(scheduleId, txnId);
      response.put("success", true);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  @PostMapping("/unmatch")
  public ResponseEntity<Map<String, Object>> unmatch(@RequestBody Map<String, Long> body) {
    Long scheduleId = body.get("scheduleId");
    Map<String, Object> response = new HashMap<>();

    try {
      service.unmatchTransaction(scheduleId);
      response.put("success", true);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  // ✅ PATCH note + trả về dto mới
  @PatchMapping("/schedules/{id}")
  public ResponseEntity<Map<String, Object>> updateSchedule(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> body) {
    String note = (String) body.get("note");
    Map<String, Object> response = new HashMap<>();
    try {
      RentalPaymentScheduleDTO updated = service.updateSchedule(id, note);
      response.put("success", true);
      response.put("data", updated);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }

  // New endpoint: Mark as paid
  @PostMapping("/mark-paid")
  public ResponseEntity<Map<String, Object>> markPaid(@RequestBody Map<String, Long> body) {
    Long scheduleId = body.get("scheduleId");
    Map<String, Object> response = new HashMap<>();

    try {
      service.markAsPaid(scheduleId);
      response.put("success", true);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      response.put("success", false);
      response.put("error", e.getMessage());
      return ResponseEntity.badRequest().body(response);
    }
  }
}
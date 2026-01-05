package com.homifybackend.manageRentalPayments.service;

import com.homifybackend.model.RentalPaymentScheduleStatus;
import com.homifybackend.manageRentalPayments.dto.RentalPaymentScheduleDTO;
import com.homifybackend.manageRentalPayments.mapper.RentalPaymentScheduleMapper;
import com.homifybackend.manageRentalPayments.repository.BankTransactionRepository;
import com.homifybackend.manageRentalPayments.repository.RentalPaymentScheduleRepository;
import com.homifybackend.model.BankTransaction;
import com.homifybackend.model.RentalContract;
import com.homifybackend.model.RentalPaymentSchedule;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RentalPaymentScheduleService {

  @Autowired
  private RentalPaymentScheduleRepository scheduleRepository;

  @Autowired
  private BankTransactionRepository bankTransactionRepository;

  @Autowired
  private RentalPaymentScheduleMapper mapper;

  @Transactional
  public void generateSchedulesForContract(RentalContract contract) {
    List<RentalPaymentSchedule> schedules = new ArrayList<>();

    LocalDate current = contract.getStartDate().withDayOfMonth(1);
    LocalDate end = contract.getEndDate();

    while (!current.isAfter(end)) {
      int dueDay = Math.min(contract.getPaymentDueDay(), current.lengthOfMonth());
      LocalDate dueDate = current.withDayOfMonth(dueDay);

      RentalPaymentSchedule schedule = new RentalPaymentSchedule();
      schedule.setRentalContractId(contract.getId());
      schedule.setPeriodMonth(current);
      schedule.setDueDate(dueDate);
      schedule.setAmountDue(contract.getMonthlyRent());
      schedule.setStatus(RentalPaymentScheduleStatus.PENDING);

      schedules.add(schedule);
      current = current.plusMonths(1);
    }

    scheduleRepository.saveAll(schedules);
  }

  @Transactional
  public List<RentalPaymentScheduleDTO> getAllSchedules() {
    List<RentalPaymentSchedule> entities = scheduleRepository.findAllWithDetails();

    LocalDate today = LocalDate.now();
    List<RentalPaymentSchedule> needUpdate = new ArrayList<>();
    List<RentalPaymentScheduleDTO> dtos = new ArrayList<>();

    for (RentalPaymentSchedule entity : entities) {
      if (entity.getStatus() == RentalPaymentScheduleStatus.PENDING && entity.getDueDate().isBefore(today)) {
        entity.setStatus(RentalPaymentScheduleStatus.OVERDUE);
        needUpdate.add(entity);
      }
      dtos.add(mapper.toDTO(entity));
    }

    if (!needUpdate.isEmpty()) scheduleRepository.saveAll(needUpdate);
    return dtos;
  }

  @Transactional
  public void matchTransaction(Long scheduleId, Long txnId) {
    Optional<RentalPaymentSchedule> optionalSchedule = scheduleRepository.findById(scheduleId);
    Optional<BankTransaction> optionalTxn = bankTransactionRepository.findById(txnId);

    if (optionalSchedule.isPresent() && optionalTxn.isPresent()) {
      RentalPaymentSchedule schedule = optionalSchedule.get();
      BankTransaction txn = optionalTxn.get();

      if (!schedule.getAmountDue().equals(txn.getAmount())) {
        throw new IllegalArgumentException("Số tiền không khớp");
      }

      schedule.setMatchedTxnId(txnId);
      schedule.setMatchedAt(LocalDateTime.now());
      schedule.setStatus(RentalPaymentScheduleStatus.PAID);

      scheduleRepository.save(schedule);
    } else {
      throw new IllegalArgumentException("Không tìm thấy schedule hoặc transaction");
    }
  }

  @Transactional
  public void unmatchTransaction(Long scheduleId) {
    RentalPaymentSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy schedule"));

    LocalDate today = LocalDate.now();
    RentalPaymentScheduleStatus newStatus =
        schedule.getDueDate().isBefore(today) ? RentalPaymentScheduleStatus.OVERDUE : RentalPaymentScheduleStatus.PENDING;
    schedule.setMatchedTxnId(null);
    schedule.setMatchedAt(null);
    schedule.setStatus(newStatus);
    scheduleRepository.save(schedule);
  }

  @Transactional
  public RentalPaymentScheduleDTO updateSchedule(Long scheduleId, String note) {
    RentalPaymentSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy schedule"));

    schedule.setNote(note);
    scheduleRepository.save(schedule);

    return mapper.toDTO(schedule);
  }

  @Transactional
  public void markAsPaid(Long scheduleId) {
    RentalPaymentSchedule schedule = scheduleRepository.findById(scheduleId)
        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy schedule"));

    if (schedule.getStatus() == RentalPaymentScheduleStatus.PAID) {
      throw new IllegalArgumentException("Schedule đã được thanh toán");
    }
    schedule.setStatus(RentalPaymentScheduleStatus.PAID);

    schedule.setMatchedAt(schedule.getDueDate().atStartOfDay());

    scheduleRepository.save(schedule);
  }
}
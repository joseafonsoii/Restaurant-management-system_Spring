package ao.jose.restaurant_management_system.controller;

import ao.jose.restaurant_management_system.dto.request.PaymentRequestDTO;
import ao.jose.restaurant_management_system.dto.response.PaymentResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.PaymentStatisticsDTO;
import jakarta.validation.Valid;
import ao.jose.restaurant_management_system.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponseDTO>> getAllPayments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Long orderId) {

        List<PaymentResponseDTO> payments;

        if (status != null) {
            payments = paymentService.getPaymentsByStatus(status);
        } else if (method != null) {
            payments = paymentService.getPaymentsByMethod(method);
        } else if (orderId != null) {
            payments = paymentService.getPaymentsByOrder(orderId);
        } else {
            payments = paymentService.getAllPayments();
        }

        return ResponseEntity.ok(payments);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<PaymentResponseDTO>> getPaymentsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<PaymentResponseDTO> payments = paymentService.getPaymentsByDateRange(startDate, endDate);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/customer/{customerName}")
    public ResponseEntity<List<PaymentResponseDTO>> getCompletedPaymentsByCustomer(
            @PathVariable String customerName) {

        List<PaymentResponseDTO> payments = paymentService.getCompletedPaymentsByCustomer(customerName);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        PaymentResponseDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        PaymentResponseDTO createdPayment = paymentService.createPayment(paymentRequestDTO);
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponseDTO> processPayment(@PathVariable Long id) {
        PaymentResponseDTO processedPayment = paymentService.processPayment(id);
        return ResponseEntity.ok(processedPayment);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDTO> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        PaymentResponseDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponseDTO> refundPayment(@PathVariable Long id) {
        PaymentResponseDTO refundedPayment = paymentService.refundPayment(id);
        return ResponseEntity.ok(refundedPayment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<PaymentStatisticsDTO> getPaymentStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        PaymentStatisticsDTO statistics;

        if (startDate != null && endDate != null) {
            statistics = paymentService.getPaymentStatisticsByDateRange(startDate, endDate);
        } else {
            statistics = paymentService.getPaymentStatistics();
        }

        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/revenue/total")
    public ResponseEntity<BigDecimal> getTotalRevenue() {
        BigDecimal revenue = paymentService.getTotalRevenue();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/revenue/range")
    public ResponseEntity<BigDecimal> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        BigDecimal revenue = paymentService.getRevenueByDateRange(startDate, endDate);
        return ResponseEntity.ok(revenue);
    }
}
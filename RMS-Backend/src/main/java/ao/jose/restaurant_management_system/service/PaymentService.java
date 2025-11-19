package ao.jose.restaurant_management_system.service;

import ao.jose.restaurant_management_system.dto.OrderSummaryDTO;
import ao.jose.restaurant_management_system.dto.request.PaymentRequestDTO;
import ao.jose.restaurant_management_system.dto.response.PaymentResponseDTO;
import ao.jose.restaurant_management_system.dto.statistics.PaymentStatisticsDTO;
import ao.jose.restaurant_management_system.model.*;
import ao.jose.restaurant_management_system.model.enums.OrderStatus;
import ao.jose.restaurant_management_system.model.enums.PaymentMethod;
import ao.jose.restaurant_management_system.model.enums.PaymentStatus;
import ao.jose.restaurant_management_system.repository.PaymentRepository;
import ao.jose.restaurant_management_system.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService{

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        log.info("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return mapToDTO(payment);
    }


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByOrder(Long orderId) {
        log.info("Fetching payments for order id: {}", orderId);
        return paymentRepository.findByOrderId(orderId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByStatus(String status) {
        log.info("Fetching payments with status: {}", status);
        try {
            PaymentStatus paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
            return paymentRepository.findByStatus(paymentStatus)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment status: " + status);
        }
    }


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByMethod(String method) {
        log.info("Fetching payments with method: {}", method);
        try {
            PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
            return paymentRepository.findByMethod(paymentMethod)
                    .stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment method: " + method);
        }
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching payments between {} and {}", startDate, endDate);
        return paymentRepository.findByCreatedAtBetween(startDate, endDate)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getCompletedPaymentsByCustomer(String customerName) {
        log.info("Fetching completed payments for customer: {}", customerName);
        return paymentRepository.findCompletedPaymentsByCustomerName(customerName)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO paymentRequestDTO) {
        log.info("Creating new payment for order id: {}", paymentRequestDTO.getOrderId());

        // Find order
        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + paymentRequestDTO.getOrderId()));

        // Validate payment amount
        if (paymentRequestDTO.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Payment amount must be greater than 0");
        }

        // Check if order is already paid
        if (order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("Order is already paid");
        }

        // Check if order is cancelled
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot process payment for cancelled order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .amount(paymentRequestDTO.getAmount())
                .method(paymentRequestDTO.getMethod())
                .status(paymentRequestDTO.getStatus())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with id: {}", savedPayment.getId());

        return mapToDTO(savedPayment);
    }


    @Transactional
    public PaymentResponseDTO processPayment(Long id) {
        log.info("Processing payment with id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment can only be processed from PENDING status");
        }

        // Simular processamento de pagamento
        // Em um sistema real, aqui você integraria com gateway de pagamento
        try {
            // Simular processamento bem-sucedido
            payment.processPayment();

            Payment processedPayment = paymentRepository.save(payment);
            log.info("Payment processed successfully for id: {}", id);

            return mapToDTO(processedPayment);

        } catch (Exception e) {
            // Em caso de falha no processamento
            payment.setStatus(PaymentStatus.FAILED);
            Payment failedPayment = paymentRepository.save(payment);
            log.error("Payment processing failed for id: {}", id, e);

            throw new RuntimeException("Payment processing failed: " + e.getMessage());
        }
    }


    @Transactional
    public PaymentResponseDTO updatePaymentStatus(Long id, String status) {
        log.info("Updating payment status for id: {} to {}", id, status);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        try {
            PaymentStatus newStatus = PaymentStatus.valueOf(status.toUpperCase());
            payment.setStatus(newStatus);

            // Se o pagamento for concluído, atualizar o status do pedido
            if (newStatus == PaymentStatus.COMPLETED && payment.getOrder() != null) {
                payment.getOrder().setStatus(OrderStatus.PAID);
            }

            Payment updatedPayment = paymentRepository.save(payment);
            log.info("Payment status updated successfully for id: {}", id);

            return mapToDTO(updatedPayment);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid payment status: " + status);
        }
    }


    @Transactional
    public PaymentResponseDTO refundPayment(Long id) {
        log.info("Processing refund for payment id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }

        payment.refundPayment();
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Payment refunded successfully for id: {}", id);
        return mapToDTO(refundedPayment);
    }


    @Transactional
    public void deletePayment(Long id) {
        log.info("Deleting payment with id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));

        paymentRepository.delete(payment);
        log.info("Payment deleted successfully with id: {}", id);
    }


    @Transactional(readOnly = true)
    public PaymentStatisticsDTO getPaymentStatistics() {
        log.info("Fetching payment statistics");

        BigDecimal totalRevenue = paymentRepository.getTotalRevenue() != null ?
                paymentRepository.getTotalRevenue() : BigDecimal.ZERO;

        BigDecimal todayRevenue = paymentRepository.getRevenueSince(
                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)) != null ?
                paymentRepository.getRevenueSince(LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)) : BigDecimal.ZERO;

        BigDecimal monthlyRevenue = paymentRepository.getRevenueSince(
                LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) != null ?
                paymentRepository.getRevenueSince(LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0)) : BigDecimal.ZERO;

        Long totalTransactions = paymentRepository.countSuccessfulTransactions() != null ?
                paymentRepository.countSuccessfulTransactions() : 0L;

        Long failedTransactions = paymentRepository.countFailedTransactions() != null ?
                paymentRepository.countFailedTransactions() : 0L;

        // Revenue by payment method
        Map<PaymentMethod, BigDecimal> revenueByMethod = paymentRepository.getRevenueByPaymentMethod()
                .stream()
                .collect(Collectors.toMap(
                        obj -> (PaymentMethod) obj[0],
                        obj -> (BigDecimal) obj[1]
                ));

        // Transactions count by payment method
        Map<PaymentMethod, Long> transactionsByMethod = paymentRepository.getTransactionCountByPaymentMethod()
                .stream()
                .collect(Collectors.toMap(
                        obj -> (PaymentMethod) obj[0],
                        obj -> (Long) obj[1]
                ));

        return PaymentStatisticsDTO.builder()
                .totalRevenue(totalRevenue)
                .todayRevenue(todayRevenue)
                .monthlyRevenue(monthlyRevenue)
                .totalTransactions(totalTransactions)
                .successfulTransactions(totalTransactions)
                .failedTransactions(failedTransactions)
                .revenueByMethod(revenueByMethod)
                .transactionsByMethod(transactionsByMethod)
                .build();
    }


    @Transactional(readOnly = true)
    public PaymentStatisticsDTO getPaymentStatisticsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching payment statistics between {} and {}", startDate, endDate);

        List<Payment> paymentsInRange = paymentRepository.findByCreatedAtBetween(startDate, endDate);

        BigDecimal revenue = paymentsInRange.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long successfulTransactions = paymentsInRange.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .count();

        long failedTransactions = paymentsInRange.stream()
                .filter(p -> p.getStatus() == PaymentStatus.FAILED)
                .count();

        Map<PaymentMethod, BigDecimal> revenueByMethod = paymentsInRange.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        Payment::getMethod,
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));

        Map<PaymentMethod, Long> transactionsByMethod = paymentsInRange.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .collect(Collectors.groupingBy(
                        Payment::getMethod,
                        Collectors.counting()
                ));

        return PaymentStatisticsDTO.builder()
                .totalRevenue(revenue)
                .todayRevenue(revenue) // Para este caso específico
                .monthlyRevenue(revenue) // Para este caso específico
                .totalTransactions((long) paymentsInRange.size())
                .successfulTransactions(successfulTransactions)
                .failedTransactions(failedTransactions)
                .revenueByMethod(revenueByMethod)
                .transactionsByMethod(transactionsByMethod)
                .build();
    }


    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = paymentRepository.getTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }


    @Transactional(readOnly = true)
    public BigDecimal getRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> payments = paymentRepository.findByCreatedAtBetween(startDate, endDate);
        return payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.COMPLETED)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PaymentResponseDTO mapToDTO(Payment payment) {
        OrderSummaryDTO orderSummary = OrderSummaryDTO.builder()
                .orderId(payment.getOrder().getId())
                .customerName(payment.getOrder().getCustomerName())
                .tableNumber(payment.getOrder().getTable().getTableNumber())
                .orderTotal(payment.getOrder().getTotalAmount())
                .orderStatus(payment.getOrder().getStatus())
                .build();

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .orderSummary(orderSummary)
                .build();
    }
}
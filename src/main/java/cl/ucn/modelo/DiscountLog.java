package cl.ucn.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "logs")
public class DiscountLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private String customerId;

    private LocalDateTime timestamp;

    @Column(name = "discount_applied")
    private double discountApplied;

    public DiscountLog() {}

    public DiscountLog(String customerId, double discountApplied) {
        this.customerId = customerId;
        this.discountApplied = discountApplied;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public double getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }
}

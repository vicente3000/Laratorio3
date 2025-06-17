
package cl.ucn.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    private String id;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(name = "total_orders")
    private int totalOrders;

    @Enumerated(EnumType.STRING)
    private LoyaltyLevel level;

    @Column(name = "has_active_promo")
    private boolean hasActivePromo;

    public enum LoyaltyLevel {
        BASIC, SILVER, GOLD, PLATINUM
    }

    public Customer() {} // constructor requerido por JPA

    public Customer(String id, LocalDate joinDate, int totalOrders, LoyaltyLevel level, boolean hasActivePromo) {
        this.id = id;
        this.joinDate = joinDate;
        this.totalOrders = totalOrders;
        this.level = level;
        this.hasActivePromo = hasActivePromo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public LoyaltyLevel getLevel() {
        return level;
    }

    public void setLevel(LoyaltyLevel level) {
        this.level = level;
    }

    public boolean isHasActivePromo() {
        return hasActivePromo;
    }

    public void setHasActivePromo(boolean hasActivePromo) {
        this.hasActivePromo = hasActivePromo;
    }
}

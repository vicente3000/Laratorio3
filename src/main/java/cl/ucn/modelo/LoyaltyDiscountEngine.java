package cl.ucn.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class LoyaltyDiscountEngine {

    private final EntityManager em;

    public LoyaltyDiscountEngine(EntityManager em) {
        this.em = em;
    }

    public double computeDiscount(Customer customer) {
        if (customer == null || customer.getId() == null || customer.getJoinDate() == null) {
            throw new IllegalArgumentException("Invalid customer");
        }

        long years = ChronoUnit.YEARS.between(customer.getJoinDate(), LocalDate.now());
        double base = switch (customer.getLevel()) {
            case BASIC -> 0;
            case SILVER -> 0.05;
            case GOLD -> 0.10;
            case PLATINUM -> 0.15;
            default -> 0;
        };

        if (years > 5) base += 0.05;
        if (customer.getTotalOrders() > 100) base += 0.05;
        if (customer.isHasActivePromo()) base += 0.10;

        if (base > 0.30) base = 0.30;

        logDiscount(customer.getId(), base);
        return base;
    }

    public double computeDiscountById(String customerId) {
        Customer customer = em.find(Customer.class, customerId);
        return computeDiscount(customer);
    }

    private void logDiscount(String customerId, double discount) {
        em.getTransaction().begin();
        em.persist(new DiscountLog(customerId, discount));
        em.getTransaction().commit();
    }

    public long countLogsForCustomer(String customerId) {
        return em.createQuery(
                        "SELECT COUNT(l) FROM DiscountLog l WHERE l.customerId = :cid", Long.class)
                .setParameter("cid", customerId)
                .getSingleResult();
    }
}

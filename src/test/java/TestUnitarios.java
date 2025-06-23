import cl.ucn.modelo.Customer;
import cl.ucn.modelo.DiscountLog;
import cl.ucn.modelo.LoyaltyDiscountEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class TestUnitarios {

    private static EntityManagerFactory emf ;
    private static LoyaltyDiscountEngine engine;
    private static EntityManager em;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("shopping");
        em = emf.createEntityManager();
        engine = new LoyaltyDiscountEngine(em);
    }
    //1.Cliente básico, sin pedidos, sin antigüedad, sin promo. Esperado: 0.0
    @Test
    public void testCrearClienteBasico() {
        Customer c = new Customer("1", LocalDate.parse("2025-12-20"), 0, Customer.LoyaltyLevel.BASIC, false);
        assertEquals(0.0, engine.computeDiscount(c),0.0);
    }

    //2.Cliente con nivel SILVER, 3 años, 50 órdenes. Esperado: 0.05
    @Test
    public void testCrearClienteSilver() {
        Customer c = new Customer("2", LocalDate.parse("2025-12-20"), 50, Customer.LoyaltyLevel.SILVER, false);
        assertEquals(0.05, engine.computeDiscount(c),0.0);
    }

    //3.Cliente GOLD con 6 años, 10 órdenes. Esperado: 0.15
    @Test
    public void testCrearClienteGold_5aniguedad(){
        Customer c = new Customer("3", LocalDate.parse("2019-01-20"), 10, Customer.LoyaltyLevel.GOLD, false);
        assertEquals(0.15, engine.computeDiscount(c),0.00001);
    }

    //4.Cliente GOLD con 101 órdenes. Esperado: 0.15
    @Test
    public void testClienteGold_101ordenes(){
        Customer c = new Customer("4", LocalDate.parse("2025-01-20"), 101, Customer.LoyaltyLevel.GOLD, false);
        assertEquals(0.15, engine.computeDiscount(c),0.00001);
    }

    //5.Cliente BASIC con promoción activa. Esperado: 0.10
    @Test
    public void testClienteBasicoPromoActiva(){
        Customer c = new Customer("5", LocalDate.parse("2025-12-20"), 0, Customer.LoyaltyLevel.BASIC, true);
        assertEquals(0.10, engine.computeDiscount(c),0.00001);
    }

    //6.Cliente PLATINUM con 10 años, 150 órdenes, promoción activa. Esperado: 0.30
    @Test
    public void testClientePlatinum_10anos_150ordenes_PromoActiva(){
        Customer c = new Customer("6", LocalDate.parse("2019-12-20"), 150, Customer.LoyaltyLevel.PLATINUM, true);
        assertEquals(0.30, engine.computeDiscount(c),0.00001);
    }

    //7.Verificar que se registre correctamente una operación en logs.
    @Test
    public void testLog(){
        Customer c = new Customer("7", LocalDate.parse("2025-12-20"), 0, Customer.LoyaltyLevel.BASIC, false);
        engine.computeDiscount(c);
        EntityManager em = emf.createEntityManager();
        DiscountLog log = em.createQuery("SELECT l FROM DiscountLog l WHERE l.customerId = :cid", DiscountLog.class)
                .setParameter("cid", "7")
                .getSingleResult();
        assertEquals(0.0, log.getDiscountApplied(),0.00001);
        assertEquals("7", log.getCustomerId());
        assertEquals(LocalDate.now(), log.getTimestamp().toLocalDate());
    }

    //8.Usar computeDiscountById() para calcular descuento.
    @Test
    public void testComputeDiscountById(){
        Customer c = new Customer("2", LocalDate.parse("2025-12-20"), 0, Customer.LoyaltyLevel.BASIC, false);
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
        assertEquals(0.00, engine.computeDiscountById("2"),0.00001);
    }

    //9.Llamar a computeDiscount(null) y verificar IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testComputeDiscountNull(){
        engine.computeDiscount(null);
    }

    //10.Cliente sin ID. Esperado: IllegalArgumentException
    @Test(expected = IllegalArgumentException.class)
    public void testComputeDiscountSinId(){
        Customer c = new Customer(null, LocalDate.parse("2025-12-20"), 0, Customer.LoyaltyLevel.BASIC, false);
        engine.computeDiscount(c);
    }
}

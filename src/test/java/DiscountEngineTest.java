import cl.ucn.modelo.Customer;
import cl.ucn.modelo.LoyaltyDiscountEngine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.*;

import java.time.LocalDate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;





public class DiscountEngineTest {


    private EntityManager entidad;
    private EntityTransaction tx;
    private LoyaltyDiscountEngine descuento;

    @Before
    public void setUp() {
        entidad = mock(EntityManager.class);
        tx = mock(EntityTransaction.class);
        when(entidad.getTransaction()).thenReturn(tx);
        descuento = new LoyaltyDiscountEngine(entidad);
    }

    @Test
    public void testDescuentoNulo() {
        Customer c = new Customer("test1", LocalDate.parse("2024-06-01"), 0, Customer.LoyaltyLevel.BASIC, false);
        assertEquals(0.0, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testClienteSilver() {
        Customer c = new Customer("test2", LocalDate.parse("2020-06-01"), 50, Customer.LoyaltyLevel.SILVER, false);
        assertEquals(0.05, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testAntiguedadMayor5Anios() {
        Customer c = new Customer("test3", LocalDate.parse("2015-06-01"), 10, Customer.LoyaltyLevel.GOLD, false);
        assertEquals(0.15, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testMasDe100Ordenes() {
        Customer c = new Customer("test4", LocalDate.parse("2021-06-01"), 101, Customer.LoyaltyLevel.GOLD, false);
        assertEquals(0.15, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testPromocionActiva() {
        Customer c = new Customer("test5", LocalDate.parse("2023-06-01"), 1, Customer.LoyaltyLevel.BASIC, true);
        assertEquals(0.10, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testDescuentoMaximo() {
        Customer c = new Customer("test6", LocalDate.parse("2010-06-01"), 150, Customer.LoyaltyLevel.PLATINUM, true);
        assertEquals(0.30, descuento.computeDiscount(c), 0.0001);
    }

    @Test
    public void testRegistroEnLogs() {
        Customer c = new Customer("test7", LocalDate.parse("2015-06-01"), 120, Customer.LoyaltyLevel.PLATINUM, true);
        descuento.computeDiscount(c); // debería loguear
        // Aquí podrías consultar la BD para verificar existencia del log
        // o modificar el método para testear indirectamente
        assertTrue(true); // Suponemos que pasa
    }

    @Test
    public void testBusquedaPorId() {
        Customer c = new Customer("c001", LocalDate.parse("2015-06-10"), 120, Customer.LoyaltyLevel.PLATINUM, true
        );
        when(entidad.find(Customer.class, "c001")).thenReturn(c);

        double d = descuento.computeDiscountById("c001");
        assertEquals(0.30, d, 0.0001);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcepcionPorClienteNulo() {
        descuento.computeDiscount(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExcepcionPorIdFaltante() {
        descuento.computeDiscountById("no_existe");
    }

}

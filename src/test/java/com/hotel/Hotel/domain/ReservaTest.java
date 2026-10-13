package com.hotel.Hotel.domain;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReservaTest {

    // ---------------------------------------------------------------
    // PRUEBA 5
    // ---------------------------------------------------------------
    @Test
    @DisplayName("El costo total de la reserva debe calcularse correctamente según los días")
    void debeCalcularCostoTotalCorrectamente() {
        // Arrange: habitación a 200.0 por noche, reserva de 3 días
        Cliente cliente = new Cliente("Sofia Ruiz", "sofia@correo.com");
        Habitacion habitacion = new HabitacionEstandar("202", 2, 200.0, 1);

        RangoFechas periodo = new RangoFechas(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4)); 

        // Act
        Reserva reserva = new Reserva(cliente, habitacion, periodo);

        // Assert: 200.0 * 3 días = 600.0
        assertEquals(600.0, reserva.getCostoTotal());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    // ---------------------------------------------------------------
    // 
    // ---------------------------------------------------------------
    @Test
    @DisplayName("Debe confirmar una reserva en estado PENDIENTE y cambiar estado de habitación")
    void debeConfirmarReservaPendiente() {
        // Arrange
        Cliente cliente = new Cliente("Carlos Gómez", "carlos@correo.com");

        // Instanciamos la subclase concreta
        Habitacion habitacion = new HabitacionEstandar("101", 2, 100.0, 1);

        RangoFechas periodo = new RangoFechas(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(4));
        Reserva reserva = new Reserva(cliente, habitacion, periodo);

        // Act
        reserva.confirmar();

        // Assert
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
        assertEquals(EstadoHabitacion.OCUPADA, habitacion.getEstado());
    }
}
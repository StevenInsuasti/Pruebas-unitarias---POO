package com.hotel.Hotel.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hotel.Hotel.domain.Cliente;
import com.hotel.Hotel.domain.EstadoHabitacion;
import com.hotel.Hotel.domain.EstadoReserva;
import com.hotel.Hotel.domain.Habitacion;
import com.hotel.Hotel.domain.HabitacionEstandar;
import com.hotel.Hotel.domain.RangoFechas;
import com.hotel.Hotel.domain.Reserva;
import com.hotel.Hotel.dto.response.ReservaResponse;
import com.hotel.Hotel.mapper.ReservaMapper;
import com.hotel.Hotel.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ReservaMapper reservaMapper;

    @InjectMocks
    private ReservaService reservaService;

    @Nested
    @DisplayName("Operaciones de Confirmación")
    class ConfirmacionTests {

        @Test
        @DisplayName("Debe confirmar exitosamente cuando la reserva existe y está PENDIENTE")
        void debeConfirmarReservaExitosamente() {
            // Arrange
            UUID reservaId = UUID.randomUUID();
            Cliente cliente = new Cliente("Beatriz Morales", "beatriz@empresa.com");
            Habitacion habitacion = new HabitacionEstandar("201", 2, 200.0, 1);

            RangoFechas fechas = new RangoFechas(
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(3));
            Reserva reservaExistente = new Reserva(cliente, habitacion, fechas);

            ReservaResponse responseEsperado = new ReservaResponse(
                    reservaId,
                    "Beatriz Morales",
                    "201",
                    fechas.fechaInicio(),
                    fechas.fechaFin(),
                    400.0,
                    "CONFIRMADA");

            // Stubs
            when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaExistente));
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(reservaMapper.toResponse(any(Reserva.class))).thenReturn(responseEsperado);

            // Act
            ReservaResponse resultado = reservaService.confirmar(reservaId);

            // Assert
            assertNotNull(resultado);
            assertEquals("CONFIRMADA", resultado.estado());
            assertEquals(EstadoReserva.CONFIRMADA, reservaExistente.getEstado());

            // Verificación de comportamiento (Mocks)
            verify(reservaRepository, times(1)).findById(reservaId);
            verify(reservaRepository, times(1)).save(reservaExistente);
            verify(reservaMapper, times(1)).toResponse(reservaExistente);
        }

    }

    // ---------------------------------------------------------------
    // PRUEBA 3
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("Operaciones de Cancelación")
    class CancelacionTests {

        @Test
        @DisplayName("Debe cancelar exitosamente una reserva existente y liberar la habitación")
        void debeCancelarReservaExitosamente() {
            // Arrange
            UUID reservaId = UUID.randomUUID();
            Cliente cliente = new Cliente("Marco Silva", "marco@correo.com");
            Habitacion habitacion = new HabitacionEstandar("305", 2, 150.0, 2);

            RangoFechas fechas = new RangoFechas(
                    LocalDateTime.now().plusDays(2),
                    LocalDateTime.now().plusDays(5));
            Reserva reservaExistente = new Reserva(cliente, habitacion, fechas);
            reservaExistente.confirmar(); // primero la confirmamos para que la habitación quede OCUPADA

            ReservaResponse responseEsperado = new ReservaResponse(
                    reservaId,
                    "Marco Silva",
                    "305",
                    fechas.fechaInicio(),
                    fechas.fechaFin(),
                    450.0,
                    "CANCELADA");

            // Stubs
            when(reservaRepository.findById(reservaId)).thenReturn(Optional.of(reservaExistente));
            when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(reservaMapper.toResponse(any(Reserva.class))).thenReturn(responseEsperado);

            // Act
            ReservaResponse resultado = reservaService.cancelar(reservaId);

            // Assert
            assertNotNull(resultado);
            assertEquals("CANCELADA", resultado.estado());
            assertEquals(EstadoReserva.CANCELADA, reservaExistente.getEstado());
            assertEquals(EstadoHabitacion.DISPONIBLE, habitacion.getEstado());

            // Verificación de comportamiento
            verify(reservaRepository, times(1)).findById(reservaId);
            verify(reservaRepository, times(1)).save(reservaExistente);
        }
    }

    // ---------------------------------------------------------------
    // PRUEBA 4
    // ---------------------------------------------------------------
    @Nested
    @DisplayName("Manejo de errores")
    class ErrorTests {

        @Test
        @DisplayName("Debe lanzar excepción cuando se intenta confirmar una reserva que no existe")
        void debeLanzarExcepcionSiReservaNoExiste() {
            // Arrange: un ID que no existe en el repositorio
            UUID idInexistente = UUID.randomUUID();

            // Stub: el repositorio devuelve vacío
            when(reservaRepository.findById(idInexistente)).thenReturn(Optional.empty());

            // Act + Assert: confirmamos que se lanza la excepción correcta
            assertThrows(IllegalArgumentException.class, () -> {
                reservaService.confirmar(idInexistente);
            });

            // Verificamos que el repositorio fue consultado pero nunca se guardó nada
            verify(reservaRepository, times(1)).findById(idInexistente);
            verify(reservaRepository, never()).save(any());
        }
    }
}
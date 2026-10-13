package com.hotel.Hotel.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    // ---------------------------------------------------------------
    // PRUEBA 1
    // ---------------------------------------------------------------
    @Test
    @DisplayName("El cliente debe quedar inactivo al acumular 3 penalizaciones")
    void debeQuedarInactivoAlAcumularTresPenalizaciones() {
        
        Cliente cliente = new Cliente("Ana Torres", "ana@correo.com");

        
        cliente.registrarPenalizacion();
        cliente.registrarPenalizacion();
        cliente.registrarPenalizacion();

        
        assertFalse(cliente.isActivo());
        assertFalse(cliente.puedeRealizarReservas());
        assertEquals(3, cliente.getPenalizaciones());
    }

    // ---------------------------------------------------------------
    // PRUEBA 2
    // ---------------------------------------------------------------
    @Test
    @DisplayName("El cliente debe poder reactivarse y volver a realizar reservas")
    void debeReactivarseCorrectamente() {
        
        Cliente cliente = new Cliente("Luis Perez", "luis@correo.com");
        cliente.registrarPenalizacion();
        cliente.registrarPenalizacion();
        cliente.registrarPenalizacion(); 

        
        cliente.reactivar();

        
        assertTrue(cliente.isActivo());
        assertTrue(cliente.puedeRealizarReservas());
        assertEquals(0, cliente.getPenalizaciones());
    }
}

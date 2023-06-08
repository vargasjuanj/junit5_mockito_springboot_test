package com.example.springboot_test;


import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // habilita el contexto en persistencia , base de datos en memoria, repos, etc, para hacer las pruebas con spring data jpa
public class IntegracionJpaTest {

    // -- el archivo import.sql es detectado automaticamente al iniciar alguna de las clases de prueba, se crea un bean con este archivo
    // se crean todas las tablas, cargan los datos, y cuando finaliza la prueba elimina todos los datos de la bd h2 en memoria
    // cada metodo test es independiente uno al otro por lo tanto si un metodo test modifica información de una tabla (eliminar, modificar, crear), al finazarse esa prueba en particular se realiza un rollback. Cada test comienza con los datos originales


    // por defecto cuando se inserta el bigdecimal le está agregando dos decimales .00

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void findById(){
        // ctrl + alt + v crea la variable
        Optional<Cuenta> cuenta = cuentaRepository.findById(1l);
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.get().getPersona());

    }

    @Test
    void findByPersona(){
        // ctrl + alt + v crea la variable
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.get().getPersona());
        assertEquals("1000.00", cuenta.get().getSaldo().toPlainString());

    }

    @Test
    void findByPersonaThrowException(){
        // ctrl + alt + v crea la variable
        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Rod");
        assertThrows(NoSuchElementException.class, cuenta::get);
        assertFalse(cuenta.isPresent());

    }

    @Test
    void testFindAll(){
        List<Cuenta> cuentas = cuentaRepository.findAll();
        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
    }

    @Test
    void testSave(){
        // se crea el objeto con el id pero ese objeto al hacer el find queda guardado en una sección de jpa, por eso a veces no aparece la traza de hibernate cuando se hace un findbyid, porque ese objeto ya está en el contexto de persistencia
        // entonces no realiza consulta sino que obtiene el mismo objeto con el id del contexto de persistencia de la sesión y se lo asigna, por lo tanto maneja con cache no necesita hacer otra consulta
        // Given
        // si el tipo de dato del id lo dejo en null falla
        Cuenta cuentaPepe = new Cuenta(0l,"Pepe", new BigDecimal("3500"));

        // when
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        //Cuenta cuenta = cuentaRepository.findByPersona("Pepe").get();
        //Cuenta cuenta = cuentaRepository.findById(cuentaSave.getId()).get();
        // Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3500", cuenta.getSaldo().toPlainString());
         //hacer la prueba con el id no es tan recomendable, porque capas otro metodo lo modifica o hace un rollba, etc
        // puede fallar
         //assertEquals(3, cuenta.getId());
    }

    @Test
    void testUpdate(){
        // given

        Cuenta cuentaPepe = new Cuenta(0l,"Pepe", new BigDecimal("3500"));

        // when
        Cuenta cuenta = cuentaRepository.save(cuentaPepe);

        // Then
        assertEquals("Pepe", cuenta.getPersona());
        assertEquals("3500", cuenta.getSaldo().toPlainString());

        // when
        cuenta.setSaldo(new BigDecimal("3800"));
        Cuenta cuentaActualizada = cuentaRepository.save(cuenta);

        // then
        assertEquals("Pepe", cuentaActualizada.getPersona());
        assertEquals("3800", cuentaActualizada.getSaldo().toPlainString());

    }

    @Test
    void testDelete(){
        // sería como el given
        Cuenta cuenta = cuentaRepository.findById(2l).get();
        assertEquals("Jhon", cuenta.getPersona());
        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class, () -> cuentaRepository.findByPersona("Jhon").get());
        assertThrows(NoSuchElementException.class, () -> cuentaRepository.findById(2l).get());
        assertEquals(1, cuentaRepository.findAll().size());

    }

}

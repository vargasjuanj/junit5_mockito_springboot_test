package com.example.springboot_test.repositories;


import com.example.springboot_test.models.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

// ya heredando de jpa spring lo transforma en un componente o bean, y va a estar disponible va ser inyectado (su referencia) con un autowired
// Jpa extendiende de CrudRepository, además del crud que implementa por debajo, también implementa la paginación de forma nativa
// Si el objeto viene con un id o clave primaria el metodo 'save' lo va actualizar, si viene con id null lo va a crear
// Hay 3 formas de hacer las consultas, por querymethods (convencion de nombres), con jpql escribimos la query con hibernate que trabaja con objetos, y con sql nativo
public interface CuentaRepository extends JpaRepository<Cuenta,Long> {

    // 'select c' significa que como resultado va a devolver el objeto cuenta
  // ?1 es un wilcard que después se reemplaza con el valor de persona, un se

    //Cuenta findByPersona(String persona);
        // Poner el Optional es opcional, pero con el podemos evitar y manejar el null pointer excepción
    // El optional nos permite personalizar el manejd de excepciones, con el if, o is present, hacer algo si está presente
    // tambien nos permite caracteristicas del apistream de java, por ejemplo el map, filter, flatmap

    @Query("select c from Cuenta c where c.persona = ?1")

    Optional<Cuenta> findByPersona(String persona);


    // todas estas firmas de metodos solo la vamos a simular con mockito para devolver supuestos datos de una bd
   // List<Cuenta> findAll();
    //Cuenta findById(Long id);

    // sería como el save de jpa que se utiliza para guardar y actualziar
    //void update(Cuenta cuenta);

}

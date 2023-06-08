package com.example.springboot_test.controllers;

import static com.example.springboot_test.Datos.*;

import com.example.springboot_test.dtos.TransaccionDTO;
import com.example.springboot_test.models.Cuenta;
import com.example.springboot_test.services.CuentaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.print.attribute.standard.Media;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// como es controlador hay que usar webmvctest y dentro inidicamos la clase controller
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    /* Es la implementación de moquito para pñrobar un controlador, por eso se llama mockmc
    Viene todo configurado, todo el contexto de prueba, para contexto web, ya viene
    inicializado y configurado en webmvctest


    En el mock mc se incluye es el contexto mvc, pero falso, no es el real, por lo tant
    todo lo que es el servidor http es falso, es simulado
     También es falso el request http, el response, se fija en esos objetos que son parte de este ambiente web mvc,
     todo es completamente falso

     No estamos trabajando solo un servidor real http ni tamposco sobre el http request cert left ni tampoco
     response real, todo es falso


     */

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CuentaService cuentaService;

    //transforma dtos a json y viceversa
    ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        objectMapper = new ObjectMapper();
    }

/// no se va a hacer en ningún momento la llamada real a un metodo serfvice

    // Está simulado el servidor http, no es un servidor real que llama desde el puerto 8080, al igual que el service está simulado
    // Trabaja con el controlador real, pero simula el servicio y servidor y todo lo que es el http (response y request)
    @Test
    void testDetalle() throws Exception {
        // given ( contexto de prueba)
        when(cuentaService.findById(1l)).thenReturn(crearCuenta001().get());
        // when (cuando se hace la llamada con un api servelet, sevidor simulado)

        // por defecto el contentType se envia como un aplication json
        mvc.perform(MockMvcRequestBuilders.get("/api/cuentas/1").contentType(MediaType.APPLICATION_JSON))

                //then (
                .andExpect(status().isOk()) // esperar resultado
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // prueba que el contentype de la respuesta, del response tambien sea APPLICATION_JSON, es decir lo que se guarda en el cuerpo de la respuesta del response body sea un json
                .andExpect(jsonPath("$.persona").value("Andrés")) // probar que algun atributo del json tenga cierto valor. El signo $ hace referencia a la raiz del json recibido
                .andExpect(jsonPath("$.saldo").value("1000"));
        verify(cuentaService).findById(1l);

    }

    @Test
    void testTransferir() throws Exception {
        // given
        TransaccionDTO dto = new TransaccionDTO();
        dto.setCuentaOrigenId(1l);
        dto.setCuentaDestinoId(2l);
        dto.setMonto(new BigDecimal("100"));
        dto.setBancoId(1l);
        // esto es solo para mostrar el json que arma
        System.out.println(objectMapper.writeValueAsString(dto));

        Map<String, Object> response = new HashMap<>();
        //response.put("date", LocalDate.now()); // con esto salta esta excepción: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDate` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: java.util.HashMap["date"])

        response.put("status","ok");
        response.put("mensaje","Transferencia realizada con éxito");
        response.put("transaccion", dto);

        System.out.println(objectMapper.writeValueAsString(response));

        // when
        mvc.perform(  post("/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)  // probamos el contentype, que lo que se envie sea un json
                .content(objectMapper.writeValueAsString(dto))) // el contenido que estamos enviando en el request


        // then, ahora probamos

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)
                )//.andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(dto.getCuentaOrigenId()))
                .andExpect(content().json(objectMapper.writeValueAsString(response))); // acá comparamos los dos json, el response que devuelve y el esperado

    }

    @Test
    void testListar() throws Exception {
        // given
        List<Cuenta> cuentas = Arrays.asList(crearCuenta001().get(), crearCuenta002().get());
        when(cuentaService.findAll()).thenReturn(cuentas);

        mvc.perform(get("/api/cuentas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andrés"))
                .andExpect(jsonPath("$[1].persona").value("Jhon"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2))) // probamos la cantidad de elementos
                .andExpect(content().json(objectMapper.writeValueAsString(cuentas)));

    }

    @Test
    void testGuardar() throws Exception {
        // Given
        Cuenta cuenta = new Cuenta(0l, "Pepe", new BigDecimal("3000"));


        when(cuentaService.save(any())).then(invocation ->{
            // como no vamos a tener comunicación con el repositorio no vamos a obtener el id nuevo, lo seteamos de aca, cuando el controller devuelva el dato
            Cuenta c = invocation.getArgument(0);
            c.setId(3L);
            return c;
        });

        // when
        mvc.perform(post("/api/cuentas").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuenta)))
                // Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3))) // si arriba no hubieramos usado el objeto invocación acá habría un error
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo", is(3000)));
        verify(cuentaService).save(any());

    }
}
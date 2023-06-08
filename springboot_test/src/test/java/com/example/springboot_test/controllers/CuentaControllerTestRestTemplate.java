package com.example.springboot_test.controllers;

import com.example.springboot_test.dtos.TransaccionDTO;
import com.example.springboot_test.models.Cuenta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Se debe ejecutar toda la clase a la vez, ya que los metodos son dependientes, porque se hace uso de la bd, y repos

// Para ejecutar todos los test nos paramos sobre el nombre de la carpeta principal, sería springboot_test y ponemos run all
@Tag("integration_rt") // Se usa está etiqueta, porque al lanzar dos o más clases de integración juntas, una modifica los datos de la otra, y luego la otra clase falla, y con esa etiqueta en settins del proyecto exluimos las clases de integración eligiendo Tags, y exluyendola así. !integration_wt, etc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // como es de integración, algunos metodos test que modifican la base de datos van a afectar a los demás metodos
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CuentaControllerTestRestTemplate {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort // de forma automatica nos importa el cuerpo
    private Integer puerto; // esto del puerto también es valido para testWebclient

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1) // se importa siempre de jupiter no de spring
    void testTransferir() throws JsonProcessingException {
        TransaccionDTO dto = new TransaccionDTO();
        dto.setMonto(new BigDecimal("100"));
        dto.setCuentaOrigenId(1l);
        dto.setCuentaDestinoId(2l);
        dto.setBancoId(1l);
        // el dto se convierte en json de forma automatica
        // String.class sería en este caso el tipo con el que queremos obtener la respuesta, es decir en este caso va a devolver un json de tipo string, una cadena con todo el contenido
        ResponseEntity<String> response = client
                .postForEntity(crearUri("/api/cuentas/transferir"), dto, String.class);  //ruta absholuta o completa, y además podemos saber el puerto

/*
                .postForEntity("/api/cuentas/transferir", dto, String.class); // con ruta relativa al proyecto
*/

        String json = response.getBody();
        // imprimimos el json para verlo en consola
        System.out.println(json);
        assertEquals(HttpStatus.OK, response.getStatusCode() );
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertNotNull(json);
        assertTrue(json.contains("Transferencia realizada con éxito"));
        // este mostraría en consola, esos escapes \\ son porque en la consola sale armado con doble comilla y se escapan
        //falla
        //assertTrue(json.contains("{\"transaccion\":{\"cuentaOrigenId\":1,\"cuentaDestinoId\":2,\"monto\":100,\"bancoId\":1},\"mensaje\":\"Transferencia realizada con éxito\",\"status\":\"ok\"}\n"));

        // Acá se convierte el json String en una estructura para poder navegar por los atributo y asi obtener los valor por ejemplo con el metodo astext, aslong, etc
        // mejor que usar el  metodo contains()
        JsonNode jsonNode = objectMapper.readTree(json);

        assertEquals("Transferencia realizada con éxito", jsonNode.path("mensaje").asText());
        assertEquals("100", jsonNode.path("transaccion").path("monto").asText());
        assertEquals(1l, jsonNode.path("transaccion").path("cuentaOrigenId").asLong());

        // probamos el json completo

        Map<String, Object> response2 = new HashMap<>();
        //response.put("date", LocalDate.now()); // con esto salta esta excepción: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDate` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: java.util.HashMap["date"])

        response2.put("status","ok");
        response2.put("mensaje","Transferencia realizada con éxito");
        response2.put("transaccion", dto);

        assertEquals(objectMapper.writeValueAsString(response2), json);
    }

    @Test
    @Order(2)
    void testDetalle(){
        ResponseEntity<Cuenta> respuesta = client.getForEntity(crearUri("/api/cuentas/1"), Cuenta.class);
        Cuenta cuenta = respuesta.getBody();
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertEquals("Andrés", cuenta.getPersona());
        // gracias a que están ordenados ahora vemos el descuento, antes tenia 1000 ahora 900
        assertEquals("900.00", cuenta.getSaldo().toPlainString());
    }

    @Test
    @Order(3)
    void testListar() throws JsonProcessingException {
        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        // convertimos arreglo en una lista porque es más facil trabajar con una lista
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        assertEquals(1l, cuentas.get(0).getId());
        assertEquals("Andrés", cuentas.get(0).getPersona());
        assertEquals("900.00", cuentas.get(0).getSaldo().toPlainString());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(cuentas));
        assertEquals("Andrés", json.get(0).path("persona").asText());
    }

    @Test
    @Order(4)
    void testGuardar(){
        Cuenta cuenta = new Cuenta(0l,"Pepa", new BigDecimal("3800"));
       // por defecto el haader contenttype es application.json si no se modifica
        ResponseEntity<Cuenta> respuesta = client.postForEntity(crearUri("/api/cuentas"), cuenta, Cuenta.class);
        assertEquals(HttpStatus.CREATED, respuesta.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, respuesta.getHeaders().getContentType());

        Cuenta cuentaCreada = respuesta.getBody();
        assertNotNull(cuentaCreada);
        assertEquals("Pepa",cuentaCreada.getPersona());
        assertEquals("3800", cuentaCreada.getSaldo().toPlainString());
    }

    @Test
    @Order(5)
    void testEliminar() {

        ResponseEntity<Cuenta[]> respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        List<Cuenta> cuentas = Arrays.asList(respuesta.getBody());
        assertEquals(3, cuentas.size());

        //client.delete(crearUri("/api/cuentas/3"));
        Map<String, Long> pathVariables = new HashMap<>();
        pathVariables.put("id", 3L);
        // el ultimo argumento es el path variable, exige que se lo pase en forma de map, el que dice enty es la configuracion de las cabeceras (objeto HTTPhEADERS), por defecto null, http
        ResponseEntity<Void> exchange = client.exchange(crearUri("/api/cuentas/{id}"), HttpMethod.DELETE, null, Void.class,
                pathVariables);

        assertEquals(HttpStatus.NO_CONTENT, exchange.getStatusCode());
        assertFalse(exchange.hasBody());

        respuesta = client.getForEntity(crearUri("/api/cuentas"), Cuenta[].class);
        cuentas = Arrays.asList(respuesta.getBody());
        assertEquals(2, cuentas.size());

        ResponseEntity<Cuenta> respuestaDetalle = client.getForEntity(crearUri("/api/cuentas/3"), Cuenta.class);
        assertEquals(HttpStatus.NOT_FOUND, respuestaDetalle.getStatusCode());
        assertFalse(respuestaDetalle.hasBody());
    }


    private String crearUri(String uri){
        return "http://localhost:" + puerto + uri;

    }


}
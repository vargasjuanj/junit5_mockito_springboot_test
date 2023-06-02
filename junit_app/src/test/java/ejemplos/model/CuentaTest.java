package ejemplos.model;

import ejemplos.excepion.DineroinsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS) // con esto puedo sacar el static porque maneja una referencia comun para todos los metodos. Igual no se recomienda esto
class CuentaTest {

    /*

    Los métodos se ejecutan en orden aleatorio, deben ser independientes entre ellos.
    La ejecución de los metodos la designa Junit Platform, se puede llegar a ordenar la ejecución de los metodos, pero nunca hacerlos independientes, reutilizar una instancia dependiente
    Se recomienda que los test no manejen estados, que sea styless
     */


    Cuenta cuenta;

    private TestInfo testInfo;
    private TestReporter testReporter;
    @BeforeAll
    // es un metodo que no está asociado a la instancia CuentaTest (se crea antes), ya está creado antes por que es estatic, pertenece a la clase, no a la instancia. Si le saco el static no lo puedo llamar, porque aún no se crea la instancia, por eso es importante que sea static
    static void beforeAll() {
        System.out.println("Comenzando las pruebas");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Terminando las pruebas");
    }



    @BeforeEach
    void initMetodoTest(TestInfo testInfo, TestReporter testReporter) {
        this.cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        System.out.println("iniciamos el metodo");
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        System.out.println("iniciando el metodo.");
        testReporter.publishEntry(" ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().orElse(null).getName()
                + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando Metodo");
    }

                /*
        Las clases Anidadadas nos van a servir para categorizar las pruebas, por ejemplos las de variables del sistema, cuentas, etc
        Son clases por default dentro de la clase principal
        También permiten colocar el beforeEach y AferEach y Displayname
        Si falla una prueba hija de una clase anidada, también va a salir como que falló la clase abuela.
        Es más facil de detectar un error y hacer un buen seguimieno y buena organización
         */

    @Tag("cuenta")
    @Nested
    @DisplayName("probando atributos de cuenta corriente")
    class CuentaTestNombreYSaldoTest {
        @Test
        // reemplza el nombre del metodo
        @DisplayName("Probando el nombre de la cuenta corriente")
            // varias pruebas unitarias en el mismo metodo, aserciones
        void testNombreCuenta() {
            testReporter.publishEntry(testInfo.getTags().toString());
            if (testInfo.getTags().contains("cuenta")) {
                testReporter.publishEntry("hacer algo con la etiqueta cuenta");
            }
            //Cuenta cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
            //cuenta.setPersona("Anderes");
            String esperado = "Andres";
            String real = cuenta.getPersona();
            // lo de validar los nulls sería hilar un poco mas fino
            // El mensaje es opcional, pero si lo pasamos en forma de String va a crear siempre el objeto falle o no. No lo va a mostrar sino falla pero lo va a crear, y muchas creaciones va a relentizar todo
            // lo mejor es pasar una función lambda, en vez de que cree instancias

            assertNotNull(real, "la cuena no puede ser nula");
            // solamente si falla se invoca el metodo del mensaje, es mas eficiente que String solo
            // con la lambda le estamos pasando una invocación futura, que se crea cuando hay error
            assertEquals(esperado, real, () -> "El nombre de la cuena no es el que se esperaba");
            assertTrue("Andres".equals(real), "nombre cuena esperada debe ser igual a la real");
        }


        @Test
        @Disabled
            //deshabilito el test. Si no ponemos @test no se va a ejecutar, pero no va aparecer como documentación en la bateria de test
        void testSaldoCuenta() {
            // fuerzo el error, pero el metodo esta deshabilitado
            fail();
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @Test
        void testReferenciaCuenta() { // primer ejemplo de TDD
            cuenta = new Cuenta("John Doe", new BigDecimal("89009997"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("89009997"));
            assertNotNull(cuenta.getSaldo());
            //assertNotEquals(cuenta2, cuenta); // da ok porque compara las referencias en este caso. Si se sobreescribe el metodo equals da false
            assertEquals(cuenta2, cuenta); //ahora si va a dar el ok, como que tienen los mismo valores porque se sobrescribio el metodo equals
        }
    }


    @Nested
    class CuentaOperacionesTest {
        @Tag("cuenta")

        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100)); // si el valor es mayor al saldo no pasa la prueba
            assertNotNull(cuenta.getSaldo());
            assertEquals(900, cuenta.getSaldo().intValue()); //aca elimina la parte decimal
            assertEquals("900.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")

        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1100, cuenta.getSaldo().intValue());
            assertEquals("1100.12345", cuenta.getSaldo().toPlainString());
        }

        @Tag("cuenta")
        @Tag("banco")

        @Test
        void testTransFerirDineroCuentas() {
            Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
            Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

            Banco banco = new Banco();
            banco.setNombre("Banco del Estado");

            banco.transferir(cuenta2, cuenta1, new BigDecimal(500));
            assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()); //primero probamos cuenta origen
            assertEquals("3000", cuenta1.getSaldo().toPlainString()); //primero probamos cuenta origen
        }

    }

    @Test
    @Tag("cuenta")
    @Tag("error")
    void testDineroInsuficienteExceptionCuenta() {
        cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));


        Exception exception = assertThrows(DineroinsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });

        // este no pasa porque es otra excepcion
      /*  Exception exception = assertThrows(NumberFormatException.class, () -> {
            cuenta.debito(new BigDecimal(1500));
        });*/

        String esperado = "Dinero Insuficiente";
        String actual = exception.getMessage();
        // si el assertThrows da false ni siquiera llega hasta acá
        assertEquals(esperado, actual);
    }


    @Test
    @Tag("cuenta")
    @Tag("error")
    void testRelacionBancoCuentas() {
        Cuenta cuenta1 = new Cuenta("John Doe", new BigDecimal("2500"));
        Cuenta cuenta2 = new Cuenta("Andres", new BigDecimal("1500.8989"));

        Banco banco = new Banco();
        banco.addCuenta(cuenta1);
        banco.addCuenta(cuenta2);

        banco.setNombre("Banco del Estado");
        banco.transferir(cuenta2, cuenta1, new BigDecimal(500));

        // lo bueno del asserAll, es que si falla una sigue ejecutando las otras, y al final muestra un resumen de cuales fallaron y salieron correctas
        assertAll(
                () -> assertEquals("1000.8989", cuenta2.getSaldo().toPlainString()), //primero probamos cuenta origen
                () -> assertEquals("3000", cuenta1.getSaldo().toPlainString()),//primero probamos cuenta origen
                () -> assertEquals(2, banco.getCuentas().size()),
                () -> assertEquals("Banco del Estado", cuenta1.getBanco().getNombre()),
                () -> assertEquals("Andres", banco.getCuentas().stream()
                        .filter(c -> "Andres".equals(c.getPersona()))
                        .findFirst()
                        .get().getPersona()
                ),
                () -> assertTrue(banco.getCuentas().stream()
                        .filter(c -> "Andres".equals(c.getPersona()))
                        .findFirst()
                        .isPresent()
                ),
                () -> // otra variante
                        assertTrue(banco.getCuentas().stream()
                                .anyMatch(c -> c.getPersona().equals("Andres"))
                        )

        );


    }


    // Assumption

    @Test
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev); // si esto devuelve true, ejecuto la prueba. Si sale false no falla, simplemente se deshabilita de forma programatica sin anoaciones

        assertNotNull(cuenta.getSaldo());
        assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @Test
    void testSaldoCuentaDevDeshabiliacionParcial() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> { // dentro de la lambda metemos el codigo que queremos habilitar o deshabiliar según se cumpla la condición o no
            assertNotNull(cuenta.getSaldo());
            assertEquals(1000.12345, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        });

        // salga true o false lo de arriba esto lo ejecuta igual
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);


    }


    @Nested
    class SistemaOperativoTest {
    /*
    Con los test condicionales podemos hacer que unos metodos se ejecuten en ciertos ambientes
    por ejemplo no queremos que se ejecuten en producción, o en linux, etc

     */

        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        // Para que funcione hay que ejecutar la clase completa, no el metodo
        @Test
        @EnabledOnOs({OS.LINUX, OS.MAC})
        // queda deshabilitado
        void testSoloLinuxMac() {

        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testSoloJava8() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_15)
        void testSoloJava15() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void testImprimirSystemProperties() {
            Properties properties = System.getProperties();
            // mapa, muestra todas las propiedades
            properties.forEach((k, v) -> System.out.println(k + " " + v));
        }

    }

    @Nested
    class SysemPropertiesTest {
        /* Variables de sistema */
        @EnabledIfSystemProperty(named = "java.version", matches = ".*15.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testSolo64Bits() {

        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch", matches = ".*32.*")
        void testNo64Bits() {

        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "agusz")
        void testUserName() {

        }

        // ambiente
        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {

        }

    }

    @Nested
    class VariableAmbienteTest {
        // Variables de Ambiente

        @Test
        void imprimirVariablesDeAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> System.out.println(k + " " + "v"));
        }

        @Test
        @Disabled
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = "ubicacion/sdfds")
        void testJavaHome() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "8")
        void testCantidadDeProcesadores() {

        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIROMENTS", matches = "dev")
        void testEnv() {

        }

        @Test // ver propiedades jvm,
        @DisabledIfEnvironmentVariable(named = "ENVIROMENTS", matches = "prod")
        void testNoProd() {

        }
    }

    /*

    Repeated Test se usa cuando nuestro metodo tiene cierta aleatoriedad
    algun parametro o variable podría cambiar. Se puede usar en anidada mb

     */

    @DisplayName("Probando Debito Cuenta Repetir")
    @RepeatedTest(value = 5, name = " {displayName} Repeticion numero {currentRepetition} de {totalRepetitions}")
        // muestra los valores reales
        // @RepeatedTest(5)
    void testDebitoCuentaRepetir(RepetitionInfo info) { // se mete por inyección RepetitionInfo
        if (info.getCurrentRepetition() == 3) {
            System.out.println("repeticion 3");
        }
        cuenta = new Cuenta("Andres", new BigDecimal("1000.12345"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue()); //aca elimina la parte decimal
        assertEquals("900.12345", cuenta.getSaldo().toPlainString());
    }

    @Tag("param")
    @Nested
    class PruebasParametrizadasTest {
        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @ValueSource(strings = {"100", "200", "300", "500", "1000"})
        // podria ser tambien doubles = {1,2,3,3}
        void testDebitoCuentaParametrizada(String monto) { // inyecto los montos de a uno
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"1,100", "2,200", "3,300", "4,500", "5,700", "6,1000.12345"})
        void testDebitoCuentaCsvSource(String index, String monto) {
            System.out.println(index + " -> " + monto);
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvSource({"200,100,John,Andres", "250,200,Pepe,Pepe", "300,300,maria,Maria", "510,500,Pepa,Pepa", "750,700,Lucas,Luca", "1000.12345,1000.12345,Cata,Cata"})
        void testDebitoCuentaCsvSource2(String saldo, String monto, String esperado, String actual) {
            System.out.println(saldo + " -> " + monto);
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data.csv")
        void testDebitoCuentaCsvFileSource(String monto) {
            cuenta.debito(new BigDecimal(monto));
            assertNotNull(cuenta.getSaldo());
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }

        @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
        @CsvFileSource(resources = "/data2.csv")
        void testDebitoCuentaCsvFileSource2(String saldo, String monto, String esperado, String actual) {
            cuenta.setSaldo(new BigDecimal(saldo));
            cuenta.debito(new BigDecimal(monto));
            cuenta.setPersona(actual);

            assertNotNull(cuenta.getSaldo());
            assertNotNull(cuenta.getPersona());
            assertEquals(esperado, actual);

            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
        }


    }

    @Tag("param")
    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @MethodSource("montoList")
    void testDebitoCuentaMethodSource(String monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("100", "200", "300", "500", "700", "1000.12345");
    }

    @Nested
    @Tag("timeout")
    class EjemploTimeoutTest {
        @Test
        @Timeout(1)
        void pruebaTimeout() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(100);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeout2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void testTimeoutAssertions() {
            assertTimeout(Duration.ofSeconds(5), () -> {
                TimeUnit.MILLISECONDS.sleep(4000);
            });
        }
    }

}
package ejemplos.services;

import ejemplos.Datos;
import ejemplos.models.Examen;
import ejemplos.repositories.ExamenRepository;
import ejemplos.repositories.ExamenRepositoryImpl;
import ejemplos.repositories.PreguntaRepository;
import ejemplos.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

// Está es otra forma de habilitar las anotaciones de mockit
@ExtendWith(MockitoExtension.class) // ya con esto extendemos nuestra clase de test unit para usar mockito, importante tener la dependencia mockito-jupiter
class ExamenServiceImplTest {

    /*
        @Mock
        ExamenRepository examenRepository;
        @Mock
        PregunaRepository preguntaRepository;
    */
    /* uso estas dos impl en vez de usar las interfaces para el docallMethod real" */
    @Mock
    ExamenRepositoryImpl examenRepository;

    @Mock
    PreguntaRepositoryImpl preguntaRepository;


    // crea la instancia de service y además le inyecta los mocks
    @InjectMocks
    //ExamenService examenService; // no se puede instanciar poque es interfaz
            ExamenServiceImpl service;


    @Captor
    ArgumentCaptor<Long> captor;


    @BeforeEach
    void setup() {
        // con openMock habilitamos el uso de anotaciones para esta clase
        //MockitoAnnotations.openMocks(this);
/*  Estas inyecciones se reemplazan por las anotaciones @Mock
        examenRepository = mock(ExamenRepository.class);
        pregunaRepository = mock(PregunaRepository.class);
*/

        //examenService = new ExamenServiceImpl(examenRepository, pregunaRepository);


    }

    @Test
    void findExamenPorNombre() {
        // eliminas RepositoryImpl
        // ExamenRepository reposiory = new ExamenRepositoryImpl();
        List<Examen> datos = Datos.EXAMENES;
        when(examenRepository.findaAll()).thenReturn(datos);


        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.get().getId());
        assertEquals("Matemáticas", examen.get().getNombre());

    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(examenRepository.findaAll()).thenReturn(datos);
        Optional<Examen> examen = service.findExamenPorNombre("Matemáticas");
        assertFalse(examen.isPresent());
    }

    @Test
    void testExamenConPreguntas() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        // solamente 7 devuelve la pregunta, para resolver eso usar anyLong(), para cualquier Long
        //when(pregunaRepository.findPreguntasPorExamenId(7L)).thenReturn(Datos.PREGUNTAS);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void testExamenConPreguntasVerificar() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
        // verifica si ejecuté este metodo, sino se ejecutó falla la prueba
        verify(examenRepository).findaAll();
        verify(preguntaRepository).findPreguntasPorExamenId(7L);
    }

    @Test
    void testNoExisteExamenVerify() {
        when(examenRepository.findaAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas2");
        assertNull(examen);
        verify(examenRepository).findaAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGuardarExamen() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(examenRepository.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
        Examen examen = service.guardar(newExamen);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        // si el examen va sin preguntas no se llama este meodo, sea o no usando con el when
        verify(preguntaRepository).guardarVarias(anyList());


    }

    @Test
    void testManejoDeExcepciones() {
        //when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        // when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        // cualquier argumento que sea null para a lanzar una excepcion illegal.. con argumentMatcher podemos reemplzar null por isnull8)
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matemáticas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examenRepository).findaAll();
        //verify(pregunaRepository).findPreguntasPorExamenId(anyLong());
        verify(preguntaRepository).findPreguntasPorExamenId(null);

    }

    @Test
    void testArgumentMatchers() {
        // con el when damos comportamiento al mock
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findaAll();
        //este metodo puede ser de ArgumenMatcher.argThat o de Mockito.argThant, asi solo argThat es con mockito
        // verify(pregunaRepository).findPreguntasPorExamenId(argThat( arg -> arg != null && arg.equals(5L))); // Aca validamos que el mtodo hayar
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 3));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(6L));

    }

    /*
    Argument Matcher personalizado. Puede ser con una clase anonima, implementado al vuelo
    , una clase personalizada o una inner class (clase anidada))
     */

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }
        // otra característica o ventaja, es que podemos personalizar el mensaje de errror cuando falla


        @Override
        public String toString() {
            return "es para un mensaje personalizado de error que se imprime cuando falla el test"
                    + argument + "debe ser un entero positivo";
        }
    }

    @Test
    void testArgumentMatchers2() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findaAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));

    }

    //capturar argumento
    @Test
    void testArgumentCaptor() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        // when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matemáticas");

        //Esto se puede reeemplazar por la anotación @Captor
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5l, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        //es importante pasarle estas preguntas para que el metodo se invoque
        examen.setPreguntas(Datos.PREGUNTAS);
        // no se puede usar  solo el when porque el metodo guardarVarias devuelve void. Se combina dotrow con when
        // con doThow le ordenamos al metodo que lance una excepción cuando se invoque
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
//        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        // sirve para retornar datos más personalizados, un when mas personalizado
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0); // obtiene el primer argumento, en este caso como hay uno es el id
            return id == 5l ? Datos.PREGUNTAS : Collections.emptyList(); //null; si lo dejamos en null va a lanzar una excepción y no va a mostrar la falla el tes
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas"); // "masdfasdfa" con esto hacemos que se devuelva emptylist

        assertEquals(5l, examen.getId());
        assertNotNull(examen);

        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    // invocar el metodo real del mock, no el simulado, es decir que no devuelva dato simulado


    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        //acá estamos simulando el metodo, no estamos invocando el metodo real
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        // solo usa metodos implementados
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = service.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5l, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());


    }

    /* Los spy (espias) son una combinación entre un objeto real
    y un hibrido. Donde si no se le indica que simule cierto metodo, va a ejecutar el real por defecto
     Es un clon del objeto real con caracteristicas de mock
     No hay que abusar del spy, de las llamadaas reales a los metodos, porque no tenemos el control al 100% de lo que pueda ocurrir al invocarlos
     Los espias solo usan cuando de verdad necesitamos llamar a ciertos metodos reales, si no se usa el when y demás
            // es muy parecido al metodo (mock()) pero el mock es 100% simulado, por lo tanto debemos mockear sus metodos con el when o el do algo
        // Con spy, solo vamos a simular lo que quereamos
        // Otra característica es que el spy requiere que se cree a partir de la clase concreta y no de una abstracta o interfaz, por eso hay que utilizar la implementación concreta
        // hay que trabajar con mocks a partir de objetos concretos
        // Es decir si usamos mock() podemos usar interfaz o implementación concreta, pero con spy soo implementación

     */

    @Test
    void testSpy() {
        //ExamenRepository examenRepository = mock(ExamenRepository.class);

        // como tipo de dato se podría usar el tipo de dato pero despues del igual va la implementación concreta
        ExamenRepository examenRepo = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepo = spy(PreguntaRepositoryImpl.class);
        // inyectamos las dependencias  spy
        ExamenService examenService = new ExamenServiceImpl(examenRepo, preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmetica");
        // acá hay un problema, por eso no se usa el when junto a spy, aca se estaría ejecutando el metodo real, y desde examenService, la linea de abajo se estaría ejecutando el simulado a traves del metodo por id
        //when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        //acá si devuelve los datos falsos, y hace la invocación simulada
        doReturn(preguntas).when(preguntaRepo).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        //assertEquals(5, examen.getPreguntas().size());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(examenRepo).findaAll(); // este se llama de forma real
        verify(preguntaRepo).findPreguntasPorExamenId(anyLong()); // y este llama al simulado, el mock


    }

    //verificar el orden en que se ejecutan los metodos mocks


    @Test
    void testOrdenDeInvocaciones() {
        // sin los datos que da este findall simulado, saltaria error en los metodos verificados en orden
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        // le pasamos por argumentos el o los mocks, para ejecutar el orden de ejecución de sus metodos
        InOrder inOrder =  inOrder(preguntaRepository);
        // con el orden de 6l y despues 5l  falla porque primero se invoca matematica y dsps lenguaje
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(7L);
    }

    @Test
    void testOrdenDeInvocaciones2() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");
        service.findExamenPorNombreConPreguntas("Lenguaje");

        // ord
        InOrder inOrder =  inOrder(examenRepository, preguntaRepository);
        inOrder.verify(examenRepository).findaAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(examenRepository).findaAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);


    }

    @Test
    void testNumeroDeInvocaciones(){
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");
        // cantidad de veces que uno espera que se invoque, por defecto es uno, si no se cumple falla
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5l);
        // atLeas significa que por lo menos se ejecute una sola vez
        verify(preguntaRepository, times(1)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5l);
        // metodo statico, por lo menos una vez, paraecido al anterior
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5l);
        // como maximo ejecutalo 10 veces, entre 1 y 10
        verify(preguntaRepository, atMost(10)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);



    }


    @Test
    void testNumeroDeInvocaciones2(){
        // si a findall lo modifico en findPreguntasPorExamenId para que se llame dos veces
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPreguntas("Matemáticas");
        //falla
        //verify(preguntaRepository).findPreguntasPorExamenId(5l);
        verify(preguntaRepository, times(2)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository, atLeast(1)).findPreguntasPorExamenId(5l);
        verify(preguntaRepository, atLeastOnce()).findPreguntasPorExamenId(5l);
        verify(preguntaRepository, atMost(30)).findPreguntasPorExamenId(5L);
        // falla
        //verify(preguntaRepository, atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void testNumeroDeInvocaciones3() {
        // como es una lista vacia lo que devuelve, no va a encontrar el examen, por lo tanto no se va a llamar el metodo findpreguntasPersonaId, no va  atener ninguna interacción con preguntaRepository

        when(examenRepository.findaAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPreguntas("Matemáticas");
        verify(preguntaRepository, never()).findPreguntasPorExamenId(5L);
        //interacción cero
        verifyNoInteractions(preguntaRepository);

        verify(examenRepository).findaAll();
    }
}
package ejemplos.services;

import ejemplos.Datos;
import ejemplos.models.Examen;
import ejemplos.repositories.ExamenRepository;
import ejemplos.repositories.PregunaRepository;
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

    @Mock
    ExamenRepository examenRepository;
    @Mock
    PregunaRepository pregunaRepository;

    // crea la instancia de service y además le inyecta los mocks
    @InjectMocks
    //ExamenService examenService; // no se puede instanciar poque es interfaz
            ExamenServiceImpl examenService;


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


        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.get().getId());
        assertEquals("Matemáticas", examen.get().getNombre());

    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();
        when(examenRepository.findaAll()).thenReturn(datos);
        Optional<Examen> examen = examenService.findExamenPorNombre("Matemáticas");
        assertFalse(examen.isPresent());
    }

    @Test
    void testExamenConPreguntas() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        // solamente 7 devuelve la pregunta, para resolver eso usar anyLong(), para cualquier Long
        //when(pregunaRepository.findPreguntasPorExamenId(7L)).thenReturn(Datos.PREGUNTAS);
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = examenService.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void testExamenConPreguntasVerificar() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = examenService.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));
        // verifica si ejecuté este metodo, sino se ejecutó falla la prueba
        verify(examenRepository).findaAll();
        verify(pregunaRepository).findPreguntasPorExamenId(7L);
    }

    @Test
    void testNoExisteExamenVerify() {
        when(examenRepository.findaAll()).thenReturn(Collections.emptyList());
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas2");
        assertNull(examen);
        verify(examenRepository).findaAll();
        verify(pregunaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void testGuardarExamen() {
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(examenRepository.guardar(any(Examen.class))).thenReturn(Datos.EXAMEN);
        Examen examen = examenService.guardar(newExamen);
        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(examenRepository).guardar(any(Examen.class));
        // si el examen va sin preguntas no se llama este meodo, sea o no usando con el when
        verify(pregunaRepository).guardarVarias(anyList());


    }

    @Test
    void testManejoDeExcepciones() {
        //when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        // when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenThrow(IllegalArgumentException.class);
        // cualquier argumento que sea null para a lanzar una excepcion illegal.. con argumentMatcher podemos reemplzar null por isnull8)
        when(pregunaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            examenService.findExamenPorNombreConPreguntas("Matemáticas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(examenRepository).findaAll();
        //verify(pregunaRepository).findPreguntasPorExamenId(anyLong());
        verify(pregunaRepository).findPreguntasPorExamenId(null);

    }

    @Test
    void testArgumentMatchers() {
        // con el when damos comportamiento al mock
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findaAll();
        //este metodo puede ser de ArgumenMatcher.argThat o de Mockito.argThant, asi solo argThat es con mockito
        // verify(pregunaRepository).findPreguntasPorExamenId(argThat( arg -> arg != null && arg.equals(5L))); // Aca validamos que el mtodo hayar
        verify(pregunaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg > 3));
        verify(pregunaRepository).findPreguntasPorExamenId(eq(6L));

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
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        verify(examenRepository).findaAll();
        verify(pregunaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));

    }

    //capturar argumento
    @Test
    void testArgumentCaptor() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
       // when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        examenService.findExamenPorNombreConPreguntas("Matemáticas");

        //Esto se puede reeemplazar por la anotación @Captor
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(pregunaRepository).findPreguntasPorExamenId(captor.capture());
        assertEquals(5l, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Examen examen = Datos.EXAMEN;
        //es importante pasarle estas preguntas para que el metodo se invoque
        examen.setPreguntas(Datos.PREGUNTAS);
        // no se puede usar  solo el when porque el metodo guardarVarias devuelve void. Se combina dotrow con when
        // con doThow le ordenamos al metodo que lance una excepción cuando se invoque
        doThrow(IllegalArgumentException.class).when(pregunaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            examenService.guardar(examen);
        });
    }

    @Test
    void testDoAnswer() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
//        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        // sirve para retornar datos más personalizados, un when mas personalizado
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0); // obtiene el primer argumento, en este caso como hay uno es el id
            return id == 5l? Datos.PREGUNTAS: Collections.emptyList(); //null; si lo dejamos en null va a lanzar una excepción y no va a mostrar la falla el tes
        }).when(pregunaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas"); // "masdfasdfa" con esto hacemos que se devuelva emptylist

        assertEquals(5l, examen.getId());
        assertNotNull(examen);

        verify(pregunaRepository).findPreguntasPorExamenId(anyLong());
    }

   // invocar el metodo real del mock, no el simulado, es decir que no devuelva dato simulado


    @Test
    void testDoCallRealMethod() {
        when(examenRepository.findaAll()).thenReturn(Datos.EXAMENES);
        when(pregunaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        // solo usa metodos implementados
        //doCallRealMethod().when().findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5l, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());



    }


}
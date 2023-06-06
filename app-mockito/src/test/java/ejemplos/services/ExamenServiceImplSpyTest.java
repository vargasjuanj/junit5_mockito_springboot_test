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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplSpyTest {

    //spy con anotaciones
    @Spy
    ExamenRepositoryImpl examenRepository;

    @Spy
    PreguntaRepositoryImpl preguntaRepository;

    @InjectMocks
            ExamenServiceImpl examenService;



    @Test
    void testSpy() {

        List<String> preguntas = Arrays.asList("aritmetica");
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPreguntas("Matemáticas");
        assertEquals(5, examen.getId());
        assertEquals("Matemáticas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(examenRepository).findaAll(); // este se llama de forma real
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong()); // y este llama al simulado, el mock


    }


}
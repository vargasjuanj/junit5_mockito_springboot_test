package ejemplos.services;

import ejemplos.models.Examen;
import ejemplos.repositories.ExamenRepository;
import ejemplos.repositories.ExamenRepositoryImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExamenServiceImplTest {

    @Test
    void findExamenPorNombre() {
        ExamenRepository reposiory = new ExamenRepositoryImpl();
        ExamenService service = new ExamenServiceImpl(reposiory);
        Examen examen = service.findExamenPorNombre("Matemáticas");
        assertNotNull(examen);
        assertEquals(5L, examen.getId());
        assertEquals("Matemáticas",examen.getNombre());

    }
}
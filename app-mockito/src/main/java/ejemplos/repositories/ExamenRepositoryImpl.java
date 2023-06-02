package ejemplos.repositories;

import ejemplos.models.Examen;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

// Simula un repositorio, despues esto se reemplaza por mockito
public class ExamenRepositoryImpl implements ExamenRepository{

    @Override
    public List<Examen> findaAll() {
        return Collections.emptyList() // si quisieramos probar que devuelve una lista vacia también hay que modificar este metodo, sin contar los otros casos
        return Arrays.asList(new Examen(5L,"Matemáticas"), new Examen(6L,"Lenguaje"), new Examen(7L,"Historia"));

    }
}

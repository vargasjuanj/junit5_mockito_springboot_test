package ejemplos.repositories;

import ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findaAll();
    Examen guardar (Examen examen);

}

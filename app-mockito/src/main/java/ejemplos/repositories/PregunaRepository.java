package ejemplos.repositories;

import java.util.List;

public interface PregunaRepository {

    List<String> findPreguntasPorExamenId(Long id);

    void guardarVarias(List<String> preguntas);
}

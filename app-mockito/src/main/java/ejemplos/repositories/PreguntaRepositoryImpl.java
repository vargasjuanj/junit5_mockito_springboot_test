package ejemplos.repositories;

import ejemplos.Datos;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PreguntaRepositoryImpl implements PreguntaRepository {
    static int count = 0;
    @Override
    public List<String> findPreguntasPorExamenId(Long id) {
        System.out.println( ++count + "PreguntaRepositoryImpl.findPreguntasPorExamenId");
        try{
            TimeUnit.SECONDS.sleep(2);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Datos.PREGUNTAS;
    }

    @Override
    public void guardarVarias(List<String> preguntas) {
        System.out.println("PreguntaRepositoryImpl.guardarVarias");
    }
}

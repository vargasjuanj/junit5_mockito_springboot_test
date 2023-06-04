package ejemplos.services;

import ejemplos.models.Examen;
import ejemplos.repositories.ExamenRepository;
import ejemplos.repositories.PregunaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PregunaRepository pregunaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PregunaRepository pregunaRepository) {
        this.examenRepository = examenRepository;
        this.pregunaRepository = pregunaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
         return examenRepository.findaAll()
                .stream()
                .filter(e -> e.getNombre().contains(nombre))
                .findFirst();

    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.get();
            List<String> preguntas = pregunaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){
            pregunaRepository.guardarVarias(examen.getPreguntas());

        }
        return examenRepository.guardar(examen);
    }
}

package ejemplos.repositories;

import ejemplos.Datos;
import ejemplos.models.Examen;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamenRepositoryImpl implements  ExamenRepository{

    @Override
    public Examen guardar(Examen examen) {
        System.out.println("ExamenRepositoryImpl.guardar");
        return Datos.EXAMEN;
    }

    @Override
    public List<Examen> findaAll() {
        System.out.println("ExamenRepositoryImpl.findaAll");
        try{
            TimeUnit.SECONDS.sleep(5);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }


}

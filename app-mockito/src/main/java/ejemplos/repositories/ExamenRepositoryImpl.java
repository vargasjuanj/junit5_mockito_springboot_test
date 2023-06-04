package ejemplos.repositories;

import ejemplos.Datos;
import ejemplos.models.Examen;

import java.util.List;

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
            System.out.println("ExamenRepositoryOtro");
        }catch (Exception e){
            e.printStackTrace();
        }
        return Datos.EXAMENES;
    }


}

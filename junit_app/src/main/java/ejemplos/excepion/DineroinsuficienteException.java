package ejemplos.excepion;

public class DineroinsuficienteException extends RuntimeException {
    public DineroinsuficienteException(String message){
        super(message);
    }

}

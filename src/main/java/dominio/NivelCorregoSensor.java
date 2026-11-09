package dominio;

/** Nível da água do córrego em metros. */
public class NivelCorregoSensor extends SensorSimulado {

    public NivelCorregoSensor(String id) {
        super(id, "NIVEL_CORREGO", 0.0, 5.0);
    }
}

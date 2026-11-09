package dominio;

/** Umidade do solo em porcentagem. */
public class UmidadeSoloSensor extends SensorSimulado {

    public UmidadeSoloSensor(String id) {
        super(id, "UMIDADE_SOLO", 10.0, 100.0);
    }
}

package dominio;

/** Chuva em mm/h. */
public class PluviometroSensor extends SensorSimulado {

    public PluviometroSensor(String id) {
        super(id, "CHUVA", 0.0, 120.0);
    }
}

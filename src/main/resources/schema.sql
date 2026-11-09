-- Tabelas do sistema. O script é idempotente: pode rodar a cada inicialização.

CREATE TABLE IF NOT EXISTS medicao (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_sensor VARCHAR(50)  NOT NULL,
    tipo      VARCHAR(30)  NOT NULL,
    valor     DOUBLE       NOT NULL,
    data_hora TIMESTAMP    NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_medicao_sensor_data ON medicao (id_sensor, data_hora);

CREATE TABLE IF NOT EXISTS alerta (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_sensor      VARCHAR(50)   NOT NULL,
    valor_previsto DOUBLE        NOT NULL,
    limite         DOUBLE        NOT NULL,
    nivel          VARCHAR(10)   NOT NULL,
    mensagem       VARCHAR(1000) NOT NULL,
    data_hora      TIMESTAMP     NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_alerta_data ON alerta (data_hora);

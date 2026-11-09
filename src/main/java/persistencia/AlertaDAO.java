package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** Acesso ao histórico de alertas emitidos. Mesmas regras do MedicaoDAO. */
public class AlertaDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO alerta (id_sensor, valor_previsto, limite, nivel, mensagem, data_hora) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_TODOS =
            "SELECT id_sensor, valor_previsto, limite, nivel, mensagem, data_hora FROM alerta "
                    + "ORDER BY data_hora DESC, id DESC";

    private static final String SQL_POR_SENSOR =
            "SELECT id_sensor, valor_previsto, limite, nivel, mensagem, data_hora FROM alerta "
                    + "WHERE id_sensor = ? ORDER BY data_hora DESC, id DESC";

    private final ConexaoBanco conexaoBanco;

    public AlertaDAO(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    public void salvar(Alerta alerta) throws DadosInvalidosException, FonteIndisponivelException {
        if (alerta == null) {
            throw new DadosInvalidosException("Não é possível gravar um alerta nulo.");
        }
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR)) {
            ps.setString(1, alerta.getIdSensor());
            ps.setDouble(2, alerta.getValorPrevisto());
            ps.setDouble(3, alerta.getLimite());
            ps.setString(4, alerta.getNivel().name());
            ps.setString(5, alerta.getMensagem());
            ps.setTimestamp(6, Timestamp.valueOf(alerta.getDataHora()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao gravar o alerta do sensor " + alerta.getIdSensor() + ": " + e.getMessage(), e);
        }
    }

    /** Todos os alertas já emitidos, do mais recente para o mais antigo. */
    public List<Alerta> listarTodos() throws FonteIndisponivelException {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_TODOS);
             ResultSet rs = ps.executeQuery()) {
            return mapear(rs);
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao listar os alertas: " + e.getMessage(), e);
        }
    }

    public List<Alerta> listarPorSensor(String idSensor) throws FonteIndisponivelException {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_POR_SENSOR)) {
            ps.setString(1, idSensor);
            try (ResultSet rs = ps.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao listar os alertas do sensor " + idSensor + ": " + e.getMessage(), e);
        }
    }

    private List<Alerta> mapear(ResultSet rs) throws SQLException, FonteIndisponivelException {
        List<Alerta> alertas = new ArrayList<>();
        while (rs.next()) {
            NivelAlerta nivel;
            try {
                nivel = NivelAlerta.valueOf(rs.getString("nivel"));
            } catch (IllegalArgumentException e) {
                throw new FonteIndisponivelException(
                        "Nível de alerta desconhecido gravado no banco: " + rs.getString("nivel"), e);
            }
            alertas.add(new Alerta(
                    rs.getString("id_sensor"),
                    rs.getDouble("valor_previsto"),
                    rs.getDouble("limite"),
                    nivel,
                    rs.getString("mensagem"),
                    rs.getTimestamp("data_hora").toLocalDateTime()));
        }
        return alertas;
    }
}

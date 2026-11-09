package persistencia;

import dominio.Medicao;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Acesso ao histórico de medições. Todo SQL usa PreparedStatement
 * (nunca concatenação de String) e os recursos são fechados com try-with-resources.
 *
 * O DAO só entrega os dados: decidir se há leituras suficientes é papel do serviço.
 */
public class MedicaoDAO {

    private static final String SQL_INSERIR =
            "INSERT INTO medicao (id_sensor, tipo, valor, data_hora) VALUES (?, ?, ?, ?)";

    private static final String SQL_POR_SENSOR =
            "SELECT id_sensor, tipo, valor, data_hora FROM medicao "
                    + "WHERE id_sensor = ? ORDER BY data_hora ASC, id ASC";

    private static final String SQL_ULTIMAS =
            "SELECT id_sensor, tipo, valor, data_hora FROM medicao "
                    + "WHERE id_sensor = ? ORDER BY data_hora DESC, id DESC LIMIT ?";

    private static final String SQL_CONTAR =
            "SELECT COUNT(*) FROM medicao WHERE id_sensor = ?";

    private static final String SQL_SENSORES =
            "SELECT DISTINCT id_sensor FROM medicao ORDER BY id_sensor";

    private final ConexaoBanco conexaoBanco;

    public MedicaoDAO(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    public void salvar(Medicao medicao) throws DadosInvalidosException, FonteIndisponivelException {
        if (medicao == null) {
            throw new DadosInvalidosException("Não é possível gravar uma medição nula.");
        }
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR)) {
            ps.setString(1, medicao.getIdSensor());
            ps.setString(2, medicao.getTipo());
            ps.setDouble(3, medicao.getValor());
            ps.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao gravar a medição do sensor " + medicao.getIdSensor() + ": " + e.getMessage(), e);
        }
    }

    /** Histórico completo do sensor, da mais antiga para a mais recente. */
    public List<Medicao> listarPorSensor(String idSensor) throws FonteIndisponivelException {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_POR_SENSOR)) {
            ps.setString(1, idSensor);
            try (ResultSet rs = ps.executeQuery()) {
                return mapear(rs);
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao consultar o histórico do sensor " + idSensor + ": " + e.getMessage(), e);
        }
    }

    /**
     * As n medições mais recentes do sensor, devolvidas em ordem cronológica
     * (da mais antiga para a mais recente), prontas para a previsão.
     */
    public List<Medicao> buscarUltimasPorSensor(String idSensor, int n)
            throws DadosInvalidosException, FonteIndisponivelException {
        if (n <= 0) {
            throw new DadosInvalidosException("A quantidade de medições deve ser maior que zero: " + n);
        }
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_ULTIMAS)) {
            ps.setString(1, idSensor);
            ps.setInt(2, n);
            try (ResultSet rs = ps.executeQuery()) {
                List<Medicao> medicoes = mapear(rs);
                Collections.reverse(medicoes); // a consulta vem da mais recente para a mais antiga
                return medicoes;
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao buscar as últimas medições do sensor " + idSensor + ": " + e.getMessage(), e);
        }
    }

    /** Quantas leituras o sensor tem no banco (útil para a regra de "poucas leituras"). */
    public int contarPorSensor(String idSensor) throws FonteIndisponivelException {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_CONTAR)) {
            ps.setString(1, idSensor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new FonteIndisponivelException(
                    "Falha ao contar as medições do sensor " + idSensor + ": " + e.getMessage(), e);
        }
    }

    /** Ids dos sensores que já têm medições gravadas (para o menu de histórico). */
    public List<String> listarSensoresComHistorico() throws FonteIndisponivelException {
        try (Connection conexao = conexaoBanco.abrir();
             PreparedStatement ps = conexao.prepareStatement(SQL_SENSORES);
             ResultSet rs = ps.executeQuery()) {
            List<String> ids = new ArrayList<>();
            while (rs.next()) {
                ids.add(rs.getString("id_sensor"));
            }
            return ids;
        } catch (SQLException e) {
            throw new FonteIndisponivelException("Falha ao listar os sensores com histórico: " + e.getMessage(), e);
        }
    }

    private List<Medicao> mapear(ResultSet rs) throws SQLException, FonteIndisponivelException {
        List<Medicao> medicoes = new ArrayList<>();
        while (rs.next()) {
            try {
                medicoes.add(new Medicao(
                        rs.getString("id_sensor"),
                        rs.getString("tipo"),
                        rs.getDouble("valor"),
                        rs.getTimestamp("data_hora").toLocalDateTime()));
            } catch (DadosInvalidosException e) {
                throw new FonteIndisponivelException(
                        "Há uma medição inválida gravada no banco: " + e.getMessage(), e);
            }
        }
        return medicoes;
    }
}

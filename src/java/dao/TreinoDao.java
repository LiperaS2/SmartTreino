package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TreinoDao {

    // Busca os treinos da conta e os organiza pelo dia da semana.
    public List<Treino> listarPorUsuario(String email)
            throws ClassNotFoundException, SQLException {
        String sql = "SELECT id, nome, grupo_muscular, dia_semana, "
                + "duracao_minutos, observacoes FROM treino "
                + "WHERE usuario_email = ? "
                + "ORDER BY FIELD(dia_semana, 'Segunda-feira', 'Terça-feira', "
                + "'Quarta-feira', 'Quinta-feira', 'Sexta-feira', "
                + "'Sábado', 'Domingo'), id";
        List<Treino> treinos = new ArrayList<>();

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            // O parâmetro limita a consulta aos treinos do usuário autenticado.
            comando.setString(1, email);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    Treino treino = new Treino();
                    treino.setId(resultado.getLong("id"));
                    treino.setNome(resultado.getString("nome"));
                    treino.setGrupoMuscular(resultado.getString("grupo_muscular"));
                    treino.setDiaSemana(resultado.getString("dia_semana"));
                    treino.setDuracaoMinutos(resultado.getInt("duracao_minutos"));
                    treino.setObservacoes(resultado.getString("observacoes"));
                    treinos.add(treino);
                }
            }
        }
        return treinos;
    }

    // Salva um treino associando-o ao e-mail do usuário autenticado.
    public void adicionar(String email, Treino treino)
            throws ClassNotFoundException, SQLException {
        String sql = "INSERT INTO treino (usuario_email, nome, grupo_muscular, "
                + "dia_semana, duracao_minutos, observacoes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            comando.setString(2, treino.getNome());
            comando.setString(3, treino.getGrupoMuscular());
            comando.setString(4, treino.getDiaSemana());
            comando.setInt(5, treino.getDuracaoMinutos());
            comando.setString(6, treino.getObservacoes());
            comando.executeUpdate();
        }
    }

    // Remove somente o treino indicado que também pertença à conta atual.
    public boolean remover(String email, long id)
            throws ClassNotFoundException, SQLException {
        String sql = "DELETE FROM treino WHERE id = ? AND usuario_email = ?";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setLong(1, id);
            comando.setString(2, email);
            return comando.executeUpdate() == 1;
        }
    }
}

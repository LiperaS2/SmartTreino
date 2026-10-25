package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDao {

    // Insere os dados de cadastro do usuário usando parâmetros SQL.
    public int cadastrarUsuario(Usuario usuario)
            throws ClassNotFoundException, SQLException {
        String sql = "INSERT INTO usuario (nome, telefone, email, senha, termos) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            // Associa cada campo do objeto às colunas do comando INSERT.
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getTelefone());
            comando.setString(3, usuario.getEmail());
            comando.setString(4, usuario.getSenha());
            comando.setString(5, usuario.getTermos());
            return comando.executeUpdate();
        }
    }

    // Confere as credenciais e retorna o nome somente se a conta for encontrada.
    public String logar(String email, String senha)
            throws ClassNotFoundException, SQLException {
        String sql = "SELECT nome FROM usuario WHERE email = ? AND senha = ?";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            comando.setString(2, senha);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? resultado.getString("nome") : null;
            }
        }
    }

    public String obterTelefone(String email)
            throws ClassNotFoundException, SQLException {
        String sql = "SELECT telefone FROM usuario WHERE email = ?";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? resultado.getString("telefone") : null;
            }
        }
    }

    // Confere a senha na própria atualização para evitar alterar outra conta.
    public boolean atualizarTelefone(String email, String senhaAtual, String telefone)
            throws ClassNotFoundException, SQLException {
        String sql = "UPDATE usuario SET telefone = ? WHERE email = ? AND senha = ?";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, telefone);
            comando.setString(2, email);
            comando.setString(3, senhaAtual);
            return comando.executeUpdate() == 1
                    || credenciaisValidas(conexao, email, senhaAtual);
        }
    }

    // A senha atual também faz parte do filtro, além do e-mail da sessão.
    public boolean atualizarSenha(String email, String senhaAtual, String novaSenha)
            throws ClassNotFoundException, SQLException {
        String sql = "UPDATE usuario SET senha = ? WHERE email = ? AND senha = ?";

        try (Connection conexao = ConexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, novaSenha);
            comando.setString(2, email);
            comando.setString(3, senhaAtual);
            return comando.executeUpdate() == 1
                    || credenciaisValidas(conexao, email, senhaAtual);
        }
    }

    // O MySQL pode reportar zero linhas alteradas quando o valor enviado já é o atual.
    private boolean credenciaisValidas(Connection conexao, String email, String senha)
            throws SQLException {
        String sql = "SELECT 1 FROM usuario WHERE email = ? AND senha = ?";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, email);
            comando.setString(2, senha);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next();
            }
        }
    }

    public boolean excluirConta(String email, String senhaAtual)
            throws ClassNotFoundException, SQLException {
        String excluirTreinos = "DELETE FROM treino WHERE usuario_email = ?";
        String excluirUsuario = "DELETE FROM usuario WHERE email = ? AND senha = ?";

        try (Connection conexao = ConexaoBanco.abrir()) {
            // Treinos e conta são removidos juntos; se a senha falhar, os treinos voltam.
            conexao.setAutoCommit(false);
            try (PreparedStatement treinos = conexao.prepareStatement(excluirTreinos);
                    PreparedStatement usuario = conexao.prepareStatement(excluirUsuario)) {
                treinos.setString(1, email);
                treinos.executeUpdate();

                usuario.setString(1, email);
                usuario.setString(2, senhaAtual);
                if (usuario.executeUpdate() != 1) {
                    conexao.rollback();
                    return false;
                }

                conexao.commit();
                return true;
            } catch (SQLException erro) {
                conexao.rollback();
                throw erro;
            }
        }
    }
}

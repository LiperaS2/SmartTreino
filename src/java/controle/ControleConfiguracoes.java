package controle;

import dao.UsuarioDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ControleConfiguracoes", urlPatterns = {"/Configuracoes"})
public class ControleConfiguracoes extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = obterEmailSessao(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/index.html#login");
            return;
        }
        carregarConfiguracoes(request, response, email);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // A conta é identificada pela sessão, nunca por um e-mail enviado no formulário.
        String email = obterEmailSessao(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/index.html#login");
            return;
        }

        // Todas as alterações sensíveis pedem a senha atual antes de chegar ao DAO.
        String acao = valor(request, "acao");
        String senhaAtual = request.getParameter("senhaAtual");
        if (senhaAtual == null || senhaAtual.isEmpty()) {
            exibirErro(request, response, email, "Informe sua senha atual para confirmar.");
            return;
        }

        UsuarioDao dao = new UsuarioDao();
        try {
            if ("telefone".equals(acao)) {
                String telefone = valor(request, "telefone");
                if (telefone.isEmpty() || telefone.length() > 15) {
                    throw new IllegalArgumentException(
                            "Informe um telefone com até 15 caracteres.");
                }
                if (!dao.atualizarTelefone(email, senhaAtual, telefone)) {
                    throw new IllegalArgumentException("A senha atual está incorreta.");
                }
                carregarConfiguracoes(request, response, email,
                        "Telefone atualizado com sucesso.");
            } else if ("senha".equals(acao)) {
                String novaSenha = request.getParameter("novaSenha");
                String confirmarSenha = request.getParameter("confirmarSenha");
                if (novaSenha == null || novaSenha.length() < 8 || novaSenha.length() > 30) {
                    throw new IllegalArgumentException(
                            "A nova senha deve ter entre 8 e 30 caracteres.");
                }
                if (!novaSenha.equals(confirmarSenha)) {
                    throw new IllegalArgumentException("As senhas informadas não coincidem.");
                }
                if (!dao.atualizarSenha(email, senhaAtual, novaSenha)) {
                    throw new IllegalArgumentException("A senha atual está incorreta.");
                }
                carregarConfiguracoes(request, response, email,
                        "Senha atualizada com sucesso.");
            } else if ("excluir".equals(acao)) {
                if (!dao.excluirConta(email, senhaAtual)) {
                    throw new IllegalArgumentException("A senha atual está incorreta.");
                }
                // Ao remover a conta, a sessão deixa de representar um usuário válido.
                HttpSession sessao = request.getSession(false);
                if (sessao != null) {
                    sessao.invalidate();
                }
                request.setAttribute("mens", "Sua conta foi excluída com sucesso.");
                request.getRequestDispatcher("/mensagens.jsp").forward(request, response);
            } else {
                throw new IllegalArgumentException("Ação inválida.");
            }
        } catch (IllegalArgumentException erro) {
            exibirErro(request, response, email, erro.getMessage());
        } catch (ClassNotFoundException | SQLException erro) {
            getServletContext().log("Não foi possível atualizar as configurações da conta.", erro);
            exibirErro(request, response, email,
                    "Não foi possível concluir a solicitação. Tente novamente.");
        }
    }

    private String obterEmailSessao(HttpServletRequest request) {
        HttpSession sessao = request.getSession(false);
        return sessao == null ? null : (String) sessao.getAttribute("usuarioEmail");
    }

    private String valor(HttpServletRequest request, String nome) {
        String valor = request.getParameter(nome);
        return valor == null ? "" : valor.trim();
    }

    private void carregarConfiguracoes(HttpServletRequest request,
            HttpServletResponse response, String email)
            throws ServletException, IOException {
        carregarConfiguracoes(request, response, email, null);
    }

    private void carregarConfiguracoes(HttpServletRequest request,
            HttpServletResponse response, String email, String sucesso)
            throws ServletException, IOException {
        try {
            // O formulário mostra o telefone atual, a senha nunca é carregada para a tela.
            String telefone = new UsuarioDao().obterTelefone(email);
            if (telefone == null) {
                response.sendRedirect(request.getContextPath() + "/index.html#login");
                return;
            }
            request.setAttribute("telefone", telefone);
            request.setAttribute("sucesso", sucesso);
            request.getRequestDispatcher("/configuracoes.jsp").forward(request, response);
        } catch (ClassNotFoundException | SQLException erro) {
            getServletContext().log("Não foi possível carregar as configurações da conta.", erro);
            request.setAttribute("erro",
                    "Não foi possível carregar suas configurações. Tente novamente.");
            request.getRequestDispatcher("/configuracoes.jsp").forward(request, response);
        }
    }

    private void exibirErro(HttpServletRequest request, HttpServletResponse response,
            String email, String mensagem)
            throws ServletException, IOException {
        request.setAttribute("erro", mensagem);
        carregarConfiguracoes(request, response, email);
    }
}

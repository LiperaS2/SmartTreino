
package controle;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import dao.UsuarioDao;
import java.sql.SQLException;
import dao.Usuario;

@WebServlet(name = "ControleUsuario", urlPatterns = {"/ControleUsuario"})
public class ControleUsuario extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Define a codificação dos caracteres para aceitar acentos
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // Receber os dados enviados pelo form
        String origem, nome, telefone, email, senha, termos;
        int resultado;
        String mensagem = "";

        origem = request.getParameter("origem");

        // Verifica se a origem foi informada antes de comparar
        if (origem != null && origem.equalsIgnoreCase("cadastrar")) {

            nome = request.getParameter("nome");
            telefone = request.getParameter("telefone");
            email = request.getParameter("email");
            senha = request.getParameter("senha");
            termos = request.getParameter("termos");

            // Encapsular os dados em um objeto usuario
            Usuario usuario = new Usuario();
            usuario.setNome(nome);
            usuario.setTelefone(telefone);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setTermos(termos);

            // Chamar o metodo cadastrarUsuario da classe UsuarioDao
            UsuarioDao dao = new UsuarioDao();

            try {
                resultado = dao.cadastrarUsuario(usuario);

                if (resultado == 1) {
                    mensagem = "Usuário cadastrado com sucesso!";
                } else {
                    mensagem = "Usuário não cadastrado.";
                }

            } catch (ClassNotFoundException | SQLException erro) {

                // Verifica se o erro aconteceu por e-mail duplicado
                if (erro.getMessage() != null
                        && erro.getMessage().contains("Duplicate entry")) {

                    mensagem = "Este e-mail já está cadastrado.";

                } else {
                    // Exibe a mensagem de outro erro ocorrido
                    mensagem = "Erro ao cadastrar usuário: "
                            + erro.getMessage();
                }
            }

            // Envia a mensagem para o arquivo mensagens.jsp
            request.setAttribute("mens", mensagem);
            request.getRequestDispatcher("mensagens.jsp")
                    .forward(request, response);

        } else if (origem != null
                && origem.equalsIgnoreCase("logar")) {

            // Receber o email e a senha digitados
            email = request.getParameter("email");
            senha = request.getParameter("senha");

            // Faz o Login
            UsuarioDao dao = new UsuarioDao();

            try {
                // Chama o metodo logar para verificar as credenciais
                nome = dao.logar(email, senha);

                if (nome == null) {
                    // Se não encontrou o usuario
                    mensagem = "Usuário e/ou senha inválidos";

                    // Envia a mensagem de erro para mensagens.jsp
                    request.setAttribute("mens", mensagem);
                    request.getRequestDispatcher("mensagens.jsp")
                            .forward(request, response);

                } else {
                    // Se encontrou o usuario

                    // Inicia uma nova sessão autenticada e abre a dashboard.
                    HttpSession sessaoAnterior = request.getSession(false);
                    if (sessaoAnterior != null) {
                        // Encerra uma sessão anterior para renovar os dados de acesso.
                        sessaoAnterior.invalidate();
                    }
                    HttpSession sessao = request.getSession(true);
                    sessao.setAttribute("usuarioNome", nome);
                    sessao.setAttribute("usuarioEmail", email);
                    response.sendRedirect(request.getContextPath()
                            + "/ControleTreino");
                }

            } catch (ClassNotFoundException | SQLException erro) {

                // Guarda a mensagem caso ocorra um erro no banco de dados
                mensagem = "Erro ao realizar login: "
                        + erro.getMessage();

                // Envia a mensagem de erro para mensagens.jsp
                request.setAttribute("mens", mensagem);
                request.getRequestDispatcher("mensagens.jsp")
                        .forward(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Processa as requisições recebidas pelo método GET
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        // Processa as requisições recebidas pelo método POST
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        // Retorna a descrição do Servlet
        return "Controle de cadastro e login de usuários";
    }
}

package controle;

import dao.Treino;
import dao.TreinoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@WebServlet(name = "ControleTreino", urlPatterns = {"/ControleTreino"})
public class ControleTreino extends HttpServlet {

    // Valores permitidos para os campos selecionáveis do formulário.
    private static final Set<String> DIAS = Set.of(
            "Segunda-feira", "Terça-feira", "Quarta-feira",
            "Quinta-feira", "Sexta-feira", "Sábado", "Domingo");
    private static final Set<String> GRUPOS_MUSCULARES = Set.of(
            "Peito", "Costas", "Pernas", "Ombros", "Braços",
            "Abdômen", "Cardio", "Corpo inteiro");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Impede que usuários sem sessão autenticada acessem a dashboard.
        if (obterEmailSessao(request) == null) {
            response.sendRedirect(request.getContextPath() + "/index.html#login");
            return;
        }
        carregarDashboard(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String email = obterEmailSessao(request);
        if (email == null) {
            response.sendRedirect(request.getContextPath() + "/index.html#login");
            return;
        }

        String acao = request.getParameter("acao");
        if ("sair".equals(acao)) {
            // Encerra a sessão atual para finalizar o acesso à conta.
            HttpSession sessao = request.getSession(false);
            if (sessao != null) {
                sessao.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/index.html");
            return;
        }

        try {
            TreinoDao dao = new TreinoDao();
            if ("adicionar".equals(acao)) {
                // Valida os dados antes de gravar o treino para a conta atual.
                dao.adicionar(email, criarTreino(request));
            } else if ("remover".equals(acao)) {
                long id;
                try {
                    id = Long.parseLong(valor(request, "id"));
                } catch (NumberFormatException erro) {
                    throw new IllegalArgumentException("Selecione um treino válido.");
                }
                if (id < 1) {
                    throw new IllegalArgumentException("Selecione um treino válido.");
                }
                if (!dao.remover(email, id)) {
                    request.setAttribute("erro",
                            "Esse treino não foi encontrado na sua lista.");
                    carregarDashboard(request, response);
                    return;
                }
            } else {
                request.setAttribute("erro", "Ação inválida.");
                carregarDashboard(request, response);
                return;
            }
            // Redireciona após a alteração para evitar repetir o envio ao atualizar.
            response.sendRedirect(request.getContextPath() + "/ControleTreino");
        } catch (IllegalArgumentException erro) {
            request.setAttribute("erro", erro.getMessage());
            carregarDashboard(request, response);
        } catch (ClassNotFoundException | SQLException erro) {
            getServletContext().log("Não foi possível salvar a rotina de treinos.", erro);
            request.setAttribute("erro",
                    "Não foi possível atualizar seus treinos. Tente novamente.");
            carregarDashboard(request, response);
        }
    }

    private String obterEmailSessao(HttpServletRequest request) {
        // Recupera a conta da sessão sem criar uma sessão nova para visitantes.
        HttpSession sessao = request.getSession(false);
        return sessao == null ? null : (String) sessao.getAttribute("usuarioEmail");
    }

    private Treino criarTreino(HttpServletRequest request) {
        String nome = valor(request, "nome");
        String grupoMuscular = valor(request, "grupoMuscular");
        String diaSemana = valor(request, "diaSemana");
        String duracao = valor(request, "duracaoMinutos");
        String observacoes = valor(request, "observacoes");

        // Aplica no servidor os mesmos limites esperados pelos campos do formulário.
        if (nome.isEmpty() || nome.length() > 100) {
            throw new IllegalArgumentException(
                    "Informe o nome do treino (até 100 caracteres).");
        }
        if (!GRUPOS_MUSCULARES.contains(grupoMuscular)) {
            throw new IllegalArgumentException("Selecione um grupo muscular válido.");
        }
        if (!DIAS.contains(diaSemana)) {
            throw new IllegalArgumentException("Selecione um dia da semana válido.");
        }
        if (observacoes.length() > 500) {
            throw new IllegalArgumentException(
                    "As observações devem ter até 500 caracteres.");
        }

        int duracaoMinutos;
        try {
            duracaoMinutos = Integer.parseInt(duracao);
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(
                    "Informe a duração do treino em minutos.");
        }
        if (duracaoMinutos < 10 || duracaoMinutos > 300) {
            throw new IllegalArgumentException(
                    "A duração deve ficar entre 10 e 300 minutos.");
        }

        Treino treino = new Treino();
        treino.setNome(nome);
        treino.setGrupoMuscular(grupoMuscular);
        treino.setDiaSemana(diaSemana);
        treino.setDuracaoMinutos(duracaoMinutos);
        treino.setObservacoes(observacoes);
        return treino;
    }

    private String valor(HttpServletRequest request, String nome) {
        String valor = request.getParameter(nome);
        return valor == null ? "" : valor.trim();
    }

    private void carregarDashboard(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String email = obterEmailSessao(request);
        try {
            // Envia a lista e seu total para a página JSP montar a dashboard.
            List<Treino> treinos = new TreinoDao().listarPorUsuario(email);
            request.setAttribute("treinos", treinos);
            request.setAttribute("totalTreinos", treinos.size());
        } catch (ClassNotFoundException | SQLException erro) {
            getServletContext().log("Não foi possível carregar os treinos.", erro);
            request.setAttribute("treinos", Collections.emptyList());
            request.setAttribute("totalTreinos", 0);
            request.setAttribute("erro",
                    "Não foi possível carregar seus treinos. Verifique a conexão com o banco de dados.");
        }
        // Encaminha a requisição para exibir o formulário e os treinos cadastrados.
        request.getRequestDispatcher("/restrito.jsp").forward(request, response);
    }
}

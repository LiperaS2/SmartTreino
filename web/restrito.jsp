<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%
    // Evita exibir a dashboard sem uma sessão autenticada ou sem os dados carregados.
    if (session.getAttribute("usuarioEmail") == null) {
        response.sendRedirect(request.getContextPath() + "/index.html#login");
        return;
    }
    if (request.getAttribute("treinos") == null) {
        response.sendRedirect(request.getContextPath() + "/ControleTreino");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Organize sua rotina de treinos no SmartTreino.">
        <title>Minha rotina — SmartTreino</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="estilos.css">
    </head>
    <body class="dashboard-page">
        <header class="site-header">
            <div class="container header-content dashboard-header">
                <a class="logo" href="index.html" aria-label="SmartTreino — página inicial">
                    <span class="logo-mark" aria-hidden="true">S</span>
                    <span>smart<span class="logo-highlight">treino</span></span>
                </a>
                <div class="dashboard-account">
                    <span class="dashboard-greeting">Olá, <strong><c:out value="${sessionScope.usuarioNome}"/></strong></span>
                    <!-- Mantém o acesso às configurações junto das opções da conta. -->
                    <a class="settings-back" href="Configuracoes">Configurações</a>
                    <form action="ControleTreino" method="post">
                        <input type="hidden" name="acao" value="sair">
                        <button class="logout-button" type="submit">Sair</button>
                    </form>
                </div>
            </div>
        </header>

        <main class="dashboard-main">
            <div class="container">
                <!-- Mostra a saudação e o total de treinos da conta. -->
                <section class="dashboard-welcome" aria-labelledby="dashboard-title">
                    <div>
                        <p class="eyebrow"><span class="eyebrow-dot"></span> Seu espaço de evolução</p>
                        <h1 id="dashboard-title">Sua rotina, <span>do seu jeito.</span></h1>
                        <p>Planeje seus treinos e organize a semana no seu ritmo.</p>
                    </div>
                    <div class="dashboard-total" aria-label="Total de treinos cadastrados">
                        <span><c:out value="${totalTreinos}"/></span>
                        <small>treinos na rotina</small>
                    </div>
                </section>

                <c:if test="${not empty erro}">
                    <p class="dashboard-alert" role="alert"><c:out value="${erro}"/></p>
                </c:if>

                <div class="dashboard-layout">
                    <!-- Formulário para incluir um treino na rotina semanal. -->
                    <section class="dashboard-panel workout-form-panel" aria-labelledby="workout-form-title">
                        <div class="dashboard-panel-heading">
                            <span class="dashboard-panel-icon" aria-hidden="true">＋</span>
                            <div>
                                <p class="eyebrow">Planejamento</p>
                                <h2 id="workout-form-title">Adicionar treino</h2>
                            </div>
                        </div>
                        <form action="ControleTreino" method="post" class="workout-form">
                            <input type="hidden" name="acao" value="adicionar">
                            <div class="form-field">
                                <label for="nome">Nome do treino</label>
                                <input type="text" id="nome" name="nome" maxlength="100"
                                       placeholder="Ex.: Treino A — força" required>
                            </div>
                            <div class="form-field">
                                <label for="grupoMuscular">Grupo ou modalidade</label>
                                <select id="grupoMuscular" name="grupoMuscular" required>
                                    <option value="">Selecione</option>
                                    <option>Peito</option>
                                    <option>Costas</option>
                                    <option>Pernas</option>
                                    <option>Ombros</option>
                                    <option>Braços</option>
                                    <option>Abdômen</option>
                                    <option>Cardio</option>
                                    <option>Corpo inteiro</option>
                                </select>
                            </div>
                            <div class="workout-form-row">
                                <div class="form-field">
                                    <label for="diaSemana">Dia da semana</label>
                                    <select id="diaSemana" name="diaSemana" required>
                                        <option value="">Selecione</option>
                                        <option>Segunda-feira</option>
                                        <option>Terça-feira</option>
                                        <option>Quarta-feira</option>
                                        <option>Quinta-feira</option>
                                        <option>Sexta-feira</option>
                                        <option>Sábado</option>
                                        <option>Domingo</option>
                                    </select>
                                </div>
                                <div class="form-field">
                                    <label for="duracaoMinutos">Duração (min)</label>
                                    <input type="number" id="duracaoMinutos" name="duracaoMinutos"
                                           min="10" max="300" step="5" placeholder="60" required>
                                </div>
                            </div>
                            <div class="form-field">
                                <label for="observacoes">Observações <span>(opcional)</span></label>
                                <textarea id="observacoes" name="observacoes" maxlength="500"
                                          rows="3" placeholder="Ex.: foco em técnica, 3 séries..."></textarea>
                            </div>
                            <button class="button workout-submit" type="submit">
                                Salvar treino <span aria-hidden="true">→</span>
                            </button>
                        </form>
                    </section>

                    <!-- Lista os treinos carregados pelo controlador para esta conta. -->
                    <section class="dashboard-panel workout-list-panel" aria-labelledby="workout-list-title">
                        <div class="dashboard-panel-heading workout-list-heading">
                            <div>
                                <p class="eyebrow">Sua semana</p>
                                <h2 id="workout-list-title">Treinos planejados</h2>
                            </div>
                            <span class="workout-count"><c:out value="${totalTreinos}"/> registrados</span>
                        </div>

                        <c:choose>
                            <c:when test="${empty treinos}">
                                <div class="workout-empty">
                                    <span aria-hidden="true">◈</span>
                                    <h3>Sua rotina começa aqui</h3>
                                    <p>Adicione seu primeiro treino pelo formulário e ele aparecerá nesta lista.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <ul class="workout-list">
                                    <c:forEach items="${treinos}" var="treino">
                                        <li class="workout-item">
                                            <div class="workout-day"><c:out value="${treino.diaSemana}"/></div>
                                            <div class="workout-details">
                                                <h3><c:out value="${treino.nome}"/></h3>
                                                <p><c:out value="${treino.grupoMuscular}"/> <span aria-hidden="true">·</span> <c:out value="${treino.duracaoMinutos}"/> min</p>
                                                <c:if test="${not empty treino.observacoes}">
                                                    <p class="workout-notes"><c:out value="${treino.observacoes}"/></p>
                                                </c:if>
                                            </div>
                                            <form action="ControleTreino" method="post">
                                                <!-- O controlador também verifica que o treino pertence ao usuário. -->
                                                <input type="hidden" name="acao" value="remover">
                                                <input type="hidden" name="id" value="<c:out value='${treino.id}'/>">
                                                <button class="workout-remove" type="submit"
                                                        aria-label="Remover treino: <c:out value='${treino.nome}'/>">Remover</button>
                                            </form>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </c:otherwise>
                        </c:choose>
                    </section>
                </div>
            </div>
        </main>

        <footer class="site-footer">
            <div class="container footer-bottom dashboard-footer">
                <p>© 2026 SmartTreino. Todos os direitos reservados.</p>
                <p>Feito para a sua evolução <span aria-hidden="true">✦</span></p>
            </div>
        </footer>
    </body>
</html>

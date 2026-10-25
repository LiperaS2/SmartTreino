<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%
    // Esta página também pode ser acessada diretamente, então a sessão é conferida aqui.
    if (session.getAttribute("usuarioEmail") == null) {
        response.sendRedirect(request.getContextPath() + "/index.html#login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="Gerencie a senha, o telefone e a conta do SmartTreino.">
        <title>Configurações da conta — SmartTreino</title>
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
                    <a class="settings-back" href="ControleTreino">Minha rotina</a>
                    <span class="dashboard-greeting">Olá, <strong><c:out value="${sessionScope.usuarioNome}"/></strong></span>
                    <form action="ControleTreino" method="post">
                        <input type="hidden" name="acao" value="sair">
                        <button class="logout-button" type="submit">Sair</button>
                    </form>
                </div>
            </div>
        </header>

        <main class="dashboard-main">
            <div class="container settings-container">
                <section class="dashboard-welcome" aria-labelledby="settings-title">
                    <div>
                        <p class="eyebrow"><span class="eyebrow-dot"></span> Sua conta</p>
                        <h1 id="settings-title">Configurações</h1>
                        <p>Atualize seus dados de acesso e gerencie sua conta.</p>
                    </div>
                </section>

                <c:if test="${not empty erro}">
                    <p class="settings-alert settings-alert-error" role="alert"><c:out value="${erro}"/></p>
                </c:if>
                <c:if test="${not empty sucesso}">
                    <p class="settings-alert settings-alert-success" role="status"><c:out value="${sucesso}"/></p>
                </c:if>

                <!-- Cada formulário tem sua própria ação para atualizar somente o dado escolhido. -->
                <div class="settings-grid">
                    <section class="dashboard-panel settings-panel" aria-labelledby="phone-title">
                        <div class="dashboard-panel-heading">
                            <span class="dashboard-panel-icon" aria-hidden="true">☎</span>
                            <div>
                                <p class="eyebrow">Dados pessoais</p>
                                <h2 id="phone-title">Alterar telefone</h2>
                            </div>
                        </div>
                        <form action="Configuracoes" method="post" class="settings-form">
                            <input type="hidden" name="acao" value="telefone">
                            <div class="form-field">
                                <label for="telefone">Telefone</label>
                                <input type="tel" id="telefone" name="telefone" maxlength="15"
                                       autocomplete="tel" value="<c:out value='${telefone}'/>" required>
                            </div>
                            <div class="form-field">
                                <label for="senhaAtualTelefone">Senha atual</label>
                                <input type="password" id="senhaAtualTelefone" name="senhaAtual"
                                       autocomplete="current-password" required>
                            </div>
                            <button class="button settings-submit" type="submit">
                                Salvar telefone <span aria-hidden="true">→</span>
                            </button>
                        </form>
                    </section>

                    <section class="dashboard-panel settings-panel" aria-labelledby="password-title">
                        <div class="dashboard-panel-heading">
                            <span class="dashboard-panel-icon" aria-hidden="true">⌑</span>
                            <div>
                                <p class="eyebrow">Segurança</p>
                                <h2 id="password-title">Alterar senha</h2>
                            </div>
                        </div>
                        <form action="Configuracoes" method="post" class="settings-form">
                            <input type="hidden" name="acao" value="senha">
                            <div class="form-field">
                                <label for="senhaAtual">Senha atual</label>
                                <input type="password" id="senhaAtual" name="senhaAtual"
                                       autocomplete="current-password" required>
                            </div>
                            <div class="form-field">
                                <label for="novaSenha">Nova senha</label>
                                <input type="password" id="novaSenha" name="novaSenha"
                                       minlength="8" maxlength="30" autocomplete="new-password" required>
                            </div>
                            <div class="form-field">
                                <label for="confirmarSenha">Confirmar nova senha</label>
                                <input type="password" id="confirmarSenha" name="confirmarSenha"
                                       minlength="8" maxlength="30" autocomplete="new-password" required>
                            </div>
                            <button class="button settings-submit" type="submit">
                                Atualizar senha <span aria-hidden="true">→</span>
                            </button>
                        </form>
                    </section>
                </div>

                <section class="dashboard-panel settings-danger" aria-labelledby="delete-title">
                    <div>
                        <p class="eyebrow">Ação permanente</p>
                        <h2 id="delete-title">Excluir minha conta</h2>
                        <p>A exclusão é permanente e também remove os treinos vinculados à sua conta.</p>
                    </div>
                    <!-- A confirmação evita cliques acidentais; a senha continua sendo validada no servidor. -->
                    <form action="Configuracoes" method="post"
                          onsubmit="return confirm('Tem certeza que deseja excluir sua conta? Esta ação não pode ser desfeita.');">
                        <input type="hidden" name="acao" value="excluir">
                        <label class="visually-hidden" for="senhaAtualExclusao">Senha atual</label>
                        <input type="password" id="senhaAtualExclusao" name="senhaAtual"
                               placeholder="Confirme com sua senha atual"
                               autocomplete="current-password" required>
                        <button class="settings-delete" type="submit">Excluir conta</button>
                    </form>
                </section>
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

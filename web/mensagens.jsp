<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Retorno da solicitação de cadastro no SmartTreino.">
    <title>Retorno do cadastro — SmartTreino</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="estilos.css">
</head>
<body>
    <header class="site-header">
        <div class="container header-content">
            <a class="logo" href="index.html" aria-label="SmartTreino — página inicial">
                <span class="logo-mark" aria-hidden="true">S</span>
                <span>smart<span class="logo-highlight">treino</span></span>
            </a>
            <a class="button button-small header-cta" href="index.html">Voltar ao início <span aria-hidden="true">↗</span></a>
        </div>
    </header>

    <main class="message-main">
        <section class="message-section" aria-labelledby="message-title">
            <div class="container message-container">
                <div class="message-card">
                    <span class="message-icon" aria-hidden="true">✦</span>
                    <p class="eyebrow"><span class="eyebrow-dot"></span> Retorno do cadastro</p>
                    <h1 id="message-title">${mens}</h1>
                    <p class="message-description">Confira o resultado da sua solicitação e volte à página inicial para continuar sua jornada.</p>
                    <a class="button" href="index.html">Ir para a página inicial <span aria-hidden="true">→</span></a>
                </div>
                <span class="message-decoration" aria-hidden="true">ST</span>
            </div>
        </section>
    </main>

    <footer class="site-footer">
        <div class="container footer-main">
            <div class="footer-brand">
                <a class="logo" href="index.html" aria-label="SmartTreino — voltar ao início">
                    <span class="logo-mark" aria-hidden="true">S</span>
                    <span>smart<span class="logo-highlight">treino</span></span>
                </a>
                <p>Treinos com propósito.<br>Evolução no seu ritmo.</p>
            </div>
            <nav class="footer-nav" aria-label="Navegação do rodapé">
                <a href="index.html#inicio">Início</a>
                <a href="index.html#recursos">Recursos</a>
                <a href="index.html#sobre">Sobre</a>
                <a href="index.html#contato">Contato</a>
            </nav>
            <a class="footer-top" href="index.html#inicio">Voltar ao início <span aria-hidden="true">↑</span></a>
        </div>
        <div class="container footer-bottom">
            <p>© 2026 SmartTreino. Todos os direitos reservados.</p>
            <p>Feito para a sua evolução <span aria-hidden="true">✦</span></p>
        </div>
    </footer>
</body>
</html>

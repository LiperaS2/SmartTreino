-- Seleciona o banco de dados onde a tabela de treinos será criada.
USE SmartTreino;

-- Cria a tabela para armazenar os treinos planejados por cada usuário.
CREATE TABLE IF NOT EXISTS treino (
    -- Identificador único e gerado automaticamente para cada treino.
    id BIGINT NOT NULL AUTO_INCREMENT,
    -- E-mail da conta proprietária do treino.
    usuario_email VARCHAR(255) NOT NULL,
    -- Nome escolhido para identificar o treino.
    nome VARCHAR(100) NOT NULL,
    -- Grupo muscular ou modalidade trabalhada.
    grupo_muscular VARCHAR(60) NOT NULL,
    -- Dia da semana planejado para realizar o treino.
    dia_semana VARCHAR(20) NOT NULL,
    -- Duração prevista do treino em minutos.
    duracao_minutos INT NOT NULL,
    -- Detalhes adicionais, que podem ficar vazios.
    observacoes VARCHAR(500) NULL,
    -- Data e hora de criação, preenchidas automaticamente pelo banco.
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    -- Define o identificador do treino como chave primária da tabela.
    PRIMARY KEY (id),
    -- Acelera a busca e a ordenação de treinos por usuário e dia da semana.
    INDEX idx_treino_usuario_dia (usuario_email, dia_semana)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

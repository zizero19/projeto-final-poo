CREATE TABLE IF NOT EXISTS turma (
    id              BIGSERIAL PRIMARY KEY,
    nome_turma      VARCHAR(100) NOT NULL,
    qtd_alunos      INTEGER NOT NULL DEFAULT 0,
    turno           VARCHAR(20) NOT NULL,
    is_ativo        BOOLEAN NOT NULL DEFAULT TRUE,
    dias_aula       VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS cliente (
    id                 BIGSERIAL PRIMARY KEY,
    cpf                VARCHAR(14) NOT NULL UNIQUE,
    nome               VARCHAR(150) NOT NULL,
    email              VARCHAR(150),
    telefone           VARCHAR(20),
    turma_id           BIGINT REFERENCES turma(id) ON DELETE SET NULL,
    saldo_devedor      NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS produto (
    id              BIGSERIAL PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    categoria       VARCHAR(30) NOT NULL,
    preco           NUMERIC(10,2) NOT NULL,
    qtd_estoque     INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS caixa (
    id              BIGSERIAL PRIMARY KEY,
    total_vendas    NUMERIC(10,2) NOT NULL DEFAULT 0,
    is_aberto       BOOLEAN NOT NULL DEFAULT TRUE,
    abertura        TIMESTAMP NOT NULL,
    fechamento      TIMESTAMP
);

CREATE TABLE IF NOT EXISTS pedido (
    id              BIGSERIAL PRIMARY KEY,
    cliente_cpf     VARCHAR(14) REFERENCES cliente(cpf) ON DELETE SET NULL,
    caixa_id        BIGINT REFERENCES caixa(id) ON DELETE SET NULL,
    data_hora       TIMESTAMP NOT NULL,
    status          VARCHAR(30) NOT NULL,
    forma_pagamento VARCHAR(20),
    observacoes     TEXT,
    preco_total     NUMERIC(10,2) NOT NULL DEFAULT 0,
    valor_pago      NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS item_pedido (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    produto_id      BIGINT REFERENCES produto(id) ON DELETE SET NULL,
    nome_produto    VARCHAR(150),
    preco_unitario  NUMERIC(10,2),
    quantidade      INTEGER NOT NULL,
    subtotal        NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_pedido_cliente_cpf ON pedido(cliente_cpf);
CREATE INDEX IF NOT EXISTS idx_pedido_caixa_id ON pedido(caixa_id);
CREATE INDEX IF NOT EXISTS idx_item_pedido_pedido_id ON item_pedido(pedido_id);


-- Compatibilidade com bancos já criados antes da preservação histórica do ItemPedido.
ALTER TABLE item_pedido ALTER COLUMN produto_id DROP NOT NULL;
ALTER TABLE item_pedido ADD COLUMN IF NOT EXISTS nome_produto VARCHAR(150);
ALTER TABLE item_pedido ADD COLUMN IF NOT EXISTS preco_unitario NUMERIC(10,2);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = 'item_pedido'::regclass
          AND conname = 'item_pedido_produto_id_fkey'
    ) THEN
        ALTER TABLE item_pedido DROP CONSTRAINT item_pedido_produto_id_fkey;
    END IF;
END $$;

ALTER TABLE item_pedido
    ADD CONSTRAINT item_pedido_produto_id_fkey
    FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE SET NULL;

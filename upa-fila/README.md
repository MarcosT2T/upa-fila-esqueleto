# 🏥 [NOME DA APLICAÇÃO]

> ✏️ **Preencha TODOS os campos entre colchetes `[ ]` e apague estas linhas de instrução antes de entregar.**
> Este arquivo é **obrigatório** e faz parte da nota (ver barema).

**Disciplina:** Programação Orientada a Objetos — UniCEUB
**Professor:** Prof. Romes Heriberto
**Turma:** [turma]  |  **Data de entrega:** [dd/mm/aaaa]

---

## 👥 Integrantes

| Nome completo | Matrícula | Principais contribuições |
|---|---|---|
| [Nome do integrante 1] | [000000000] | [ex.: FilaPrioridade, testes de estrutura] |
| [Nome do integrante 2] | [000000000] | [ex.: ClassificadorManchester, MenuCli] |
| [Nome do integrante 3] | [000000000] | [...] |

> Trabalho individual? Mantenha só uma linha.

---

## 📋 Descrição da aplicação

[Em 1 ou 2 parágrafos: o que o sistema faz, para quem serve e qual problema resolve.
Ex.: "Sistema de linha de comando que gerencia o fluxo de pacientes de uma UPA 24h, desde a
recepção até a chamada médica, classificando o risco pelo Protocolo de Manchester simplificado..."]

### Funcionalidades

- [ ] RF01 — Cadastro de paciente com validação de CPF e geração de senha
- [ ] RF02 — Triagem com classificação de risco (5 cores)
- [ ] RF03 — Chamada do próximo paciente por prioridade, com painel de chamada
- [ ] RF04 — Visualização das filas de triagem e de atendimento
- [ ] RF05 — Busca de paciente por CPF e por senha (busca binária)
- [ ] RF06 — Relatórios: últimos atendimentos (pilha) e ordenado por espera (merge sort)
- [ ] RF07 — Estatísticas do plantão
- [ ] Bônus: [descreva, se fez]

> Marque com `[x]` o que está **funcionando**.

---

## ⚙️ Requisitos para executar

- **JDK 17 ou superior** (verifique com `java -version` e `javac -version`)
- **Maven 3.8+** (opcional — só necessário para rodar os testes e gerar o `.jar`)

---

## 📦 Instalação

```bash
# 1. Descompacte o arquivo .zip
# 2. Entre na pasta do projeto
cd [nome-da-pasta]
```

[Se o seu grupo usou alguma IDE, explique como importar: ex. "IntelliJ → File → Open → selecionar pom.xml → Open as Project".]

---

## ▶️ Como executar

**Opção A — sem Maven (só JDK):**

```bash
# Windows: dê dois cliques em executar.bat  (ou rode no terminal:)
executar.bat

# Linux / macOS:
./executar.sh
```

**Opção B — com Maven:**

```bash
mvn compile exec:java
```

**Opção C — gerar e rodar o .jar:**

```bash
mvn package
java -jar target/upa-fila-1.0.jar
```

---

## 🧪 Como rodar os testes

```bash
mvn test
```

Resultado obtido pelo grupo: **[__] testes, [__] passando, [__] falhando.**

---

## 🎬 Roteiro de demonstração

[Passo a passo para o professor testar rapidamente. Ex.:]

1. Opção 1 → cadastre "Ana", CPF `529.982.247-25`
2. Opção 1 → cadastre "Bruno", CPF `111.444.777-35`
3. Opção 2 → triagem da Ana com sinais normais → **AZUL**
4. Opção 2 → triagem do Bruno com SpO2 = 85 → **VERMELHO**
5. Opção 4 → Bruno aparece **na frente** da Ana, mesmo tendo chegado depois
6. Opção 3 → chama o Bruno; o painel mostra a senha e o consultório

---

## 🧩 Conceitos de POO aplicados

> Indique **onde** (classe e método) cada conceito aparece no **seu** código.

| Conceito | Onde aparece |
|---|---|
| Encapsulamento | [ex.: `Paciente` — status só muda via `registrarTriagem()`] |
| Herança | [...] |
| Classe abstrata | [...] |
| Interface | [...] |
| Polimorfismo | [...] |
| Composição | [...] |
| Exceções personalizadas | [...] |
| Recursão | [...] |
| Estruturas de dados | [...] |
| Padrões de projeto | [...] |
| Princípios SOLID | [...] |

---

## 🗂️ Estrutura do projeto

```
src/main/java/br/ceub/poo/upa/
├── App.java              → ponto de entrada (monta as dependências)
├── cli/                  → leitura do teclado e menus
├── modelo/               → entidades (Pessoa, Paciente, Medico, Triagem...)
├── triagem/              → classificação de risco (Strategy)
├── estrutura/            → Fila, FilaPrioridade, Pilha (implementadas à mão)
├── algoritmo/            → ordenação e busca
├── servico/              → regras de negócio (UpaService)
├── repositorio/          → armazenamento dos pacientes
├── observador/           → painel de chamada (Observer)
├── excecao/              → exceções personalizadas
└── util/                 → validação de CPF, gerador de senha
src/test/java/...         → testes JUnit 5
```

[Liste aqui as classes que o grupo **criou além** do esqueleto, se houver.]

---

## ⚠️ Limitações conhecidas

[Seja honesto: o que não funciona ou poderia melhorar. Declarar uma limitação vale mais do que escondê-la.]

---

## 🤖 Declaração de uso de ferramentas de IA

[Informe se usou ferramentas de IA (ChatGPT, Copilot, Claude, Gemini...), **para quê** e **em quais partes**.
Ex.: "Usamos o ChatGPT para entender o erro X e para revisar o método Y. Todo o código foi escrito e compreendido pelo grupo."
Se não usou, escreva "Não utilizamos".]

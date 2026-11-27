# UPA Fila — Sistema de Gestão de Fila de Pacientes

**Disciplina:** Programação Orientada a Objetos — UniCEUB
**Professor:** Prof. Romes Heriberto
**Turma:** Turma B - 0926 |  **Data de entrega:** 29/11/2026

---

## 👥 Integrantes

| Nome completo | Matrícula | Principais contribuições |
|---|---|--------------------------|
| Marcos Paulo dos Santos Júnior | 72650390 | Implementei a validação de CPF, a identificação de pacientes e profissionais, as transições de status do paciente e o cálculo da espera. Na Etapa 4, implementei a classificação de risco, os critérios de desempate entre pacientes, a fila de prioridade e a pilha. |
---

## 📋 Descrição da aplicação

UPA Fila é uma aplicação acadêmica de terminal desenvolvida em Java para simular o fluxo de pacientes em uma Unidade de Pronto Atendimento. O projeto representa a chegada, a triagem e a chamada médica. A triagem usa cinco níveis de risco definidos pelo protocolo didático da atividade.

A fila de atendimento considera primeiro a gravidade. Quando dois pacientes têm a mesma prioridade, a ordem depende do horário de chegada e, se esse horário também coincidir, do número da senha.

### Funcionalidades

- [X] RF01 — Cadastro de paciente com validação de CPF e geração de senha
- [X] RF02 — Triagem com classificação de risco (5 cores)
- [X] RF03 — Chamada do próximo paciente por prioridade, com painel de chamada
- [X] RF04 — Visualização das filas de triagem e de atendimento
- [X] RF05 — Busca de paciente por CPF e por senha (busca binária)
- [X] RF06 — Relatórios: últimos atendimentos (pilha) e ordenado por espera (merge sort)
- [X] RF07 — Estatísticas do plantão
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

Resultados por etapa, obtidos no IntelliJ com os testes fornecidos no projeto.

| Etapa | Teste | Resultado |
|---|---|---|
| 1 | `ValidadorCpfTest` | 5 de 5 aprovados |
| 3 | `PacienteTest.senhaFormatadaTemTresDigitos` | 1 de 1 aprovado |
| 4 | `ClassificadorManchesterSimplificadoTest` | 6 de 6 aprovados |
| 4 | `FilaPrioridadeTest` | 5 de 5 aprovados |
| 4 | `PilhaTest` | 2 de 2 aprovados |
| 5 | `BuscaTest` | 2 de 2 aprovados |
| 5 | `OrdenacaoTest` | 5 de 5 aprovados |
| 6 | `UpaServiceTest` | 6 de 6 aprovados |
| 6 | Suíte completa fornecida pelo professor | 33 de 33 aprovados |
| 7 | Demonstração manual da CLI | Opções 1 a 7 verificadas com dois pacientes. Conferi a chamada por prioridade, as buscas, os relatórios, as estatísticas e o retorno ao menu após entradas inválidas ou fila vazia. |
| 8 | `PacienteTest` e `SinaisVitaisTest` | 8 testes novos: atendimento antes da triagem, validação dos sinais vitais e aceitação da dor no limite superior. |
| 8 | Suíte completa | 41 de 41 aprovados: 33 testes originais e 8 acrescentados na Etapa 8. |
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



| Conceito | Onde aparece |
|---|---|
| Encapsulamento | `Paciente` mantém o status privado. `registrarTriagem()` e `marcarComoAtendido()` verificam o estado atual antes de alterá-lo. |
| Herança | `Paciente` estende `Pessoa`. `Medico` e `Enfermeiro` estendem `Profissional` e recebem nome e registro de conselho pela hierarquia. |
| Classe abstrata | `Pessoa` reúne os dados comuns e declara o método abstrato `getIdentificacao()`. |
| Interface | `Paciente` implementa `Comparable<Paciente>`. O método `compareTo()` define a ordem por prioridade, chegada e senha. |
| Polimorfismo | Na verificação manual da Etapa 3, a chamada `getIdentificacao()` por uma referência `Pessoa` produziu a identificação específica de paciente, médico e enfermeiro. |
| Composição | `Paciente` guarda a `Triagem` recebida em `registrarTriagem()`. Cada `Atendimento` associa um paciente ao médico que o chamou. |
| Exceções personalizadas | `FilaPrioridade.espiar()` e `Pilha.topo()` lançam `FilaVaziaException` quando a estrutura está vazia. |
| Estruturas de dados | `FilaPrioridade.enfileirar()` insere cada nó na posição ordenada. `Pilha.empilhar()` e `desempilhar()` operam no topo, de modo que o último elemento inserido é o primeiro a sair. |
| Recursão | `Busca.buscaBinariaRecursiva()` reduz a busca à metade que pode conter a chave. `Ordenacao.mergeSort()` ordena as duas metades recursivamente e `intercalar()` combina os resultados. |
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

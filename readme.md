# Caderno de Receitas — API REST com Mini Spring

Projeto desenvolvido para a avaliação da disciplina de **Programação 3**, ministrada pelo professor **Alisson Ferreira da Silva** no curso de **Sistemas de Informação (UNIFACOL)**.

A aplicação consiste em uma API REST em **Java 21** voltada ao gerenciamento de um **Caderno de Receitas** (Tema 3 sorteado), construída a partir do projeto-base [Mini Spring](https://www.google.com/search?q=https://github.com/ferreiralisson/mini-spring) e aplicando orientação a objetos, arquitetura em camadas, Inversão de Controle (IoC), Injeção de Dependências (DI) por construtor, reflexão e conceitos do protocolo HTTP.

## 1. Integrantes da Equipe e Detalhamento das Responsabilidades

| Integrante | Papel Principal                                                   | Responsabilidades Detalhadas no Projeto                                                                                                                                                                                                                                                                                                                                                                                              |
| :--- |:------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Vinicius de Sousa Silva** | **Model, DTO & Interface do Repositório**                         | Criação da entidade `ReceitaCulinaria` (com `id`, `nome`, `duracaoDoPreparoMinutos`, `Ingredientes` e `TutorialPreparo`) e do DTO de entrada `ReceitaCulinariaInput` (sem `id`, com `@NotBlank` e `@Positive`), além da definição da interface `ReceitaCulinariaRepository` com os contratos de persistência.                                                                                                                        |
| **Emanuel Vinícius de Souza** | **Implementação do Repositório em Memória & Concorrência**        | Implementação da classe `InMemoryReceitaCulinariaRepository` utilizando `ConcurrentHashMap`, garantindo a geração automática e sequencial de ID na criação (`AtomicLong`), a preservação do ID original na atualização (`computeIfPresent`) e a ordenação na listagem.                                                                                                                                                               |
| **Matheus Guilherme de Paula Santos** | **Service & Regra de Negócio Principal**                          | Implementação do `ReceitaCulinariaService` com injeção de `ReceitaCulinariaRepository` via construtor, validação de `duracaoDoPreparoMinutos` positiva (`> 0` → `400`), aplicação da regra de negócio do tema (limite máximo de **1.440 minutos** → `422`) e verificação de existência do registro por ID (`404`).                                                                                                                   |
| **Pedro Henrique Pereira da Silva** | **Validações Estruturais, Ingredientes & Filtro de Busca**        | Implementação no `ReceitaCulinariaService` das validações de campos textuais obrigatórios (`nome` e `TutorialPreparo` com `.isBlank()` → `400`), validação e higienização (`.trim()`) da lista de `Ingredientes` (mínimo de 1 item e sem itens em branco → `400`) e filtro de busca parcial por `nome` (*case-insensitive* via `.toLowerCase().contains(...)`), garantindo que operações rejeitadas não alterem os dados em memória. |
| **Tainon Coutinho** | **Controller & Mapeamento de Rotas HTTP**                         | Implementação do `ReceitaCulinariaControler` com injeção do `ReceitaCulinariaService` via construtor e mapeamento dos endpoints `/receitas`: `POST` (`201 Created` com cabeçalho `Location: /receitas/{id}`), `GET` geral e com filtro `?nome=` (`200 OK`), `GET /{id}` (`200 OK` / `404`), `PUT /{id}` (`200 OK`) e `DELETE /{id}` (`204 No Content`).                                                                              |
| **João Felipe Falcão de Lima Salsa** | **Tratamento de Erros HTTP, Conversão de Parâmetros & Bootstrap** | Implementação da validação e conversão segura do `@PathVariable("id")` (`parseId` lançando `400 Bad Request` para IDs não numéricos), integração das exceções do Service (`400`, `404`, `422`) com o `DispatcherServlet` e configuração da classe principal `Application.java` preservando o pacote `framework`.                                                                                                                     |
| **Igor Gustavo de Brito Rocha** | Documentação & Testes                                             | Configuração inicial do repositório a partir do projeto-base `mini-spring`, elaboração do arquivo `requests.http` cobrindo todos os cenários de sucesso (`201`, `200`, `204`) e erro (`400`, `404`, `405`, `415`, `422`) e redação final do `README.md`.                                                                                                                                                                             |

## 2. Descrição do Domínio (Tema 3 — Caderno de Receitas)

Conforme a especificação do tema sorteado (**Tema 3: Caderno de receitas — Receitas culinárias**), o sistema permite cadastrar, listar todas as receitas, filtrar por parte do nome (ignorando maiúsculas e minúsculas), consultar por identificador (`id`), atualizar preservando o ID original e excluir receitas culinárias, mantendo todos os registros em memória (`ConcurrentHashMap` e `AtomicLong`).

### Especificação do Tema Sorteado vs. Implementação no Projeto

| Item do Tema Sorteado | Especificação da Atividade | Implementação no Código (`br.com.minispring.CadernoDeReceitas`) |
| :--- | :--- | :--- |
| **Tema / Domínio** | 3 — Caderno de receitas (Receitas culinárias) | Entidade `ReceitaCulinaria` e DTO `ReceitaCulinariaInput` |
| **Rota Base** | `/receitas` | `@RestController("/receitas")` em `ReceitaCulinariaControler` |
| **Campo Textual Obrigatório** | `nome` | `@NotBlank String nome` (somado a `ArrayList<String> Ingredientes` e `@NotBlank String TutorialPreparo`) |
| **Campo Numérico Positivo** | `tempoPreparoMinutos` | `@Positive float duracaoDoPreparoMinutos` (`> 0`) |
| **Regra de Negócio (422)** | Máximo de **1.440 minutos** de preparo | Constante `DuracaoMaxima = 1440` validada em `ReceitaCulinariaService` |

### Modelos de Dados (`Records`)

A separação entre a entidade de domínio (`ReceitaCulinaria`) e o DTO de entrada (`ReceitaCulinariaInput`) impede que o cliente envie ou altere o `id` manualmente nas requisições `POST` e `PUT`:

| Classe (`Record`) | Finalidade | Campos |
| :--- | :--- | :--- |
| `ReceitaCulinaria` | Entidade principal armazenada e retornada nas respostas HTTP | `Long id`, `String nome`, `float duracaoDoPreparoMinutos`, `ArrayList<String> Ingredientes`, `String TutorialPreparo` |
| `ReceitaCulinariaInput` | DTO de entrada (`@RequestBody`) para cadastro e atualização | `@NotBlank String nome`, `@Positive float duracaoDoPreparoMinutos`, `ArrayList<String> Ingredientes`, `@NotBlank String TutorialPreparo` |

## 3. Regra de Negócio e Validações HTTP

Todas as validações estruturais (`400`) e a regra de negócio (`422`) são aplicadas de maneira idêntica tanto no cadastro (`POST /receitas`) quanto na atualização (`PUT /receitas/{id}`). Caso qualquer validação falhe, uma `HttpException` interrompe o fluxo imediatamente antes da chamada ao repositório, garantindo que **nenhuma operação rejeitada modifique os dados armazenados**.

| Regra / Validação | Critério Aplicado | Status HTTP | Mensagem Retornada |
| :--- | :--- | :--- | :--- |
| **Regra de Negócio (Tempo Máximo)** | `duracaoDoPreparoMinutos` não pode ultrapassar **1.440 minutos** (24 horas de preparo). | `422 Unprocessable Content` | `"Uma Receita pode ter no Máximo 1.440 Minutos!"` |
| **Nome obrigatório** | O campo `nome` não pode ser nulo, vazio (`""`) ou conter apenas espaços em branco. É salvo com `.trim()`. | `400 Bad Request` | `"Falha de validação"` (`"nome não pode estar em branco"`) |
| **Tutorial de preparo obrigatório** | O campo `TutorialPreparo` não pode ser nulo, vazio ou conter apenas espaços em branco. É salvo com `.trim()`. | `400 Bad Request` | `"Falha de validação"` (`"TutorialPreparo não pode estar em branco"`) |
| **Duração positiva** | O campo `duracaoDoPreparoMinutos` deve ser estritamente maior que zero (`> 0`). | `400 Bad Request` | `"Falha de validação"` (`"duracaoDoPreparoMinutos deve ser positivo"`) |
| **Ingredientes obrigatórios** | A lista `Ingredientes` deve ter pelo menos 1 item e nenhum item pode ser nulo ou em branco. Cada item recebe `.trim()`. | `400 Bad Request` | `"Ingredientes deve ter pelo menos um item!"` ou `"Ingredientes não pode ter item em branco!"` |
| **Validação de ID na URL** | O `{id}` informado na rota deve ser um número inteiro positivo (`> 0`). | `400 Bad Request` | `"O ID da receita deve ser um número inteiro (ex: /receitas/1)!"` ou `"ID inválido!"` |
| **Registro inexistente** | Consultas (`GET`), atualizações (`PUT`) e exclusões (`DELETE`) exigem que o `id` exista no repositório. | `404 Not Found` | `"Receita culinária não encontrada!"` |
| **Contrato JSON e mídia** | O corpo deve usar `Content-Type: application/json`, respeitar os tipos sem coerção escalar e ter até 1 MiB. | `400` / `413` / `415` | `"JSON inválido ou incompatível com os campos esperados"` / `"Use Content-Type: application/json"` |

## 4. Rotas da API (Endpoints)

Todas as rotas estão mapeadas sob `/receitas` na classe `ReceitaCulinariaControler`:

| Método | Rota | Descrição | Status de Sucesso | Status de Erro Possíveis |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/receitas` | Cadastra uma nova receita culinária gerando o `id` sequencial automaticamente. | `201 Created` (com cabeçalho `Location: /receitas/{id}`) | `400`, `413`, `415`, `422` |
| `GET` | `/receitas` | Lista todas as receitas cadastradas em ordem crescente de `id`. | `200 OK` | `405` |
| `GET` | `/receitas?nome={texto}` | Filtra receitas por parte do nome, desconsiderando maiúsculas e minúsculas (*case-insensitive*). | `200 OK` | `405` |
| `GET` | `/receitas/{id}` | Consulta uma receita culinária específica pelo seu `id`. | `200 OK` | `400`, `404`, `405` |
| `PUT` | `/receitas/{id}` | Atualiza todos os dados de uma receita existente preservando seu `id`. | `200 OK` | `400`, `404`, `405`, `413`, `415`, `422` |
| `DELETE` | `/receitas/{id}` | Exclui uma receita existente pelo seu `id`, sem corpo na resposta. | `204 No Content` | `400`, `404`, `405` |

### Exemplo de Corpo JSON (`POST /receitas` e `PUT /receitas/{id}`)

```json
{
  "nome": "Bolo de Cenoura com Cobertura de Chocolate",
  "duracaoDoPreparoMinutos": 50.0,
  "Ingredientes": [
    "3 cenouras médias",
    "4 ovos",
    "2 xícaras de açúcar",
    "2 e 1/2 xícaras de farinha de trigo",
    "1 colher de sopa de fermento em pó"
  ],
  "TutorialPreparo": "Bata as cenouras, os ovos e o óleo no liquidificador, misture os secos e asse a 180°C por 40 minutos."
}
```

### Exemplo de Resposta de Erro Padronizada (`400`, `404`, `405`, `415`, `422`, `500`)

```json
{
  "timestamp": "2026-10-26T14:00:00Z",
  "status": 422,
  "message": "Uma Receita pode ter no Máximo 1.440 Minutos!",
  "path": "/receitas",
  "details": [],
  "requestId": "c8f1d4e2-9a3b-4c12-8e7f-123456789abc"
}
```

## 5. Organização do Projeto, Arquitetura em Camadas, IoC/DI e Caminho da Requisição

### 5.1 Responsabilidades das Camadas

O pacote `br.com.minispring.framework` original foi preservado integralmente, e as classes do domínio foram organizadas em `br.com.minispring.CadernoDeReceitas`:

| Camada | Classe / Interface | Responsabilidade |
| :--- | :--- | :--- |
| **Controller** | `ReceitaCulinariaControler` (`@RestController("/receitas")`) | Camada de apresentação HTTP: recebe parâmetros extraídos da requisição (`@RequestBody`, `@PathVariable`, `@RequestParam`), converte o ID textual da rota, delega o processamento ao `Service` e define o `HttpResult` (`201` com `Location`, `200` ou `204`). |
| **Service** | `ReceitaCulinariaService` (`@Service`) | Camada de negócio: aplica todas as validações de domínio (`400`) e a regra de negócio de 1.440 minutos (`422`), higieniza os textos com `.trim()`, filtra por nome e lança `404` quando um registro não existe. Depende da interface `ReceitaCulinariaRepository`. |
| **Repository** | `ReceitaCulinariaRepository` e `InMemoryReceitaCulinariaRepository` (`@Repository`) | Camada de dados em memória: define o contrato na interface e armazena as receitas em um `ConcurrentHashMap<Long, ReceitaCulinaria>`, gerando IDs únicos com `AtomicLong.incrementAndGet()` e preservando o ID nas atualizações com `computeIfPresent`. |

### 5.2 Inversão de Controle (IoC) e Injeção de Dependências (DI) por Construtor

1. **Varredura de Componentes (`ComponentScanner`):** Ao iniciar em `Application.main`, `MiniApplication.run(Application.class)` escaneia o pacote `br.com.minispring.CadernoDeReceitas` e localiza todas as classes concretas anotadas com `@Component` ou suas meta-anotações (`@RestController`, `@Service`, `@Repository`).
2. **Container de IoC (`ApplicationContext`):** O framework assume o controle da criação dos objetos (IoC), instanciando um único objeto (*singleton* por contexto) para cada componente na inicialização.
3. **Resolução e Injeção pelo Construtor (DI):**
   * Para criar `ReceitaCulinariaControler`, o container inspeciona seu único construtor público via Reflection e vê que ele exige `ReceitaCulinariaService`.
   * Para criar `ReceitaCulinariaService`, o container vê que seu construtor exige a interface `ReceitaCulinariaRepository`, busca a única classe concreta atribuível a ela (`InMemoryReceitaCulinariaRepository`), instancia o repositório primeiro e o injeta no construtor do `Service`, que em seguida é injetado no construtor do `Controller`.

### 5.3 Caminho Completo de uma Requisição HTTP

1. **Jetty e `DispatcherServlet`:** O servidor Jetty recebe a chamada HTTP em `127.0.0.1:8080` e entrega `HttpServletRequest` e `HttpServletResponse` ao Front Controller (`DispatcherServlet`).
2. **Interceptação (`LoggingInterceptor.before`):** Gera um `UUID` para a requisição (`requestId`), grava o cabeçalho `X-Request-Id` na resposta e registra o instante inicial (`System.nanoTime()`).
3. **Roteamento (`Router.match`):** Compara verbo HTTP e URL com as expressões regulares registradas no bootstrap. Retorna `404` se a rota não existir ou `405` (com header `Allow`) se o método HTTP não for suportado naquela rota.
4. **Binding e Validação (`DispatcherServlet.bind`):** Extrai variáveis da rota (`@PathVariable`), parâmetros de consulta (`@RequestParam`) ou desserializa o corpo JSON (`@RequestBody`) com `Jackson ObjectMapper` para `ReceitaCulinariaInput`, acionando o `Validator` (`@NotBlank` e `@Positive`).
5. **Execução nas Camadas (`Controller` → `Service` → `Repository`):** O método do controller é invocado por reflection (`Method.invoke`), chama o `ReceitaCulinariaService` para validar a lista de ingredientes e a regra de 1.440 minutos, e persiste ou consulta no `InMemoryReceitaCulinariaRepository`.
6. **Serialização da Resposta e Log (`write` e `LoggingInterceptor.after`):** O resultado (`HttpResult`) ou exceção (`HttpException`) é serializado em JSON UTF-8 com o status HTTP correspondente (`200`, `201`, `204`, `400`, `404`, `405`, `415`, `422`, `500`), e o interceptor registra no log o tempo total em milissegundos.

## 6. Estrutura de Diretórios

```text
CadernoDeReceitas-Project-P3/
├── docs/
│   ├── ARQUITETURA.md
│   └── CONCEITOS-E-REFERENCIAS.md
├── src/
│   ├── main/
│   │   ├── java/br/com/minispring/
│   │   │   ├── CadernoDeReceitas/
│   │   │   │   ├── Application.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── ReceitaCulinariaControler.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── ReceitaCulinaria.java
│   │   │   │   │   └── ReceitaCulinariaInput.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── ReceitaCulinariaRepository.java
│   │   │   │   │   └── InMemoryReceitaCulinariaRepository.java
│   │   │   │   └── service/
│   │   │   │       └── ReceitaCulinariaService.java
│   │   │   └── framework/
│   │   │       ├── MiniApplication.java
│   │   │       ├── annotation/
│   │   │       ├── context/
│   │   │       ├── validation/
│   │   │       └── web/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/br/com/minispring/
├── pom.xml
├── requests.http
└── README.md
```

## 7. Instruções de Execução e Testes (`requests.http`)

### 7.1 Pré-requisitos

* **Java 21** (JDK 21)
* **Apache Maven 3.8+**

### 7.2 Como Compilar e Executar a API

No terminal, na pasta raiz do projeto (onde está o `pom.xml`), execute:

```bash
# 1. Compilar o projeto e empacotar o JAR executável
mvn clean package -DskipTests

# 2. Iniciar o servidor Jetty na porta 8080
java -jar target/mini-spring-1.0.0.jar
```

Saída esperada no terminal ao iniciar:

```text
Mini Spring em http://127.0.0.1:8080
  DELETE /receitas/{id}
  GET /receitas/{id}
  PUT /receitas/{id}
  GET /receitas
  POST /receitas
```

### 7.3 Como Testar Tudo com o Arquivo `requests.http`

O repositório acompanha o arquivo `requests.http` com todos os exemplos de sucesso e erro exigidos na entrega:

* **Exemplos de Sucesso (`201`, `200`, `204`):**
  * Cadastro de receitas válidas e no limite exato de `1440.0` minutos (`POST /receitas` → `201 Created` + `Location`);
  * Listagem geral (`GET /receitas` → `200 OK`) e filtro parcial por nome ignorando maiúsculas/minúsculas (`GET /receitas?nome=bolo` → `200 OK`);
  * Consulta por ID (`GET /receitas/1` → `200 OK`);
  * Atualização preservando o ID (`PUT /receitas/1` → `200 OK`);
  * Exclusão de registro (`DELETE /receitas/2` → `204 No Content`).
* **Exemplos de Erro (`400`, `404`, `405`, `415`, `422`):**
  * `400 Bad Request`: `nome` em branco, `TutorialPreparo` em branco, `duracaoDoPreparoMinutos` igual a `0` ou negativa, lista de `Ingredientes` vazia ou com item em branco, ID não numérico (`/receitas/abc`) ou `<= 0` (`/receitas/0`), JSON malformado, tipo incompatível ou propriedade desconhecida;
  * `422 Unprocessable Content`: violação da regra de negócio no `POST` e no `PUT` com `duracaoDoPreparoMinutos` acima de `1440` (ex.: `1441.0` e `2000.0`);
  * `404 Not Found`: `GET`, `PUT` e `DELETE` para ID inexistente (`/receitas/9999`) e rota inexistente;
  * `405 Method Not Allowed`: verbo `PATCH /receitas/1` retornando o cabeçalho `Allow: DELETE, GET, PUT`;
  * `415 Unsupported Media Type`: envio com `Content-Type: text/plain`.

## 8. Declaração de Uso de Inteligência Artificial

Em conformidade com as regras da avaliação, declaramos que ferramentas de Inteligência Artificial Generativa foram utilizadas como **apoio** para:


* Estruturação e revisão da documentação deste `README.md` de acordo com os requisitos da atividade;
* Revisão das validações e respostas HTTP do domínio.
* O projeto foi desenvolvido com o apoio de ferramentas de Inteligência Artificial (IA) para esclarecimento de dúvidas, auxílio nos estudos e compreensão do código, servindo como suporte ao processo de aprendizagem e desenvolvimento, sem substituir o estudo e o entendimento dos conceitos utilizados.

Todos os integrantes do grupo revisaram e compreendem integralmente o código entregue, a divisão de responsabilidades em camadas, a injeção de dependências e o caminho percorrido por cada requisição HTTP.
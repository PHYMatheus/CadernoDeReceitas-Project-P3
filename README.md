# Caderno de Receitas: Aplicação Web com Framework de Injeção de Dependência (Mini Spring)

Projeto desenvolvido para a disciplina de **Programação 3**, do curso de **Sistemas de Informação**.

O sistema consiste em um caderno de receitas digital implementado em **Java**, executado sobre um framework próprio e simplificado inspirado no Spring (denominado **Mini Spring**), que implementa Inversão de Controle (IoC), Injeção de Dependência (DI) e mapeamento de rotas HTTP por meio de anotações.

---

## Sumário

1. [Contextualização e problema](#1-contextualização-e-problema)
2. [Objetivos](#2-objetivos)
3. [Requisitos funcionais](#3-requisitos-funcionais)
4. [Arquitetura da solução](#4-arquitetura-da-solução)
5. [O framework Mini Spring](#5-o-framework-mini-spring)
6. [Estrutura do projeto](#6-estrutura-do-projeto)
7. [Modelo de dados](#7-modelo-de-dados)
8. [Endpoints da API](#8-endpoints-da-api)
9. [Instruções de execução](#9-instruções-de-execução)
10. [Tecnologias utilizadas](#10-tecnologias-utilizadas)
11. [Conceitos de Programação 3 aplicados](#11-conceitos-de-programação-3-aplicados)
12. [Trabalhos futuros](#12-trabalhos-futuros)
13. [Equipe](#13-equipe)

---

## 1. Contextualização e problema

Receitas culinárias costumam ser registradas de forma dispersa e pouco confiável: cadernos físicos suscetíveis a danos, capturas de tela em aplicativos de mensagens, páginas de navegador salvas temporariamente ou simplesmente memorizadas por quem as prepara. Essa fragmentação gera dificuldades recorrentes aos usuários:

| Dificuldade do usuário | Necessidade identificada |
|---|---|
| Perda ou dificuldade de localizar receitas | Repositório único, organizado e persistente |
| Impossibilidade de consultar receitas a partir dos ingredientes disponíveis | Busca por ingrediente, além da busca por nome |
| Necessidade de adaptar quantidades para diferentes números de pessoas | Recálculo automático de porções |
| Dificuldade em reencontrar receitas preferidas | Marcação de favoritas e organização por categoria |
| Compartilhamento informal e desestruturado | Padronização das informações de cada receita |

Dessa forma, a necessidade central do usuário é **armazenar, localizar e reaproveitar receitas de maneira simples e confiável**. O sistema proposto foi concebido a partir dessa necessidade, e as decisões de arquitetura descritas neste documento visam atendê-la com clareza e facilidade de evolução.

---

## 2. Objetivos

### 2.1 Objetivo geral

Desenvolver uma aplicação de gerenciamento de receitas culinárias, exposta por meio de API REST, executada sobre um framework próprio que reproduz os princípios fundamentais do Spring.

### 2.2 Objetivos específicos

- Implementar um contêiner de IoC capaz de instanciar e gerenciar componentes automaticamente.
- Implementar injeção de dependência por anotação (`@Autowired`).
- Implementar roteamento de requisições HTTP por anotações (`@GetMapping`, `@PostMapping` etc.).
- Organizar a aplicação em camadas com responsabilidades bem definidas.
- Aplicar, na prática, os conceitos de reflexão, anotações customizadas e programação orientada a interfaces.

---

## 3. Requisitos funcionais

| Código | Requisito |
|---|---|
| RF01 | O sistema deve permitir cadastrar, consultar, atualizar e remover receitas. |
| RF02 | O sistema deve permitir listar todas as receitas cadastradas. |
| RF03 | O sistema deve permitir buscar receitas por nome, categoria ou ingrediente. |
| RF04 | O sistema deve permitir recalcular as quantidades de uma receita para um número de porções informado. |
| RF05 | O sistema deve permitir marcar e desmarcar receitas como favoritas. |
| RF06 | O sistema deve organizar as receitas por categorias. |

---

## 4. Arquitetura da solução

A aplicação adota **arquitetura em camadas**, de modo que cada camada possua uma única responsabilidade e se comunique apenas com a camada imediatamente inferior. Essa decisão favorece a manutenção, a testabilidade e a substituição de componentes sem impacto nas demais partes do sistema (por exemplo, a troca do mecanismo de persistência).

```
        Cliente HTTP (navegador / Postman / aplicação)
                          │
                          ▼
┌────────────────────────────────────────────────────┐
│                    MINI SPRING                     │
│                                                    │
│   Dispatcher (roteador)      Contêiner IoC         │
│   Interpreta @XxxMapping     Instancia e injeta    │
│                              os beans              │
└───────────────────────┬────────────────────────────┘
                        ▼
              ┌───────────────────┐
              │ ReceitaController │  Camada de apresentação (HTTP)
              └─────────┬─────────┘
                        ▼
              ┌───────────────────┐
              │  ReceitaService   │  Camada de regras de negócio
              └─────────┬─────────┘
                        ▼
              ┌───────────────────┐
              │ ReceitaRepository │  Camada de acesso a dados
              └─────────┬─────────┘
                        ▼
              ┌───────────────────┐
              │       Model       │  Entidades de domínio
              └───────────────────┘
```

### 4.1 Responsabilidades das camadas

| Camada | Responsabilidade |
|---|---|
| **Controller** | Receber as requisições HTTP, delegar o processamento e devolver a resposta. Não contém regras de negócio. |
| **Service** | Concentrar as regras de negócio, como o recálculo de porções e a busca por ingrediente. |
| **Repository** | Abstrair o armazenamento dos dados. A implementação inicial é em memória, podendo ser substituída por arquivo ou banco de dados. |
| **Model** | Representar as entidades do domínio (`Receita`, `Ingrediente`, `Categoria`). |

### 4.2 Decisões de projeto

- **Baixo acoplamento:** as camadas dependem de abstrações, e as dependências são fornecidas pelo contêiner, não instanciadas manualmente.
- **Substituibilidade da persistência:** o `Repository` isola o mecanismo de armazenamento, permitindo evolução sem alterações em `Service` ou `Controller`.
- **Testabilidade:** a injeção de dependência facilita a substituição de componentes por implementações de teste.

---

## 5. O framework Mini Spring

O Mini Spring foi desenvolvido com a **API de Reflection** do Java e reproduz, de forma simplificada, os principais mecanismos do Spring Framework.

### 5.1 Anotações disponíveis

| Anotação | Finalidade |
|---|---|
| `@Component` | Registra a classe como bean gerenciado pelo contêiner. |
| `@Service` | Especialização de `@Component` para classes de regra de negócio. |
| `@Repository` | Especialização de `@Component` para classes de acesso a dados. |
| `@Controller` | Identifica classes que tratam requisições HTTP. |
| `@Autowired` | Solicita ao contêiner a injeção de uma dependência. |
| `@GetMapping` | Associa um método ao verbo HTTP `GET` em um caminho. |
| `@PostMapping` | Associa um método ao verbo HTTP `POST` em um caminho. |
| `@PutMapping` | Associa um método ao verbo HTTP `PUT` em um caminho. |
| `@DeleteMapping` | Associa um método ao verbo HTTP `DELETE` em um caminho. |
| `@PathVariable` | Extrai valores variáveis da URL (ex.: `/receitas/{id}`). |
| `@RequestBody` | Converte o corpo JSON da requisição em objeto Java. |

### 5.2 Funcionamento

1. **Varredura (scan):** ao ser iniciado, o framework percorre o pacote da aplicação em busca de classes anotadas.
2. **Registro:** cada classe identificada é instanciada e armazenada como *bean* no contêiner.
3. **Injeção de dependências:** para cada campo anotado com `@Autowired`, o contêiner localiza o bean correspondente e o injeta por reflexão.
4. **Mapeamento de rotas:** os métodos anotados com `@XxxMapping` são registrados em uma tabela associando verbo HTTP e caminho ao método Java correspondente.
5. **Atendimento de requisições:** o servidor HTTP recebe a requisição, o Dispatcher consulta a tabela de rotas e invoca o método adequado.

---

## 6. Estrutura do projeto

```
caderno-receitas/
├── src/main/java/br/edu/caderno/
│   ├── Main.java
│   ├── minispring/                  # Framework
│   │   ├── annotations/             # Anotações customizadas
│   │   ├── core/                    # Contêiner IoC e scanner de classes
│   │   └── web/                     # Dispatcher, servidor HTTP e JSON
│   └── caderno/                     # Aplicação
│       ├── controller/              # ReceitaController
│       ├── service/                 # ReceitaService
│       ├── repository/              # ReceitaRepository
│       └── model/                   # Receita, Ingrediente, Categoria
├── src/test/java/                   # Testes automatizados
├── pom.xml
└── README.md
```

---

## 7. Modelo de dados

```
Receita
├── id            : Long
├── nome          : String
├── categoria     : Categoria
├── porcoes       : int
├── tempoPreparo  : int          (minutos)
├── ingredientes  : List<Ingrediente>
├── modoPreparo   : List<String>
└── favorita      : boolean

Ingrediente
├── nome          : String
├── quantidade    : double
└── unidade       : String       (g, ml, xícara, colher, etc.)
```

---

## 8. Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/receitas` | Lista todas as receitas. |
| `GET` | `/receitas/{id}` | Retorna os detalhes de uma receita. |
| `POST` | `/receitas` | Cadastra uma nova receita. |
| `PUT` | `/receitas/{id}` | Atualiza uma receita existente. |
| `DELETE` | `/receitas/{id}` | Remove uma receita. |
| `GET` | `/receitas/busca?ingrediente={nome}` | Busca receitas por ingrediente. |
| `GET` | `/receitas/{id}/porcoes/{n}` | Retorna a receita recalculada para `n` porções. |
| `PUT` | `/receitas/{id}/favorita` | Alterna o estado de favorita. |

### 8.1 Exemplo de requisição

```http
POST /receitas
Content-Type: application/json

{
  "nome": "Brigadeiro de Colher",
  "categoria": "DOCES",
  "porcoes": 4,
  "tempoPreparo": 15,
  "ingredientes": [
    { "nome": "leite condensado", "quantidade": 1, "unidade": "lata" },
    { "nome": "chocolate em pó",  "quantidade": 3, "unidade": "colher" },
    { "nome": "manteiga",         "quantidade": 1, "unidade": "colher" }
  ],
  "modoPreparo": [
    "Misturar todos os ingredientes em uma panela.",
    "Cozinhar em fogo baixo, mexendo continuamente, até desgrudar do fundo.",
    "Servir em recipiente individual."
  ]
}
```

### 8.2 Exemplo de recálculo de porções

```http
GET /receitas/1/porcoes/12
```

A resposta contém a receita com as quantidades dos ingredientes proporcionalmente ajustadas para 12 porções.

---

## 9. Instruções de execução

### 9.1 Pré-requisitos

- Java 17 ou superior
- Maven 3.8 ou superior

### 9.2 Procedimento

```bash
# 1. Clonar o repositório
git clone <url-do-repositorio>
cd caderno-receitas

# 2. Compilar e empacotar
mvn clean package

# 3. Executar
java -jar target/caderno-receitas.jar
```

Após a inicialização, o console exibirá informações semelhantes às seguintes:

```
Mini Spring iniciado.
Beans registrados: 3
Rotas mapeadas: 8
Servidor disponível em http://localhost:8080
```

A API poderá ser acessada em `http://localhost:8080/receitas`.

### 9.3 Execução dos testes

```bash
mvn test
```

---

## 10. Tecnologias utilizadas

- **Java 21** (reflexão, anotações, streams, records)
- **Maven** (gerenciamento de dependências e build)
- **com.sun.net.httpserver** (servidor HTTP embutido no JDK)
- **Gson / Jackson** (serialização e desserialização JSON)
- **JUnit 5** (testes automatizados)

---

## 11. Conceitos de Programação 3 aplicados

- Anotações customizadas (`@interface`) e suas políticas de retenção
- API de Reflection
- Inversão de Controle (IoC) e Injeção de Dependência (DI)
- Arquitetura em camadas
- Programação orientada a interfaces
- Coleções e Streams
- Tratamento de exceções
- Testes unitários

---

## 12. Trabalhos futuros

- Persistência em banco de dados relacional, mantendo a interface do `Repository`
- Armazenamento de imagens das receitas
- Geração de lista de compras a partir de receitas selecionadas
- Sugestão de receitas com base nos ingredientes disponíveis
- Autenticação e cadastro de usuários, com cadernos individuais
- Exportação de receitas em PDF

---

## 13. Equipe

| Nome     | Responsabilidade              |
|----------|-------------------------------|
| Matheus  | Desenvolvimento e arquitetura |
| Tainon   | Desenvolvimento e arquitetura |
| Vinicius | Desenvolvimento e arquitetur  |
| Igor     | Desenvolvimento e arquitetura |

**Disciplina:** Programação 3
**Curso:** Sistemas de Informação
**Professor(a):** *Alisson Ferreira*
**Instituição:** *Unifacol
**Período:** *(4º Período)*

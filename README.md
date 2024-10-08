<h1 align="center">
  Trivia API
</h1>

<p align="center"><em> ⚠️ Em desenvolvimento! ⚠️ </em></p>

<!--toc:start-->

- [Introdução](#introdução)
- [Instalação](#instalação)
- [Configuração](#configuração)

<!--toc:end-->

## Introdução

Esta é uma API simples feita como exercício para deploy com Railway. Não há muito para ser visto por enquanto, a API
permite com que Questões sejam adicionadas e questões podem estar enquadradas em diversas categorias. Por hora ainda
não foi implementada uma forma de realizar um <em>quiz</em> e checar se uma pergunta esta correta pela API ou de obter um
questionário que possa ter uma nota associada ao mesmo.


## Instalação

1. Clone o repositório git para sua maquina.

```
git clone https://github.com/PauloRPA/Trivia-API
```

2. Entre na pasta do repositório e rode o comando `mvn spring-boot:run`.
3. Espere o processo de build e após o seu fim a aplicação estará disponível na porta `9998`.

A aplicação usará por padrão o perfil "dev". Nesse perfil o banco de dados a será usado o banco de dados H2 em memória
e devido a isso não é necessário configurar as variáveis de ambiente para o banco de dados.
Para que a aplicação possa ser testada com mais facilidade o swagger (OpenAPI) esta configurado para responder
a requisições no caminho root (/), sendo assim, basta acessar http://localhost:9998 para acessar o Swagger.
Caso deseje autopreencher o banco de dados com valores padrão, veja [Variáveis de ambiente](#variáveis-de-ambiente).

Caso o maven não esteja instalado na sua máquina, é possível usar o wrapper 'mvnw' disponível na pasta root do
repositório.
Use então: `./mvnw spring-boot:run` ou `./mvnw.bat spring-boot:run` caso esteja usando Windows.

## Configuração

No perfil "dev" (padrão) não é necessário configurar o banco de dados. No perfil "dio" é possível configurar o banco
de dados postgres. O perfil "dio" foi feito com o intuíto de ser usado para deploy de teste no Railway.

### Variáveis de ambiente

- `application.data.fill.category`: (boolean) Inserir dados na tabela de Category no momento em que a aplicação for
  iniciada.
- `spring_profiles_active`: (string) Perfil no qual a aplicação deverá rodar.

- `PG_USER`: (string) Usuário do banco de dados.
- `PG_PASSWORD`: (string) Senha do banco de dados.
- `PG_HOST`: (string) Host do banco de dados.
- `PG_PORT`: (int) Porta do banco de dados.
- `PG_DATABASE`: (string) Banco de dados.
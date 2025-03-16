# Tech Challenge - Fase 5

Projeto realizado como atividade avaliativa do curso de **Software Architecture - Pós-Tech - FIAP**.

Link do projeto no GitHub:

- Microsserviço de PROCESSAMENTO: https://github.com/efrancodelima/pedido
- Microsserviço de NOTIFICACAO: https://github.com/efrancodelima/pagamento

Link do vídeo demonstrando o projeto em execução:

- https://youtu.be/C-mozV9B57o (funcionamento da aplicação e processo de deploy)

# Índice

- [Objetivos](#1-objetivos)
- [Requisitos](#2-requisitos)
  - [Arquitetura](#21-arquitetura)
  - [Banco de dados](#22-banco-de-dados)
  - [Testes](#23-testes)
  - [Pipeline](#24-pipeline)
  - [API Web](#25-api-web)
- [Evidências dos testes](#3-evidências-dos-testes)
- [Comentários sobre o projeto](#4-comentários-sobre-o-projeto)
  - [Microsserviços](#41-microsserviços)
  - [Banco de dados](#42-banco-de-dados)
  - [Testes](#43-testes)
  - [Pipeline](#44-pipeline)
- [Instrução para rodar a aplicação](#5-instrução-para-rodar-a-aplicação)

## 1. Objetivos

Desenvolver um sistema para uma lanchonete local em fase de expansão. O sistema deverá realizar o controle dos pedidos, além de outras funções correlatas, conforme especificado no Tech Challenge.

## 2. Requisitos

### 2.1 Processamento assíncrono

"A nova versão do sistema deve processar mais de um vídeo ao mesmo tempo."

O microsserviço de processamento possui um endpoint para receber um ou mais vídeos.

Deixamos a cargo do cliente da aplicação escolher se deseja enviar vários vídeos em uma requisição só ou em requisições separadas. Em qualquer caso, os vídeos serão processados simultaneamente, em diferentes threads.

Se vier um vídeo por requisição e houver várias requisições simultâneas, o processamento dos vídeos também será simultâneo, pois esse já é o comportamento padrão de um microsserviço Java/Spring: cada requisição roda em uma thread separada.

Se vierem vários vídeos em uma requisição só, o sistema irá encaminhar cada vídeo para uma thread diferente, usando recursos de assincronismo.

### 2.2 Balancemaneto de carga

"Em caso de picos o sistema não deve perder uma requisição."

O sistema roda na AWS ECS, um orquestrador de containeres próprio da AWS. Foi implementado balanceamento de carga para que nenhuma requisição seja perdida. Naturalmente, esse balanceamento deve ser ajustado e redimensionado conforme a demanda da aplicação.

### 2.3 Autenticação

"O Sistema deve ser protegido por usuário e senha."

FORMA DE AUTENTICAÇÃO

Para poder usar o sistema, o usuário deve se autenticar usando sua conta Google.
Sem se autenticar, o usuário não consegue enviar vídeos, listar os vídeos enviados e visualizar os status de cada um.
Obviamente, uma vez autenticado, ele tem acesso apenas aos seus próprios vídeos.

Por que escolhi usar a conta Google como forma de autenticação?
Por dois motivos:
- Usar uma conta já existente é mais prático e oferece uma experiência mais agradável ao usuário, que não vai precisar preencher um novo cadastro;
- Hoje em dia a maioria dos usuários web possui uma conta Google (em 2024 o Google tinha mais de 2 bilhões de usuários ativos);
- Poderia ter usado o Cognito, mas já usei ele no Tech Challenge da fase 3, então não agregaria nenhuma novidade (embora também fosse uma boa solução).

GERADOR DE TOKEN

O token de autenticação é emitido usando o Firebase e tem prazo de validade de 1 hora (podendo ser renovado pela aplicação se o usuário continuar ativo).

Por que escolhi o Firebase para gerar o token?
O firebase é uma ferramenta interessante porque oferece várias opções de login, permitindo expandir as opções de entrada do sistema no futuro. Exemplos de opções disponíveis: email e senha, número de telefone, Apple sign-in e provedores de identidade federados, tais como o Google, Facebook, Twitter, GitHub, entre outros.

VALIDAÇÃO DO TOKEN NA APLICAÇÃO

O sistema possui 2 microsserviços: "processamento" e "notificação".

O usuário só se comunica com o sistema de processamento, que possui um filtro de autenticação. Esse filtro age de forma global, atuando em todos os endpoints (só tem dois na verdade: enviar e listar vídeos).

O filtro, na hora de validar o token JWT, considera não apenas o conteúdo do token, mas o emissor também (que é a nossa aplicação cadastrada no Firebase). Isso corrobora na segurança do sistema.

O microsserviço de notificação é consumido apenas pelo microsserviço de processamento. Ele roda em uma VPC privada dentro da AWS e não é acessível ao usuário.


### 2.4 Listar os status dos vídeos

"O fluxo deve ter uma listagem de status dos vídeos de um usuário."

Esse ponto criou uma certa dúvida se o fluxo citado no documento se referia ao uso de WebFlux ou SSE, mas em contato com os professores pelo Discord foi orientado que se tratava apenas de uma lista.

Inicialmente, eu pensei em criar um endpoint que atualizaria os status dos vídeos de tempos em tempos para o cliente (usando webflux). Mas depois achei melhor criar um endpoint simples que retorna a listagem e deixar nas maõs do cliente o controle sobre a consulta. No caso, o cliente pode ser uma aplicação front end e ela decide quando e em qual intervalo de tempo ela quer consultar a listagem. Ela decide se quer consultar apenas uma vez ou se prefere ir atualizando os status, ela decide quando parar, etc.

O front end não é necessário para a entrega do Tech Challenge, mas eu criei um projeto em Angular bem simples, só para deixar a demonstração da aplicação mais simples. Assim não será necessário ficar inserindo token de autenticação manualmente na requisição.

### 2.5 Notificações

"Em caso de erro um usuário pode ser notificado (email ou um outro meio de comunicação)."

O microsserviço de notificação foi criado só para fazer isso.

Quando o usuário envia um vídeo para processar, ele recebe uma resposta 204 assim que o upload do vídeo é concluído. Quando a aplicação finaliza o processamento de um vídeo, um email é enviado para o usuário notificando a finalização com sucesso ou falha, conforme o caso.

Se for sucesso, o link para download do arquivo zip contendo as imagens vai junto no e-mail. Se houver falha, o motivo da falha é informado no e-mail (pode ser um tipo de arquivo não compatível com o serviço, por exemplo).


### 2.6 Persistência dos dados

"O sistema deve persistir os dados."

O sistema persiste os dados de uso do usuário e o arquivo zip gerado.

Com relação à parte multimídia, decidimos não persistir o vídeo em si, o que poderia impactar no custo de armazenamento. O vídeo é descartado após o processamento, apenas o resultado final, o arquivo zip para download é persistido.

Com relação aos dados de utilização do usuário, persistimos:
- nome e email do usuário;
- nome do vídeo recebido;
- timestamp do recebimento do vídeo;
- status do vídeo;
- timestamp do status;
- mensagem de erro, se houver;
- link para download do arquivo zip.

Isso tudo será detalhado melhor mais à frente na parte de banco de dados.


### 2.7 Escalabilidade

"O sistema deve estar em uma arquitetura que o permita ser escalado."

Ok, o sistema roda na AWS ECS e utiliza o banco de dados AWS Aurora. Ambos são escaláveis.

### 2.8 Repositórios

"O projeto deve ser versionado no Github."

Ok, os links para os repositórios se encontram no início deste documento. Os repositórios têm a branch main protegida e só aceitam merge por meio de pull request.


### 2.9 Qualidade do software

"O projeto deve ter testes que garantam a sua qualidade."

Cada microsserviço possui cobertura de testes mínima de 80% e análise de issues/vulnerabilidades pelo Sonar.

Na pipeline, após a análise do Sonar, tem um step para validar da qualidade do código. Essa validação é feita com um script bash que pega os dados do Sonar Cloud (utilizando a API Web que ele disponibiliza) e verifica se os valores estão ok.

O script confere e imprime os dados, item por item, no log da pipeline:
- coverage analysis (tests errors, testes failures, coverage and line coverage);
- security analysis (vulnerabilities, hotspots and rating);
- reliability analysis (bugs and rating);
- maintainability analysis (code smells and rating);
- quality gate (status).

### 2.10 Pipeline

"CI/CD da aplicação."

A pipeline segue o padrão dos projetos anteriores: ela valida a qualidade do código (conforme descrito no item anterior) e, se a qualidade estiver ok, faz o build da imagem docker, faz o push no ECR e o deploy no ECS. Se der qualquer erro, a pipeline é interrompida.


## 3 Evidência dos testes

## 4 Banco de dados

### 4.1 Modelagem

### 4.2 Script de criação

O script segue abaixo:

```SQL
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS processamento (
    numero_video BIGINT AUTO_INCREMENT PRIMARY KEY,
    nome_video VARCHAR(255) NOT NULL,
    usuario_id BIGINT NOT NULL,
    status_processamento VARCHAR(255) NOT NULL,
    mensagem_erro TEXT,
    link_download VARCHAR(500),
    timestamp_inicio DATETIME NOT NULL,
    timestamp_conclusao DATETIME,
    CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);
```

## Projetos futuros

Como ideias para projetos futuros, poderíamos expandir as opções de autenticação do usuário e criar um microsserviço para pagamento da assinatura (supondo que o microsserviço seja pago, não custeado com anúncios ou outras formas de financiamento).









### 2.1. Arquitetura

O projeto da fase anterior, que utilizava a Clean Architecture, deverá ser migrado para a arquitetura de microsserviços.

O projeto deverá conter, no mínimo, três microsserviços, que deverão se comunicar entre si.

### 2.2. Banco de dados

Cada microsserviço deverá ter seu próprio banco de dados.

Um microsserviço não poderá acessar o banco de dados de outro microsserviço.

Deverá ser utilizado, pelo menos, um banco de dados SQL (relacional) e um NoSQL (não relacional).

### 2.3. Testes

Os microsserviços deverão conter testes de unidade e, pelo menos, um caminho de teste deve implementar o BDD.

Os testes deverão cobrir, no mínimo, 80% do código.

Não foi pedido teste de integração (além do BDD, que geralmente inclui integração, embora não esteja limitado a isso).

### 2.4. Pipeline

Cada microsserviço deverá ter seu próprio repositório no GitHub.

As branchs main deverão estar protegidas de forma a não permitir commits diretos.

Alterações na branch main só serão permitidas via pull request.

A pipeline deverá ser acionada sempre que a branch main for alterada.

A pipeline deverá:
- validar o build;
- validar a qualidade do código via sonarqube ou outro serviço semelhante;
- validar a cobertura de testes, sendo o coverage mínimo de 80%;
- realizar o deploy na nuvem escolhida.

### 2.5. API Web

A seguir, seguem os requisitos quanto à API web definidos no Tech Challenge.

Não houve requisitos novos em relação à fase anterior, mas foi necessário criar alguns endpoints novos para permitir/facilitar a comunicação entre os microsserviços. Então, além dos endpoints mencionados abaixo, que são requisitos do projeto, teremos outros que serão demonstrados no vídeo do projeto.

Cliente

- Cadastrar cliente
- Buscar cliente pelo CPF

Produto:

- Criar, editar e remover produtos
- Buscar produtos por categoria

Pedido

- Fazer checkout
- Deverá retornar a identificação do pedido
- Atualizar o status do pedido
- Consultar o status do pagamento
- Listar pedidos nessa ordem: Pronto > Em Preparação > Recebido
- Pedidos mais antigos primeiro e mais novos depois.
- Pedidos finalizados não devem aparecer na lista.

## 3. Evidências dos testes

Abaixo segue a captura da tela do Sonar Cloud e também deixaremos os links para as pipelines com todos os steps concluídos com sucesso, incluindo os testes, cujos logs são impressos na pipeline.

![Tela do Sonar Cloud](assets/tela-sonar.png)

Links:
- Microsserviço de PEDIDO: https://github.com/efrancodelima/pedido/actions/runs/13273137930/job/37057026528
- Microsserviço de PAGAMENTO: https://github.com/efrancodelima/pagamento/actions/runs/13272650996/job/37055483951
- Microsserviço de PRODUÇÃO: https://github.com/efrancodelima/producao/actions/runs/13231048854/job/36928236519

## 4. Comentários sobre o projeto

Todos os requisitos mencionados nesse documento são atendidos pelo projeto, mas deixaremos aqui alguns comentários sobre as partes que julgamos mais relevantes.

### 4.1. Microsserviços

A aplicação é composta de três microsserviços: pedido, pagamento e produção.

Seguimos mais ou menos a forma de divisão sugerida no Tech Challenge, com pequenas modificações.

O microsserviço de pedido não é a visão do cliente e o microsserviço de produção não é a visão da cozinha.

Em vez de dividirmos os microsserviços por público alvo, preferimos dividi-los por domínios.

Então, embora o microsserviço de produção atenda à cozinha, ele também pode atender o cliente, que deseja saber em que etapa de produção seu pedido está.

Da mesma forma, o microsserviço de pedido, embora atenda ao cliente, que precisa fazer o checkout, também atende à cozinha, que necessita saber o que cada pedido contém para poder produzir.

No divisão por domínios, poderíamos ter um microsserviço só para clientes, outro para produtos e assim por diante. Porém, preferimos adotar uma abordagem mais conservadora na migração do monolito: em vez de realizar uma mudança radical, separamos pequenas partes dele e transformamos em microsserviços.

Sendo assim, o microsserviço de pedidos ainda concentra a parte das funcionalidades da aplicação anterior, exceto no que diz respeito ao pagamento e à esteira de produção (o histórico dos pedidos).

### 4.2. Banco de dados

O microsserviço de pagamento utiliza o banco de dados MongoDB, que é orientado a documentos.

Escolhi esse microsserviço para utilizar o banco não relacional, pois ele recebe notificações do mercado livre e, em algum momento futuro, o formato da notificação pode mudar ou a aplicação pode decidir alterar quais campos dessa notificação é importante guardar em seu banco de dados.

Nessa situação, o fato do MogoDB ter uma estrutura flexível em vez de um esquema rígido pode ser uma vantagem.

Os outros microsserviços (pedido e produção) utilizam banco de dados relacionais.

### 4.4. Testes

Os testes de unidade sozinhos já atingem cobertura de código superior a 80%, tanto na cobertura de linhas de código quanto na cobertura de ramificações do código.

Além deles, criamos 3 testes BDD no microsserviço de pedido, 2 no de pagamento e 2 no de produção (número superior ao mínimo, que era 1 em cada). Criamos um a mais no de pedido, pois é o microsserviço que contém mais funcionalidades, então pareceu razoável que ele tivesse mais testes também.

### 4.4. Pipeline

As mudanças em relação à fase anterior foram os testes e o Sonar.

Os testes BDD necessitam da aplicação rodando para funcionar, então temos novos steps para:
- iniciar a aplicação com o profile de testes (comando maven);
- aguardar a disponibilidade da aplicação (script bash);
- enviar o código para análise do Sonar Cloud;
- verificar a qualidade do código e a cobertura de testes.

No microsserviço de pagamento temos um step a mais que é o service do MongoDB. Embora o uso de bancos de dados do tipo embedded (como o H2) seja amplamente difundido, há uma discussão na comunidade a respeito do assunto: um teste de integração de verdade não deveria usar um banco de dados de verdade?

Note que as versões do Mongo embedded disponíveis no mercado não são oferecidas pela mesma equipe que oferece a versão ofocial do MongoDB. Por esse motivo, prefirimos utilizar um banco de dados real nos testes em vez de um banco embedded. Poderíamos ter feito isso nos outros microsserviços também? Sim, mas como esses já estavam funcionando, por ora, deixamos como estão.

Para análise do código, utilizamos o Sonar Cloud. Essa ferramenta realmente executa os testes, em vez de apenas utilizar os relatórios gerados pelo jacoco. No log do step do Sonar, podemos verificar os testes sendo executados. Sendo assim, removi o step que tinha na fase anterior com o comando "mvn test". A ideia é tornar a pipeline mais enxuta e eficiente.

A análise da qualidade do código é feita com um script bash que pega os dados do Sonar Cloud (utilizando a API Web que ele disponibiliza) e verifica se os valores estão ok.

O script confere e imprime os dados, item por item, no log da pipeline:
- coverage analysis (tests errors, testes failures, coverage and line coverage);
- security analysis (vulnerabilities, hotspots and rating);
- reliability analysis (bugs and rating);
- maintainability analysis (code smells and rating);
- quality gate (status).

O restante da pipeline não mudou muita coisa. Só o build da imagem, que teve o arquivo dockerfile modificado um pouco: antes os dados de conexão com o banco eram definidos no build, passados como variáveis de ambiente; agora essa parte foi removida e os as variáveis são definidas nas task definitions do ECS.

Resumindo: os dados continuam sendo passados como variáveis de ambiente, só que agora o valor das variáveis é definido após o build, não durante. Isso dá mais flexibilidade ao código, já que não precisamos realizar um novo build caso alguma dessas variáveis mude. Além dos dados de conexão com o banco, as URLs dos microsserviços também são variáveis de ambiente definidas após o build. 

## 5. Instrução para rodar a aplicação

Primeiro, é necessário verificar se os bancos de dados estão ativos e, depois, realizar o deploy dos microsserviços.

Sugestão de ordem para execução das APIs:

- Cadastrar cliente
- Buscar cliente pelo CPF
- Cadastrar produtos
- Editar produto
- Buscar produtos por categoria
- Remover produtos (não remova todos, deixe pelo menos 1)
- Fazer checkout
- Consultar o status do pagamento
- Mock da notificação do Mercado Pago \*
- Atualizar o status do pedido
- Listar pedidos

O status do pedido muda em uma ordem definida: recebido, em preparação, pronto, finalizado. Mas ele não avança se o pedido não tiver o pagamento aprovado, então é necessário realizar o mock da notificação do Mercado Pago antes de atualizar o status do pedido.

Exemplo de mock para a notificação do Mercado Pago usando o curl (você pode usar o Postman também, se preferir).

```
curl -X PUT <URL>/api/v2/pedidos/webhook/ \
-H "Content-Type: application/json" \
-d '{
"id": 1,
"date_created": "2024-09-30T11:26:38.000Z",
"date_approved": "2024-09-30T11:26:38.000Z",
"date_last_updated": "2024-09-30T11:26:38.000Z",
"money_release_date": "2017-09-30T11:22:14.000Z",
"payment_method_id": "Pix",
"payment_type_id": "credit_card",
"status": "approved",
"status_detail": "accredited",
"currency_id": "BRL",
"description": "Pago Pizza",
"collector_id": 2,
"payer": {
  "id": 123,
  "email": "test_user_80507629@testuser.com",
  "identification": {
	"type": "CPF",
	"number": 19119119100
  },
  "type": "customer"
},
"metadata": {},
"additional_info": {},
"external_reference": "MP0001",
"transaction_amount": 250,
"transaction_amount_refunded": 50,
"coupon_amount": 15,
"transaction_details": {
  "net_received_amount": 250,
  "total_paid_amount": 250,
  "overpaid_amount": 0,
  "installment_amount": 250
},
"installments": 1,
"card": {}
}'
```

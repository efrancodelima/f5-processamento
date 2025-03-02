# language: pt

Funcionalidade: Consultar status do pedido

  Cenário: Usuário consulta o status de um pedido
    Dado que existe um pedido pronto na esteira de produção
    Quando o sistema receber uma consulta sobre o status do pedido
    Então o sistema deve retornar o status pronto

# POC Resctive Rest
Esse projeto é uma prova de conceito (POC) com o objetivo de criar um serviço REST que trabalhe de forma asincrona utilizando programação reativa (Hystrix e RxJava).

## Técnologias envolvidas
- Java 8
- Spring Boot
- Jersey
- Hystrix
- RxJava
- Swagger
 
## Regras de Negócio
O serviço da api consiste em buscar um produto por id, e caso passado uma utm, será aplicado um desconto nos preços de um seller específico deste produto.

Entidades: um **Product** possui uma lista de **Variation** (SKUs) que por sua vez possui uma lista de **Offer** (uma para cada seller do produto)

O serviço busca um produto por id, e caso seja passado uma UTM, para cada variação do produto que contém uma offerta que recebe desconto, é buscado uma nova lista de ofertas para a variação (com desconto da utm) e aplicado o novo preço na oferta que recebe ddesconto.
Todo processamento é feito de forma reativa, e todo processamento dde desconto em paralelo.

### Defaults da aplicação
- Preço dos produtos: 100,00 
- Preço com desconto: 50,00
- Seller que tem descontos aplicados: 1

## Acessando a API
Após subir a plicação o serviço esta disponível em: (http://localhost:8080/api/products/{id})
O serviço tem como opção parâmetros para simular delays nos serviços, facilitando os testes de paralelismo e circuit break.
A documentação da api pode ser visualizada em: (http://localhost:8080/swagger/index.html)
Para monitorar o Hystrix, basta acessar (http://localhost:8080/hystrix) e passar o link (http://localhost:8080/hystrix.stream)

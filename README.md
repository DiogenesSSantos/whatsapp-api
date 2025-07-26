# WhatsApp API

Projeto desenvolvido para auxiliar na comunicação entre o paciente e a Regulação de Saúde, facilitando o agendamento e o acompanhamento de consultas e exames marcados.

## Descrição

Esta API foi criada para integrar o canal de comunicação via WhatsApp com os sistemas de agendamento da Regulação de Saúde. A proposta é melhorar o fluxo de informações, permitindo que pacientes recebam notificações, lembretes e possam até mesmo confirmar ou reagendar compromissos diretamente pelo aplicativo. Essa abordagem visa reduzir faltas, 
otimizar processos e trazer mais agilidade na troca de informações entre os envolvidos.
Leia a documentação <a href="http://javadocsapiwhatsapp.devdiogenes.shop/index.html">JavaDocs</a> da aplicação.

## Funcionalidades

- **Envio de Mensagens Automatizado:** Automatize a comunicação com os pacientes, enviando consultas/exames marcados.
- **Confirmação Interativa:** Permite que os pacientes confirmem ou solicitem alterações diretamente pela resposta à mensagem.
- **Registro de Comunicação:** Guarda o histórico das interações para fins de auditoria e análise de dados, como impressão em PDF desses dados.

## Tecnologias Utilizadas

- **Backend:** Java + spring boot
- **Front-end:** Html,Css,JavaScript
- **Banco de Dados:** MongoDB, MySQL ou outro SGBD da sua escolha
- **Integração WhatsApp:** Biblioteca do <a href= "https://github.com/Auties00/Cobalt">autiesCobalt</a>
- **Gerador QrCode:** Biblioteca do <a href="https://github.com/zxing/zxing">zxing</a>
- **Relatórios:** JasperReports
- **Autenticação:** Oauth2 um método robusto para controle de acesso

## Requisitos

- **Java:** Versão 21 ou superior.
- **Spring Boot / Spring-JPA/ Spring-Security :** Versões mais recente. 
- **JasperReports:** Versão mais recente do <a href="https://community.jaspersoft.com/download-jaspersoft/community-edition/">JasperReports</a>.
- **QrCodeGerador:** Versão mais recente da biblioteca do <a href="https://github.com/zxing/zxing">zxing</a>.
- **Conexão com a API do WhatsApp:** Versão mais recente da biblioteca do <a href= "https://github.com/Auties00/Cobalt">autiesCobalt</a>.
- **Banco de Dados:** Sistema de armazenamento devidamente configurado e conectado.

## Instalação

1. **Clone o repositório:**

   ```bash
   git clone https://github.com/DiogenesSSantos/whatsappApi.git


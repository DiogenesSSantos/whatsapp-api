document.addEventListener("DOMContentLoaded", function() {
    const numerosAdicionados = [];
    const telefonesAdicionadosDiv = document.getElementById("telefonesAdicionados");
    const adicionarTelefoneBtn = document.getElementById("adicionarTelefoneBtn");
    const adicionarBtn = document.getElementById("adicionarBtn");
    const enviarBtn = document.getElementById("enviarBtn");
    const destinatariosUl = document.getElementById("destinatarios");
    const statusMensagemDiv = document.getElementById("statusMensagem");

    adicionarTelefoneBtn.addEventListener("click", function() {
        const telefoneInput = document.getElementById("telefone");
        const numeroTelefone = telefoneInput.value.trim();

        if (numeroTelefone && /^\d+$/.test(numeroTelefone)) {  // Verifica se o número contém apenas dígitos
            numerosAdicionados.push(numeroTelefone);
            telefoneInput.value = "";
            renderizarTelefones();
        } else {
            alert("Por favor, insira um número de telefone válido contendo apenas dígitos.");
        }
    });

    adicionarBtn.addEventListener("click", function() {
        const nomePaciente = document.getElementById("paciente").value.trim();
        const tipoConsulta = document.getElementById("tipoConsulta").value;
        const bairro = document.getElementById("bairro").value.trim();
        const data = document.getElementById("data").value;

        if (nomePaciente && numerosAdicionados.length > 0) {
            const usuario = {
                nome: nomePaciente,
                numeros: [...numerosAdicionados],
                bairro: bairro,
                consulta: tipoConsulta,
                data: data
            };

            adicionarDestinatario(usuario);
            limparCampos();
        }
    });

    enviarBtn.addEventListener("click", function() {
        const destinatarios = obterDestinatarios();

        statusMensagemDiv.style.display = "block";
        statusMensagemDiv.textContent = "Enviando...";
        enviarBtn.disabled = true;

        axios.post('https://api.devdiogenes.shop/api/zap/enviarList', destinatarios, {
            headers: {
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            console.log("Mensagem enviada com sucesso:", response.data);
            destinatariosUl.innerHTML = "";
            statusMensagemDiv.textContent = "Todas as mensagens enviadas";
            enviarBtn.disabled = false;
        })
        .catch(error => {
            console.error("Erro ao enviar mensagem:", error);
            statusMensagemDiv.textContent = "Erro ao enviar mensagem";
            enviarBtn.disabled = false;
        });
    });

    function renderizarTelefones() {
        telefonesAdicionadosDiv.innerHTML = numerosAdicionados.map((numero, index) => 
            `<p>${numero} <button type="button" class="removerTelefoneBtn" data-index="${index}">Remover</button></p>`
        ).join("");

        // Adicionar eventos de clique aos botões de remover
        document.querySelectorAll(".removerTelefoneBtn").forEach(button => {
            button.addEventListener("click", function() {
                const index = this.getAttribute("data-index");
                removerTelefone(index);
            });
        });
    }

    function adicionarDestinatario(usuario) {
        const listItem = document.createElement("li");
        listItem.innerHTML = `${JSON.stringify(usuario)} <button type="button" onclick="this.parentElement.remove()">Remover</button>`;
        destinatariosUl.appendChild(listItem);
    }

    function obterDestinatarios() {
        const destinatarios = [];
        destinatariosUl.querySelectorAll("li").forEach(listItem => {
            destinatarios.push(JSON.parse(listItem.textContent.replace("Remover", "").trim()));
        });
        return destinatarios;
    }

    function removerTelefone(index) {
        numerosAdicionados.splice(index, 1);
        renderizarTelefones();
    }

    function limparCampos() {
        document.getElementById("paciente").value = "";
        numerosAdicionados.length = 0;
        renderizarTelefones();
        document.getElementById("bairro").value = "";
        document.getElementById("data").value = "";
        document.getElementById("telefone").value = "";
    }
});


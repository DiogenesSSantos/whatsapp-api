document.getElementById('adicionarTelefoneBtn').addEventListener('click', function () {
    const telefone = document.getElementById('telefone').value;

    if (telefone) {
        adicionarTelefone(telefone);
        document.getElementById('telefone').value = ''; // Limpar o campo de telefone
    } else {
        alert('Por favor, insira um número de telefone.');
    }
});

document.getElementById('adicionarBtn').addEventListener('click', function () {
    const telefones = Array.from(document.querySelectorAll('#telefonesAdicionados li')).map(li => li.dataset.telefone);
    const paciente = document.getElementById('paciente').value;
    const tipoConsulta = document.getElementById('tipoConsulta').value;

    if (telefones.length && paciente && tipoConsulta) {
        adicionarDestinatario(telefones, paciente, tipoConsulta);
        resetarCamposTelefone();
        resetarCampoPaciente();
    } else {
        alert('Por favor, preencha todos os campos.');
    }
});

document.getElementById('enviarBtn').addEventListener('click', function () {
    const destinatarios = Array.from(document.querySelectorAll('#destinatarios li')).map(li => ({
        nome: li.dataset.paciente,
        numeros: JSON.parse(li.dataset.telefones),
        tipoConsulta: li.dataset.tipoConsulta
    }));

    if (destinatarios.length) {
        enviarListaDeMensagens(destinatarios);
    } else {
        alert('Adicione pelo menos um destinatário.');
    }
});

document.getElementById('monitorarBtn').addEventListener('click', function () {
    window.location.href = 'respostas.html';
});

function adicionarTelefone(telefone) {
    const ul = document.getElementById('telefonesAdicionados');
    const li = document.createElement('li');
    li.dataset.telefone = telefone;
    li.textContent = telefone;

    const removerBtn = document.createElement('button');
    removerBtn.textContent = 'Remover';
    removerBtn.addEventListener('click', function () {
        li.remove();
    });
    li.appendChild(removerBtn);

    ul.appendChild(li);
}

function adicionarDestinatario(telefones, paciente, tipoConsulta) {
    const ul = document.getElementById('destinatarios');
    const li = document.createElement('li');
    li.dataset.telefones = JSON.stringify(telefones);
    li.dataset.paciente = paciente;
    li.dataset.tipoConsulta = tipoConsulta;
    li.textContent = `Telefones: ${telefones.join(', ')}, Paciente: ${paciente}, Tipo de Consulta: ${obterNomeTipoConsulta(tipoConsulta)}`;
    ul.appendChild(li);
}

function enviarListaDeMensagens(destinatarios) {
    console.log('Enviando destinatários:', JSON.stringify(destinatarios, null, 2)); // Log detalhado para inspeção
    axios.post('http://localhost:8080/zap/enviarList', destinatarios, {
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        console.log('Mensagens enviadas com sucesso:', response.data);
        alert('Mensagens enviadas com sucesso!');
    })
    .catch(error => {
        console.error('Erro ao enviar mensagens:', error.response ? error.response.data : error.message);
        alert('Erro ao enviar mensagens. Verifique o console para mais detalhes.');
    });
}

function resetarCamposTelefone() {
    document.getElementById('telefone').value = ''; // Limpar o campo de telefone principal
    const ul = document.getElementById('telefonesAdicionados');
    ul.innerHTML = ''; // Limpar a lista de telefones adicionados
}

function resetarCampoPaciente() {
    document.getElementById('paciente').value = ''; // Limpar o campo de nome do paciente
}

// Função para obter o nome legível do tipo de consulta
function obterNomeTipoConsulta(tipoConsulta) {
    switch (tipoConsulta) {
        case "consulta-geral":
            return "OFTALMOLOGISTA";
        case "consulta-urgente":
            return "COLONOSCOPIA";
        case "consulta-retorno":
            return "LABORATORIAIS";
        default:
            return tipoConsulta;
    }
}

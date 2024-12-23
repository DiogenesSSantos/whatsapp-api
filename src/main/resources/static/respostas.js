const socket = new WebSocket('ws://localhost:8080'); // Substitua pelo endereço do seu servidor WebSocket

socket.onmessage = function (event) {
    const data = JSON.parse(event.data);
    const telefone = data.telefone;
    const resposta = data.resposta;
    adicionarResposta(`Resposta de ${telefone}: ${resposta}`);
};

socket.onopen = function () {
    console.log('Conectado ao servidor WebSocket');
};

socket.onclose = function () {
    console.log('Desconectado do servidor WebSocket');
};

socket.onerror = function (error) {
    console.error('Erro no WebSocket: ', error);
};

function adicionarResposta(resposta) {
    const ul = document.getElementById('respostas');
    const li = document.createElement('li');
    li.textContent = resposta;
    ul.appendChild(li);
}

function voltar() {
    window.location.href = 'index.html';
}

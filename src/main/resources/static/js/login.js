javascript
"use strict";

document.addEventListener("DOMContentLoaded", () => {
    iniciarLogin();
    iniciarVisualizacaoSenha();
    atualizarAno();
    iniciarValidacaoEmTempoReal();
});

const formulario = document.querySelector(".formulario");
const campoUsuario = document.querySelector("#nomeUsuario");
const campoSenha = document.querySelector("#senha");
const botaoLogin = document.querySelector(".btn-login");

function iniciarLogin() {

    if (!formulario || !campoUsuario || !campoSenha || !botaoLogin) {
        console.error("Elementos do formulário de login não encontrados.");
        return;
    }

    formulario.addEventListener("submit", (evento) => {

        const usuario = campoUsuario.value.trim();
        const senha = campoSenha.value;

        if (!validarCampos(usuario, senha)) {
            evento.preventDefault();
            return;
        }

        alterarEstadoBotao(true);

        // O formulário continua sendo enviado ao Spring Boot.
    });
}

function validarCampos(usuario, senha) {

    limparMensagens();

    let valido = true;

    if (usuario.length === 0) {
        mostrarErro(campoUsuario, "Informe seu nome de usuário.");
        valido = false;
    }

    if (senha.trim().length === 0) {
        mostrarErro(campoSenha, "Informe sua senha.");
        valido = false;
    }

    if (usuario.length > 50) {
        mostrarErro(campoUsuario, "O nome de usuário deve ter no máximo 50 caracteres.");
        valido = false;
    }

    if (senha.length > 128) {
        mostrarErro(campoSenha, "A senha deve ter no máximo 128 caracteres.");
        valido = false;
    }

    if (!valido) {
        const primeiroCampoInvalido = formulario.querySelector(".is-invalid");

        if (primeiroCampoInvalido) {
            primeiroCampoInvalido.focus();
        }
    }

    return valido;
}

function mostrarErro(campo, mensagem) {

    campo.classList.add("is-invalid");
    campo.setAttribute("aria-invalid", "true");

    let mensagemErro = campo.parentElement.querySelector(".mensagem-erro");

    if (!mensagemErro) {
        mensagemErro = document.createElement("small");
        mensagemErro.className = "mensagem-erro";
        mensagemErro.setAttribute("role", "alert");

        campo.insertAdjacentElement("afterend", mensagemErro);
    }

    mensagemErro.textContent = mensagem;
}

function limparMensagens() {

    formulario.querySelectorAll(".mensagem-erro").forEach((mensagem) => {
        mensagem.remove();
    });

    formulario.querySelectorAll(".is-invalid").forEach((campo) => {
        campo.classList.remove("is-invalid");
        campo.removeAttribute("aria-invalid");
    });
}

function iniciarValidacaoEmTempoReal() {

    if (!formulario) return;

    [campoUsuario, campoSenha].forEach((campo) => {

        if (!campo) return;

        campo.addEventListener("input", () => {

            campo.classList.remove("is-invalid");
            campo.removeAttribute("aria-invalid");

            const mensagem = campo.parentElement.querySelector(".mensagem-erro");

            if (mensagem) {
                mensagem.remove();
            }
        });
    });
}

function iniciarVisualizacaoSenha() {

    if (!campoSenha) return;

    const botaoVisualizar = document.querySelector("#mostrarSenha");

    if (!botaoVisualizar) return;

    botaoVisualizar.addEventListener("click", () => {

        const senhaVisivel = campoSenha.type === "text";

        campoSenha.type = senhaVisivel ? "password" : "text";

        const icone = botaoVisualizar.querySelector("i");

        if (icone) {
            icone.classList.toggle("bi-eye", senhaVisivel);
            icone.classList.toggle("bi-eye-slash", !senhaVisivel);
        }

        botaoVisualizar.setAttribute(
            "aria-label",
            senhaVisivel ? "Mostrar senha" : "Ocultar senha"
        );
    });
}

function alterarEstadoBotao(carregando) {

    if (!botaoLogin) return;

    if (carregando) {

        botaoLogin.disabled = true;

        botaoLogin.innerHTML = `
<span class="spinner-border spinner-border-sm"
aria-hidden="true"></span>
Entrando...
`;

        botaoLogin.setAttribute("aria-busy", "true");

    } else {

        botaoLogin.disabled = false;

        botaoLogin.innerHTML = `
<i class="bi bi-box-arrow-in-right"></i>
Entrar
    `;

        botaoLogin.removeAttribute("aria-busy");
    }
}

function atualizarAno() {

    const elementoAno = document.querySelector("#ano");

    if (elementoAno) {
        elementoAno.textContent = new Date().getFullYear();
    }
}
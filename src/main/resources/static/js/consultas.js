
"use strict";

document.addEventListener("DOMContentLoaded", () => {

    const botaoConfirmar = document.querySelector(".btn-confirmar");
    const modalElemento = document.getElementById("staticBackdrop");

    const nomePet = document.getElementById("nomePet");
    const raca = document.getElementById("raca");
    const idade = document.getElementById("idade");

    const nomeTutor = document.getElementById("nomeTutor");
    const celular = document.getElementById("celular");
    const email = document.getElementById("email");

    const consulta = document.getElementById("consulta");
    const especialidade = document.getElementById("especialidade");
    const data = document.getElementById("data");
    const horario = document.getElementById("horario");

    const opcoesPet = document.querySelector(".opcoes-pet");

    if (
        !botaoConfirmar ||
        !modalElemento ||
        !nomePet ||
        !raca ||
        !idade ||
        !nomeTutor ||
        !celular ||
        !email ||
        !consulta ||
        !especialidade ||
        !data ||
        !horario ||
        !opcoesPet
    ) {
        console.error("Elementos do formulário de consultas não encontrados.");
        return;
    }

    if (typeof bootstrap === "undefined") {
        console.error("Bootstrap não foi carregado.");
        return;
    }

    const modalConfirmacao =
        bootstrap.Modal.getOrCreateInstance(modalElemento);

    function obterDataLocal() {
        const hoje = new Date();

        const ano = hoje.getFullYear();
        const mes = String(hoje.getMonth() + 1).padStart(2, "0");
        const dia = String(hoje.getDate()).padStart(2, "0");

        return `${ano}-${mes}-${dia}`;
    }

    data.min = obterDataLocal();

    // Máscara de telefone

    celular.addEventListener("input", () => {

        let numero = celular.value
            .replace(/\D/g, "")
            .substring(0, 11);

        if (numero.length <= 10) {

            numero = numero.replace(/^(\d{2})(\d)/, "($1) $2");
            numero = numero.replace(/(\d{4})(\d)/, "$1-$2");

        } else {

            numero = numero.replace(/^(\d{2})(\d)/, "($1) $2");
            numero = numero.replace(/(\d{5})(\d)/, "$1-$2");

        }

        celular.value = numero;

        limparErro(celular);
    });

    // Limpar erro de um campo

    function limparErro(campo) {

        if (!campo) return;

        campo.classList.remove("is-invalid");
        campo.removeAttribute("aria-invalid");

        const mensagemErro = campo.parentElement
            ? campo.parentElement.querySelector(".mensagem-erro")
            : null;

        if (mensagemErro) {
            mensagemErro.remove();
        }
    }

    // Limpar todos os erros

    function limparTodosErros() {

        document.querySelectorAll(".is-invalid").forEach(campo => {
            campo.classList.remove("is-invalid");
            campo.removeAttribute("aria-invalid");
        });

        document.querySelectorAll(".mensagem-erro").forEach(mensagem => {
            mensagem.remove();
        });

        opcoesPet.classList.remove("border-danger");
    }

    // Mostrar erro

    function mostrarErro(campo, mensagem) {

        if (!campo) return;

        campo.classList.add("is-invalid");
        campo.setAttribute("aria-invalid", "true");

        let mensagemErro = campo.parentElement
            ? campo.parentElement.querySelector(".mensagem-erro")
            : null;

        if (!mensagemErro) {

            mensagemErro = document.createElement("small");
            mensagemErro.className = "mensagem-erro text-danger d-block";
            mensagemErro.setAttribute("role", "alert");

            campo.insertAdjacentElement("afterend", mensagemErro);
        }

        mensagemErro.textContent = mensagem;
    }

    // Validar pet

    function validarPet() {

        let valido = true;

        const animalSelecionado = document.querySelector(
            'input[name="animal"]:checked'
        );

        if (!animalSelecionado) {

            opcoesPet.classList.add("border-danger");
            valido = false;

        }

        if (nomePet.value.trim().length < 2) {

            mostrarErro(nomePet, "Informe o nome do seu pet.");
            valido = false;

        }

        if (raca.value.trim() === "") {

            mostrarErro(raca, "Informe a espécie ou raça do pet.");
            valido = false;

        }

        const idadePet = Number(idade.value);

        if (
            idade.value.trim() === "" ||
            !Number.isFinite(idadePet) ||
            idadePet < 0
        ) {

            mostrarErro(idade, "Informe uma idade válida.");
            valido = false;

        }

        return valido;
    }

    // Validar tutor

    function validarTutor() {

        let valido = true;

        if (nomeTutor.value.trim().length < 3) {

            mostrarErro(nomeTutor, "Informe seu nome completo.");
            valido = false;

        }

        const numeroCelular = celular.value.replace(/\D/g, "");

        if (
            numeroCelular.length !== 10 &&
            numeroCelular.length !== 11
        ) {

            mostrarErro(celular, "Informe um telefone válido com DDD.");
            valido = false;

        }

        const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailValido.test(email.value.trim())) {

            mostrarErro(email, "Informe um endereço de e-mail válido.");
            valido = false;

        }

        return valido;
    }

    // Validar serviço

    function validarServico() {

        let valido = true;

        if (!consulta.value) {

            mostrarErro(consulta, "Selecione o tipo de consulta.");
            valido = false;

        }

        if (!especialidade.value) {

            mostrarErro(especialidade, "Selecione uma especialidade.");
            valido = false;

        }

        if (!data.value) {

            mostrarErro(data, "Selecione a data da consulta.");
            valido = false;

        } else {

            const dataSelecionada = new Date(`${data.value}T12:00:00`);
            const hoje = new Date(`${obterDataLocal()}T12:00:00`);

            if (Number.isNaN(dataSelecionada.getTime())) {

                mostrarErro(data, "Informe uma data válida.");
                valido = false;

            } else if (dataSelecionada < hoje) {

                mostrarErro(data, "A data não pode ser anterior ao dia de hoje.");
                valido = false;

            } else if (dataSelecionada.getDay() === 0) {

                mostrarErro(data, "A clínica não realiza atendimentos aos domingos.");
                valido = false;

            }

        }

        if (!horario.value) {

            mostrarErro(horario, "Selecione um horário disponível.");
            valido = false;

        }

        return valido;
    }

    // Limpar erros ao alterar os campos

    const campos = [
        nomePet,
        raca,
        idade,
        nomeTutor,
        celular,
        email,
        consulta,
        especialidade,
        data,
        horario
    ];

    campos.forEach(campo => {

        campo.addEventListener("input", () => limparErro(campo));
        campo.addEventListener("change", () => limparErro(campo));

    });

    document.querySelectorAll('input[name="animal"]').forEach(radio => {

        radio.addEventListener("change", () => {
            opcoesPet.classList.remove("border-danger");
        });

    });

    // Adicionar informações ao modal

    function adicionarInformacao(container, titulo, valor, ultima = false) {

        const paragrafo = document.createElement("p");

        const destaque = document.createElement("strong");
        destaque.textContent = `${titulo}: `;

        paragrafo.appendChild(destaque);
        paragrafo.appendChild(
            document.createTextNode(valor || "Não informado")
        );

        if (ultima) {
            paragrafo.classList.add("mb-0");
        }

        container.appendChild(paragrafo);
    }

    // Mostrar confirmação

    function mostrarConfirmacao() {

        const corpoModal = modalElemento.querySelector(".modal-body");

        if (!corpoModal) {
            console.error("Corpo do modal não encontrado.");
            return;
        }

        const animalSelecionado = document.querySelector(
            'input[name="animal"]:checked'
        );

        const dataFormatada = new Date(
            `${data.value}T12:00:00`
        ).toLocaleDateString("pt-BR");

        corpoModal.replaceChildren();

        const mensagem = document.createElement("p");

        const titulo = document.createElement("strong");
        titulo.textContent = "Pronto! ";

        mensagem.appendChild(titulo);
        mensagem.appendChild(
            document.createTextNode("Confira os dados do agendamento:")
        );

        corpoModal.appendChild(mensagem);

        const dados = document.createElement("div");
        dados.className = "dados-confirmacao";

        adicionarInformacao(dados, "Pet", nomePet.value.trim());
        adicionarInformacao(dados, "Tutor", nomeTutor.value.trim());

        adicionarInformacao(
            dados,
            "Animal",
            animalSelecionado ? animalSelecionado.value : ""
        );

        adicionarInformacao(dados, "Consulta", consulta.value);
        adicionarInformacao(dados, "Especialidade", especialidade.value);
        adicionarInformacao(dados, "Data", dataFormatada);
        adicionarInformacao(dados, "Horário", horario.value, true);

        corpoModal.appendChild(dados);

        const observacao = document.createElement("p");
        observacao.className = "mt-3 mb-0";
        observacao.textContent =
            "Para consultar mais informações, acesse a Carteira-Pet.";

        corpoModal.appendChild(observacao);

        modalConfirmacao.show();
    }

    // Confirmar agendamento

    botaoConfirmar.addEventListener("click", event => {

        event.preventDefault();

        limparTodosErros();

        const petValido = validarPet();
        const tutorValido = validarTutor();
        const servicoValido = validarServico();

        if (!petValido || !tutorValido || !servicoValido) {

            const primeiroErro = document.querySelector(".is-invalid");

            if (primeiroErro) {

                primeiroErro.focus();

            } else if (opcoesPet.classList.contains("border-danger")) {

                const primeiroRadio = document.querySelector(
                    'input[name="animal"]'
                );

                if (primeiroRadio) {
                    primeiroRadio.focus();
                }

            }

            return;
        }

        mostrarConfirmacao();
    });

    console.log("Sistema de consultas Patas & Bigodes inicializado!");

});

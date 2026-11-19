
/* =====================================================
   PATAS & BIGODES
   Sistema de Agendamento de Consultas
===================================================== */

"use strict";

/* =====================================================
   INICIALIZAÇÃO
===================================================== */

document.addEventListener("DOMContentLoaded", () => {

    // Elementos principais
    const botaoConfirmar = document.querySelector(".btn-confirmar");
    const modalElemento = document.getElementById("staticBackdrop");

    const nomePet = document.getElementById("nomePet");
    const raca = document.getElementById("raca");
    const idade = document.getElementById("idade");

    const nomeTutor = document.getElementById("nomeTutor");
    const celular = document.getElementById("celular");
    const email = document.getElementById("email");
    const informacoes = document.getElementById("informacoes");

    const consulta = document.getElementById("consulta");
    const especialidade = document.getElementById("especialidade");
    const data = document.getElementById("data");
    const horario = document.getElementById("horario");

    const opcoesPet = document.querySelector(".opcoes-pet");

    // Verifica se os elementos existem
    if (!botaoConfirmar || !modalElemento) {
        console.error("Erro: elementos de agendamento não encontrados.");
        return;
    }

    // Inicializa o modal do Bootstrap
    const modalConfirmacao = bootstrap.Modal.getOrCreateInstance(
        modalElemento
    );

    /* =================================================
       CONFIGURAÇÃO DA DATA MÍNIMA
    ================================================= */

    function obterDataLocal() {

        const hoje = new Date();

        const ano = hoje.getFullYear();
        const mes = String(hoje.getMonth() + 1).padStart(2, "0");
        const dia = String(hoje.getDate()).padStart(2, "0");

        return `${ano}-${mes}-${dia}`;
    }

    data.min = obterDataLocal();

    /* =================================================
       MÁSCARA DE TELEFONE
    ================================================= */

    celular.addEventListener("input", () => {

        let numero = celular.value.replace(/\D/g, "");

        numero = numero.substring(0, 11);

        if (numero.length <= 10) {

            numero = numero.replace(
                /^(\d{2})(\d)/,
                "($1) $2"
            );

            numero = numero.replace(
                /(\d{4})(\d)/,
                "$1-$2"
            );

        } else {

            numero = numero.replace(
                /^(\d{2})(\d)/,
                "($1) $2"
            );

            numero = numero.replace(
                /(\d{5})(\d)/,
                "$1-$2"
            );
        }

        celular.value = numero;

    });

    /* =================================================
       REMOVER ERROS DOS CAMPOS
    ================================================= */

    function limparErro(campo) {

        if (!campo) return;

        campo.classList.remove("is-invalid");

        const mensagem = campo.parentElement.querySelector(
            ".mensagem-erro"
        );

        if (mensagem) {
            mensagem.remove();
        }
    }

    function limparTodosErros() {

        document.querySelectorAll(".is-invalid").forEach(campo => {
            campo.classList.remove("is-invalid");
        });

        document.querySelectorAll(".mensagem-erro").forEach(mensagem => {
            mensagem.remove();
        });

    }

    /* =================================================
       EXIBIR MENSAGENS DE ERRO
    ================================================= */

    function mostrarErro(campo, mensagem) {

        if (!campo) return;

        campo.classList.add("is-invalid");

        let mensagemErro = campo.parentElement.querySelector(
            ".mensagem-erro"
        );

        if (!mensagemErro) {

            mensagemErro = document.createElement("small");

            mensagemErro.className = "mensagem-erro text-danger";

            campo.insertAdjacentElement("afterend", mensagemErro);
        }

        mensagemErro.textContent = mensagem;
    }

    /* =================================================
       VALIDAÇÃO DOS DADOS DO PET
    ================================================= */

    function validarPet() {

        let valido = true;

        const animalSelecionado = document.querySelector(
            'input[name="animal"]:checked'
        );

        // Tipo de animal
        if (!animalSelecionado) {

            opcoesPet.classList.add("border-danger");

            valido = false;

        } else {

            opcoesPet.classList.remove("border-danger");
        }

        // Nome do pet
        if (nomePet.value.trim().length < 2) {

            mostrarErro(
                nomePet,
                "Informe o nome do seu pet."
            );

            valido = false;
        }

        // Raça ou espécie
        if (raca.value.trim() === "") {

            mostrarErro(
                raca,
                "Informe a espécie ou raça do pet."
            );

            valido = false;
        }

        // Idade
        const idadePet = Number(idade.value);

        if (
            idade.value.trim() === "" ||
            !Number.isFinite(idadePet) ||
            idadePet <= 0
        ) {

            mostrarErro(
                idade,
                "Informe uma idade válida."
            );

            valido = false;
        }

        return valido;
    }

    /* =================================================
       VALIDAÇÃO DOS DADOS DO TUTOR
    ================================================= */

    function validarTutor() {

        let valido = true;

        // Nome completo
        if (nomeTutor.value.trim().length < 3) {

            mostrarErro(
                nomeTutor,
                "Informe seu nome completo."
            );

            valido = false;
        }

        // Celular
        const numeroCelular = celular.value.replace(/\D/g, "");

        if (
            numeroCelular.length !== 10 &&
            numeroCelular.length !== 11
        ) {

            mostrarErro(
                celular,
                "Informe um telefone válido com DDD."
            );

            valido = false;
        }

        // E-mail
        const emailValido = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        if (!emailValido.test(email.value.trim())) {

            mostrarErro(
                email,
                "Informe um endereço de e-mail válido."
            );

            valido = false;
        }

        return valido;
    }

    /* =================================================
       VALIDAÇÃO DO SERVIÇO
    ================================================= */

    function validarServico() {

        let valido = true;

        // Tipo de consulta
        if (consulta.selectedIndex < 0) {

            mostrarErro(
                consulta,
                "Selecione o tipo de consulta."
            );

            valido = false;
        }

        // Especialidade
        if (especialidade.selectedIndex === 0) {

            mostrarErro(
                especialidade,
                "Selecione uma especialidade."
            );

            valido = false;
        }

        // Data
        if (!data.value) {

            mostrarErro(
                data,
                "Selecione a data da consulta."
            );

            valido = false;

        } else {

            const [ano, mes, dia] = data.value.split("-").map(Number);

            const dataSelecionada = new Date(
                Date.UTC(ano, mes - 1, dia)
            );

            const dataAtual = new Date(
                `${obterDataLocal()}T00:00:00`
            );

            if (dataSelecionada < new Date(
                Date.UTC(
                    dataAtual.getFullYear(),
                    dataAtual.getMonth(),
                    dataAtual.getDate()
                )
            )) {

                mostrarErro(
                    data,
                    "A data não pode ser anterior ao dia de hoje."
                );

                valido = false;

            } else if (dataSelecionada.getUTCDay() === 0) {

                mostrarErro(
                    data,
                    "A clínica não realiza atendimentos aos domingos."
                );

                valido = false;
            }
        }

        // Horário
        if (horario.selectedIndex === 0) {

            mostrarErro(
                horario,
                "Selecione um horário disponível."
            );

            valido = false;
        }

        return valido;
    }

    /* =================================================
       LIMPAR ERROS AUTOMATICAMENTE
    ================================================= */

    document.querySelectorAll(
        "#nomePet, #raca, #idade, #nomeTutor, #celular, #email, #consulta, #especialidade, #data, #horario"
    ).forEach(campo => {

        campo.addEventListener("input", () => {
            limparErro(campo);
        });

        campo.addEventListener("change", () => {
            limparErro(campo);
        });

    });

    document.querySelectorAll('input[name="animal"]').forEach(radio => {

        radio.addEventListener("change", () => {

            opcoesPet.classList.remove("border-danger");

        });

    });

    /* =================================================
       CONFIRMAÇÃO DO AGENDAMENTO
    ================================================= */

    botaoConfirmar.addEventListener("click", () => {

        // Remove mensagens anteriores
        limparTodosErros();

        opcoesPet.classList.remove("border-danger");

        // Executa as validações
        const petValido = validarPet();
        const tutorValido = validarTutor();
        const servicoValido = validarServico();

        // Verifica se todos os campos estão corretos
        if (!petValido || !tutorValido || !servicoValido) {

            const primeiroErro = document.querySelector(".is-invalid");

            if (primeiroErro) {
                primeiroErro.focus();
            } else if (opcoesPet.classList.contains("border-danger")) {
                document.getElementById("cachorro").focus();
            }

            return;
        }

        // Formata a data para exibição
        const dataFormatada = new Date(
            `${data.value}T12:00:00`
        ).toLocaleDateString("pt-BR");

        // Obtém o animal selecionado
        const animal = document.querySelector(
            'input[name="animal"]:checked'
        );

        // Atualiza a mensagem de confirmação
        const corpoModal = modalElemento.querySelector(".modal-body");

        corpoModal.innerHTML = `
            <p>
                <strong>Pronto!</strong>
                A consulta do seu pet foi confirmada.
            </p>

            <div class="dados-confirmacao">
                <p><strong>Pet:</strong> ${nomePet.value.trim()}</p>
                <p><strong>Tutor:</strong> ${nomeTutor.value.trim()}</p>
                <p><strong>Animal:</strong> ${animal.value}</p>
                <p><strong>Consulta:</strong> ${consulta.value}</p>
                <p><strong>Especialidade:</strong> ${especialidade.value}</p>
                <p><strong>Data:</strong> ${dataFormatada}</p>
                <p class="mb-0"><strong>Horário:</strong> ${horario.value}</p>
            </div>

            <p class="mt-3 mb-0">
                Para consultar mais informações, acesse a
                <strong>Carteira-Pet</strong> pelo botão abaixo.
            </p>
        `;

        // Abre o modal
        modalConfirmacao.show();

        console.log("Agendamento validado com sucesso!");

    });

    /* =================================================
       FINALIZAÇÃO
    ================================================= */

    console.log(
        "Sistema de consultas Patas & Bigodes inicializado!"
    );

});

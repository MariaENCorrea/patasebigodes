
"use strict";

const HORARIOS_CLINICA = {
    0: null,
    1: { abertura: 9, fechamento: 20 },
    2: { abertura: 9, fechamento: 20 },
    3: { abertura: 9, fechamento: 20 },
    4: { abertura: 9, fechamento: 20 },
    5: { abertura: 9, fechamento: 20 },
    6: { abertura: 9, fechamento: 13 }
};

document.addEventListener("DOMContentLoaded", () => {

    const elementoStatus = document.querySelector(".status");
    const elementoProximo = document.querySelector(".proximo");
    const cardsHorarios = document.querySelectorAll(".horarios .card");

    atualizarStatus();
    destacarDiaAtual();
    atualizarAno();
    iniciarAnimacoes();

    setInterval(atualizarStatus, 60000);

    document.addEventListener("visibilitychange", () => {
        if (!document.hidden) {
            atualizarStatus();
            destacarDiaAtual();
        }
    });

    function atualizarStatus() {

        if (!elementoStatus || !elementoProximo) {
            return;
        }

        const agora = new Date();
        const diaAtual = agora.getDay();
        const horarioHoje = HORARIOS_CLINICA[diaAtual];

        const horaAtual = agora.getHours();
        const minutoAtual = agora.getMinutes();

        const minutosAtuais = horaAtual * 60 + minutoAtual;

        const estaAberta = horarioHoje !== null &&
            minutosAtuais >= horarioHoje.abertura * 60 &&
            minutosAtuais < horarioHoje.fechamento * 60;

        if (estaAberta) {
            mostrarStatusAberto(horarioHoje);
        } else {
            mostrarStatusFechado(agora);
        }
    }

    function mostrarStatusAberto(horario) {

        elementoStatus.classList.remove("fechado");
        elementoStatus.classList.add("aberto");

        elementoStatus.innerHTML = `
            <i class="bi bi-circle-fill"></i>
            ABERTO AGORA
        `;

        elementoProximo.textContent =
            `Estamos atendendo! Nosso expediente termina às ${formatarHora(horario.fechamento)}.`;
    }

    function mostrarStatusFechado(agora) {

        elementoStatus.classList.remove("aberto");
        elementoStatus.classList.add("fechado");

        elementoStatus.innerHTML = `
            <i class="bi bi-circle-fill"></i>
            FECHADO AGORA
        `;

        const proximoHorario = encontrarProximoAtendimento(agora);

        if (!proximoHorario) {
            elementoProximo.textContent =
                "Não há previsão de atendimento.";
            return;
        }

        const dataProximo = proximoHorario.data;
        const horarioProximo = proximoHorario.horario;

        const mesmoDia =
            agora.getFullYear() === dataProximo.getFullYear() &&
            agora.getMonth() === dataProximo.getMonth() &&
            agora.getDate() === dataProximo.getDate();

        const diaSemana = dataProximo.toLocaleDateString("pt-BR", {
            weekday: "long"
        });

        const hora = formatarHora(horarioProximo.abertura);

        if (mesmoDia) {
            elementoProximo.textContent =
                `Próximo atendimento hoje às ${hora}.`;
        } else {
            elementoProximo.textContent =
                `Próximo atendimento: ${diaSemana} às ${hora}.`;
        }
    }

    function encontrarProximoAtendimento(agora) {

        for (let i = 0; i <= 7; i++) {

            const dataVerificada = new Date(agora);
            dataVerificada.setDate(agora.getDate() + i);

            const horario = HORARIOS_CLINICA[dataVerificada.getDay()];

            if (!horario) {
                continue;
            }

            const horarioAbertura = new Date(dataVerificada);

            horarioAbertura.setHours(
                horario.abertura,
                0,
                0,
                0
            );

            if (horarioAbertura > agora) {
                return {
                    data: dataVerificada,
                    horario: horario
                };
            }
        }

        return null;
    }

    function formatarHora(hora) {
        return `${String(hora).padStart(2, "0")}:00`;
    }

    function destacarDiaAtual() {

        const diaAtual = new Date().getDay();

        cardsHorarios.forEach((card) => {

            card.classList.remove("hoje");

            const diasCard = card.dataset.dias;

            if (!diasCard) {
                return;
            }

            const dias = diasCard.split(",").map(Number);

            if (dias.includes(diaAtual)) {
                card.classList.add("hoje");
            }
        });
    }

    function atualizarAno() {

        const elementoAno = document.querySelector("#ano");

        if (elementoAno) {
            elementoAno.textContent = new Date().getFullYear();
        }
    }

    function iniciarAnimacoes() {

        const elementos = document.querySelectorAll(
            ".cabecalho-pagina, .horarios .card, .status-area, .emergencia"
        );

        if (!("IntersectionObserver" in window)) {
            return;
        }

        const observador = new IntersectionObserver((entradas, observer) => {

            entradas.forEach((entrada) => {

                if (entrada.isIntersecting) {
                    entrada.target.classList.add("visivel");
                    observer.unobserve(entrada.target);
                }
            });

        }, {
            threshold: 0.15
        });

        elementos.forEach((elemento) => {
            elemento.classList.add("animar");
            observador.observe(elemento);
        });
    }

});

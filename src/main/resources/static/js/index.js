
"use strict";

document.addEventListener("DOMContentLoaded", () => {
    iniciarAnimacoes();
    atualizarAno();
    iniciarRolagemSuave();
    verificarImagens();
    iniciarMenuMobile();
    destacarMenu();
});

/* ANIMAÇÕES AO ROLAR A PÁGINA */

function iniciarAnimacoes() {

    const elementos = document.querySelectorAll(
        ".conteudo, .animais, .sobre-conteudo, .sobre-imagem, " +
        ".card-servico, .chamada-conteudo, .titulo-secao, " +
        ".mapa-container, .endereco"
    );

    if (!("IntersectionObserver" in window)) {
        return;
    }

    const observador = new IntersectionObserver((entradas, observer) => {

        entradas.forEach((entrada) => {

            if (entrada.isIntersecting) {
                entrada.target.classList.add("is-visible");
                observer.unobserve(entrada.target);
            }

        });

    }, {
        threshold: 0.15,
        rootMargin: "0px 0px -40px 0px"
    });

    elementos.forEach((elemento) => {
        elemento.classList.add("reveal");
        observador.observe(elemento);
    });
}

/* ATUALIZAÇÃO AUTOMÁTICA DO ANO */

function atualizarAno() {

    const elementoAno = document.querySelector("#ano");

    if (elementoAno) {
        elementoAno.textContent = new Date().getFullYear();
    }
}

/* IDENTIFICAÇÃO DOS LINKS INTERNOS */

function obterDestino(link) {

    const href = link.getAttribute("href");

    if (!href || href === "#") {
        return null;
    }

    try {
        const url = new URL(href, window.location.href);

        if (
            url.origin !== window.location.origin ||
            url.pathname !== window.location.pathname ||
            !url.hash
        ) {
            return null;
        }

        return document.getElementById(
            decodeURIComponent(url.hash.substring(1))
        );

    } catch (erro) {
        return null;
    }
}

/* ROLAGEM SUAVE */

function iniciarRolagemSuave() {

    const links = document.querySelectorAll('a[href*="#"]');

    links.forEach((link) => {

        link.addEventListener("click", (evento) => {

            const secao = obterDestino(link);

            if (!secao) {
                return;
            }

            evento.preventDefault();

            secao.scrollIntoView({
                behavior: window.matchMedia(
                    "(prefers-reduced-motion: reduce)"
                ).matches ? "auto" : "smooth",
                block: "start"
            });

            if (secao.id) {
                history.replaceState(
                    null,
                    "",
                    "#" + encodeURIComponent(secao.id)
                );
            }

        });

    });
}

/* VERIFICAÇÃO DAS IMAGENS */

function verificarImagens() {

    const imagens = document.querySelectorAll("img");

    imagens.forEach((imagem) => {

        imagem.addEventListener("error", () => {

            console.warn(
                "Não foi possível carregar a imagem:",
                imagem.getAttribute("src")
            );

            imagem.classList.add("imagem-indisponivel");

        });

        if (imagem.complete && imagem.naturalWidth === 0) {
            imagem.classList.add("imagem-indisponivel");
        }

    });
}

/* MENU MOBILE */

function iniciarMenuMobile() {

    const menu = document.querySelector("#menuNavbar");

    if (!menu || typeof bootstrap === "undefined") {
        return;
    }

    const links = menu.querySelectorAll(".nav-link");

    links.forEach((link) => {

        link.addEventListener("click", () => {

            if (menu.classList.contains("show")) {

                const instancia =
                    bootstrap.Collapse.getOrCreateInstance(menu);

                instancia.hide();
            }

        });

    });
}

/* DESTAQUE DO MENU */

function destacarMenu() {

    const secoes = document.querySelectorAll("section[id]");

    const links = document.querySelectorAll(
        ".navbar-nav a[href*='#']"
    );

    if (!("IntersectionObserver" in window)) {
        return;
    }

    const observador = new IntersectionObserver((entradas) => {

        entradas.forEach((entrada) => {

            if (entrada.isIntersecting) {

                links.forEach((link) => {

                    link.classList.remove("ativo");

                    const destino = obterDestino(link);

                    if (destino === entrada.target) {
                        link.classList.add("ativo");
                    }

                });

            }

        });

    }, {
        threshold: 0.1,
        rootMargin: "-85px 0px -55% 0px"
    });

    secoes.forEach((secao) => {
        observador.observe(secao);
    });
}

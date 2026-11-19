javascript
document.addEventListener("DOMContentLoaded", () => {

    // Animação dos elementos ao rolar a página
    const elementosAnimados = document.querySelectorAll(
        ".card-principal, .card-recepcao, .beneficio, " +
        ".profissional, .agendamento, .info-item"
    );

    elementosAnimados.forEach(elemento => {
        elemento.classList.add("animacao-pendente");
    });

    if ("IntersectionObserver" in window) {
        const observador = new IntersectionObserver((entradas, observer) => {
            entradas.forEach(entrada => {
                if (entrada.isIntersecting) {
                    entrada.target.classList.add("visivel");
                    observer.unobserve(entrada.target);
                }
            });
        }, {
            threshold: 0.12
        });

        elementosAnimados.forEach(elemento => {
            observador.observe(elemento);
        });
    } else {
        elementosAnimados.forEach(elemento => {
            elemento.classList.add("visivel");
        });
    }

    // Efeito nos cards de benefícios
    const beneficios = document.querySelectorAll(".beneficio");

    beneficios.forEach(card => {
        card.addEventListener("mouseenter", () => {
            card.classList.add("beneficio-destaque");
        });

        card.addEventListener("mouseleave", () => {
            card.classList.remove("beneficio-destaque");
        });
    });

    // Menu ativo conforme a página atual
    const linksMenu = document.querySelectorAll(".menu-link");

    const paginaAtual = window.location.pathname
        .split("/")
        .pop()
        .toLowerCase();

    linksMenu.forEach(link => {
        const destino = link.getAttribute("href");

        if (!destino || destino.startsWith("#")) {
            return;
        }

        const paginaDestino = destino
            .split("/")
            .pop()
            .toLowerCase();

        if (paginaDestino === paginaAtual) {
            linksMenu.forEach(item => {
                item.classList.remove("ativo");
            });

            link.classList.add("ativo");
        }
    });

    // Fechar menu mobile após clicar em um link
    const menuPrincipal = document.getElementById("menuPrincipal");

    if (menuPrincipal && window.bootstrap) {
        const linksMobile = menuPrincipal.querySelectorAll(".menu-link");

        linksMobile.forEach(link => {
            link.addEventListener("click", () => {
                if (
                    window.innerWidth < 992 &&
                    menuPrincipal.classList.contains("show")
                ) {
                    const menu = bootstrap.Collapse.getOrCreateInstance(
                        menuPrincipal
                    );

                    menu.hide();
                }
            });
        });
    }

    // Navegação suave para âncoras
    const linksInternos = document.querySelectorAll('a[href^="#"]');

    linksInternos.forEach(link => {
        link.addEventListener("click", evento => {
            const destino = link.getAttribute("href");

            if (!destino || destino === "#") {
                return;
            }

            const secao = document.querySelector(destino);

            if (secao) {
                evento.preventDefault();

                secao.scrollIntoView({
                    behavior: "smooth",
                    block: "start"
                });
            }
        });
    });

    // Botão voltar ao topo
    const botaoTopo = document.createElement("button");

    botaoTopo.type = "button";
    botaoTopo.className = "botao-topo";
    botaoTopo.setAttribute("aria-label", "Voltar ao topo");
    botaoTopo.title = "Voltar ao topo";
    botaoTopo.innerHTML = '<i class="bi bi-arrow-up"></i>';

    document.body.appendChild(botaoTopo);

    const atualizarBotaoTopo = () => {
        botaoTopo.classList.toggle(
            "botao-topo-visivel",
            window.scrollY > 350
        );
    };

    window.addEventListener("scroll", atualizarBotaoTopo, {
        passive: true
    });

    atualizarBotaoTopo();

    botaoTopo.addEventListener("click", () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    });

    // Ano automático no rodapé
    const copyright = document.querySelector(".footer-content p");

    if (copyright) {
        copyright.innerHTML = copyright.innerHTML.replace(
            /©\s*\d{4}/,
            `© ${new Date().getFullYear()}`
        );
    }

    // Proteção contra erros nas imagens
    const imagens = document.querySelectorAll("img");

    imagens.forEach(imagem => {
        imagem.addEventListener("error", () => {
            imagem.classList.add("imagem-indisponivel");

            console.warn(
                "Não foi possível carregar a imagem:",
                imagem.getAttribute("src")
            );
        });
    });

});

document.addEventListener("DOMContentLoaded", function () {

    const formCadastro = document.getElementById("formCadastro");

    const nomePet = document.getElementById("nomePet");
    const tutorPet = document.getElementById("tutor");
    const racaPet = document.getElementById("raca");

    const inputCpf = document.getElementById("inputCpf");
    const inputCelular = document.getElementById("inputCelular");

    const campoBuscaMedica = document.getElementById("buscarAtualizacoes");
    const campoBuscaVacinas = document.getElementById("buscarVacinas");

    function exibirMensagem(mensagem, tipo = "success") {
        let alerta = document.getElementById("mensagemCarteirinha");

        if (!alerta) {
            alerta = document.createElement("div");
            alerta.id = "mensagemCarteirinha";
            document.querySelector("main").prepend(alerta);
        }

        alerta.className = `alert alert-${tipo}`;
        alerta.textContent = mensagem;

        alerta.scrollIntoView({
            behavior: "smooth",
            block: "nearest"
        });
    }

    // MÁSCARA CPF

    if (inputCpf) {
        inputCpf.addEventListener("input", function () {
            let valor = this.value.replace(/\D/g, "").substring(0, 11);

            valor = valor.replace(/^(\d{3})(\d)/, "$1.$2");
            valor = valor.replace(/^(\d{3})\.(\d{3})(\d)/, "$1.$2.$3");
            valor = valor.replace(/\.(\d{3})(\d{1,2})$/, ".$1-$2");

            this.value = valor;
        });
    }

    // MÁSCARA CELULAR

    if (inputCelular) {
        inputCelular.addEventListener("input", function () {
            let valor = this.value.replace(/\D/g, "").substring(0, 11);

            if (valor.length <= 10) {
                valor = valor.replace(/^(\d{2})(\d)/, "($1) $2");
                valor = valor.replace(/(\d{4})(\d)/, "$1-$2");
            } else {
                valor = valor.replace(/^(\d{2})(\d)/, "($1) $2");
                valor = valor.replace(/(\d{5})(\d)/, "$1-$2");
            }

            this.value = valor;
        });
    }

    // VALIDAR CPF

    function cpfValido(cpf) {
        cpf = cpf.replace(/\D/g, "");

        if (cpf.length !== 11 || /^(\d)\1{10}$/.test(cpf)) {
            return false;
        }

        for (let tamanho = 9; tamanho < 11; tamanho++) {
            let soma = 0;

            for (let i = 0; i < tamanho; i++) {
                soma += Number(cpf[i]) * (tamanho + 1 - i);
            }

            let digito = (soma * 10) % 11;

            if (digito === 10) {
                digito = 0;
            }

            if (digito !== Number(cpf[tamanho])) {
                return false;
            }
        }

        return true;
    }

    // VALIDAR FORMULÁRIO

    if (formCadastro) {
        formCadastro.addEventListener("submit", function (evento) {

            if (!formCadastro.checkValidity()) {
                evento.preventDefault();
                formCadastro.reportValidity();
                return;
            }

            if (inputCpf && inputCpf.value.trim()) {
                if (!cpfValido(inputCpf.value)) {
                    evento.preventDefault();

                    inputCpf.setCustomValidity("Informe um CPF válido.");
                    inputCpf.reportValidity();
                    inputCpf.setCustomValidity("");

                    return;
                }
            }
        });
    }

    // BUSCAR ATUALIZAÇÕES MÉDICAS

    if (campoBuscaMedica) {
        campoBuscaMedica.addEventListener("input", function () {
            const termo = this.value.toLocaleLowerCase("pt-BR").trim();

            document.querySelectorAll(".atualizacao-item").forEach(function (item) {
                item.hidden = !item.textContent.toLocaleLowerCase("pt-BR").includes(termo);
            });
        });
    }

    // BUSCAR VACINAS

    if (campoBuscaVacinas) {
        campoBuscaVacinas.addEventListener("input", function () {
            const termo = this.value.toLocaleLowerCase("pt-BR").trim();

            document.querySelectorAll(".vacina-item").forEach(function (item) {
                item.hidden = !item.textContent.toLocaleLowerCase("pt-BR").includes(termo);
            });
        });
    }

    // IMPRESSÃO

    const botaoImprimir = document.querySelector(".btn-imprimir");

    if (botaoImprimir) {
        botaoImprimir.addEventListener("click", function () {
            window.print();
        });
    }

});

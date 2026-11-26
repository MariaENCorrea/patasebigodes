
document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formularioCadastro");

    const nomeAnimal = document.getElementById("nomeAnimal");
    const idade = document.getElementById("idade");
    const especie = document.getElementById("especie");

    const proprietario = document.getElementById("proprietario");
    const cpf = document.getElementById("cpf");
    const numero = document.getElementById("numero");
    const email = document.getElementById("email");
    const senha = document.getElementById("senha");
    const cidade = document.getElementById("cidade");
    const bairro = document.getElementById("bairro");

    formulario.noValidate = true;

    function somenteNumeros(valor) {
        return valor.replace(/\D/g, "");
    }

    function mostrarErro(campo, mensagem) {

        campo.classList.add("is-invalid");
        campo.classList.remove("is-valid");

        let mensagemErro = campo.nextElementSibling;

        if (!mensagemErro ||
            !mensagemErro.classList.contains("invalid-feedback")) {

            mensagemErro = document.createElement("div");
            mensagemErro.className = "invalid-feedback";

            campo.insertAdjacentElement("afterend", mensagemErro);
        }

        mensagemErro.textContent = mensagem;
    }

    function mostrarSucesso(campo) {

        campo.classList.remove("is-invalid");
        campo.classList.add("is-valid");

        const mensagemErro = campo.nextElementSibling;

        if (mensagemErro &&
            mensagemErro.classList.contains("invalid-feedback")) {

            mensagemErro.textContent = "";
        }
    }

    function limparValidacao(campo) {

        campo.classList.remove("is-invalid", "is-valid");

        const mensagemErro = campo.nextElementSibling;

        if (mensagemErro &&
            mensagemErro.classList.contains("invalid-feedback")) {

            mensagemErro.textContent = "";
        }
    }

    function validarObrigatorio(campo, mensagem) {

        if (!campo.value.trim()) {

            mostrarErro(campo, mensagem);

            return false;
        }

        mostrarSucesso(campo);

        return true;
    }

    // MÁSCARA DE CPF

    cpf.addEventListener("input", function () {

        let valor = somenteNumeros(this.value).slice(0, 11);

        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

        this.value = valor;
    });

    // MÁSCARA DE CELULAR

    numero.addEventListener("input", function () {

        let valor = somenteNumeros(this.value).slice(0, 11);

        if (valor.length > 10) {

            valor = valor.replace(
                /^(\d{2})(\d{5})(\d{4})$/,
                "($1) $2-$3"
            );

        } else if (valor.length > 6) {

            valor = valor.replace(
                /^(\d{2})(\d{4})(\d{1,4})$/,
                "($1) $2-$3"
            );

        } else if (valor.length > 2) {

            valor = valor.replace(
                /^(\d{2})(\d+)/,
                "($1) $2"
            );

        } else if (valor.length > 0) {

            valor = valor.replace(/^(\d+)/, "($1");
        }

        this.value = valor;
    });

    // VALIDAÇÃO DE CPF

    function validarCPF(valor) {

        const numeroCPF = somenteNumeros(valor);

        if (
            numeroCPF.length !== 11 ||
            /^(\d)\1{10}$/.test(numeroCPF)
        ) {
            return false;
        }

        let soma = 0;

        for (let i = 0; i < 9; i++) {

            soma += Number(numeroCPF[i]) * (10 - i);
        }

        let digito1 = (soma * 10) % 11;

        if (digito1 === 10) {
            digito1 = 0;
        }

        if (digito1 !== Number(numeroCPF[9])) {
            return false;
        }

        soma = 0;

        for (let i = 0; i < 10; i++) {

            soma += Number(numeroCPF[i]) * (11 - i);
        }

        let digito2 = (soma * 10) % 11;

        if (digito2 === 10) {
            digito2 = 0;
        }

        return digito2 === Number(numeroCPF[10]);
    }

    // VALIDAÇÃO DE E-MAIL

    function validarEmail(valor) {

        const formatoEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

        return formatoEmail.test(valor);
    }

    // VALIDAÇÃO DE CELULAR

    function validarCelular(valor) {

        const telefone = somenteNumeros(valor);

        return telefone.length === 10 || telefone.length === 11;
    }

    // VALIDAÇÃO DOS DADOS DO ANIMAL

    function validarAnimal() {

        let valido = true;

        if (!validarObrigatorio(
            nomeAnimal,
            "Informe o nome do animal."
        )) {
            valido = false;
        }

        const sexoSelecionado = document.querySelector(
            'input[name="sexo"]:checked'
        );

        if (!sexoSelecionado) {

            alert("Selecione o sexo do animal.");

            valido = false;
        }

        if (idade.value.trim() !== "") {

            const idadeAnimal = Number(idade.value);

            if (
                !Number.isInteger(idadeAnimal) ||
                idadeAnimal < 0 ||
                idadeAnimal > 100
            ) {

                mostrarErro(
                    idade,
                    "Informe uma idade entre 0 e 100 anos."
                );

                valido = false;

            } else {

                mostrarSucesso(idade);
            }

        } else {

            limparValidacao(idade);
        }

        if (!especie.value) {

            mostrarErro(
                especie,
                "Selecione a espécie do animal."
            );

            valido = false;

        } else {

            mostrarSucesso(especie);
        }

        return valido;
    }

    // VALIDAÇÃO DOS DADOS DO PROPRIETÁRIO

    function validarProprietario() {

        let valido = true;

        if (!validarObrigatorio(
            proprietario,
            "Informe seu nome completo."
        )) {
            valido = false;
        }

        // CPF

        if (!validarObrigatorio(
            cpf,
            "Informe seu CPF."
        )) {

            valido = false;

        } else if (!validarCPF(cpf.value)) {

            mostrarErro(
                cpf,
                "Informe um CPF válido."
            );

            valido = false;

        } else {

            mostrarSucesso(cpf);
        }

        // CELULAR

        if (!validarObrigatorio(
            numero,
            "Informe seu número de celular."
        )) {

            valido = false;

        } else if (!validarCelular(numero.value)) {

            mostrarErro(
                numero,
                "Informe um celular com DDD válido."
            );

            valido = false;

        } else {

            mostrarSucesso(numero);
        }

        // E-MAIL

        if (!validarObrigatorio(
            email,
            "Informe seu e-mail."
        )) {

            valido = false;

        } else if (!validarEmail(email.value.trim())) {

            mostrarErro(
                email,
                "Informe um endereço de e-mail válido."
            );

            valido = false;

        } else {

            mostrarSucesso(email);
        }

        // SENHA

        if (!validarObrigatorio(
            senha,
            "Crie uma senha."
        )) {

            valido = false;

        } else if (senha.value.length < 6) {

            mostrarErro(
                senha,
                "A senha deve ter pelo menos 6 caracteres."
            );

            valido = false;

        } else {

            mostrarSucesso(senha);
        }

        // CIDADE

        if (!validarObrigatorio(
            cidade,
            "Informe sua cidade."
        )) {
            valido = false;
        }

        // BAIRRO

        if (!validarObrigatorio(
            bairro,
            "Informe seu bairro."
        )) {
            valido = false;
        }

        return valido;
    }

    // LIMPAR VALIDAÇÃO AO DIGITAR

    document.querySelectorAll(
        ".form-control, .form-select"
    ).forEach(function (campo) {

        campo.addEventListener("input", function () {
            limparValidacao(this);
        });

        campo.addEventListener("change", function () {
            limparValidacao(this);
        });

    });

    // FINALIZAR CADASTRO

    formulario.addEventListener("submit", function (evento) {

        const animalValido = validarAnimal();
        const proprietarioValido = validarProprietario();

        if (!animalValido || !proprietarioValido) {

            evento.preventDefault();

            const primeiroErro = document.querySelector(
                ".is-invalid"
            );

            if (primeiroErro) {

                primeiroErro.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });

                primeiroErro.focus();
            }

            return;
        }

        const confirmar = confirm(
            "Todos os dados foram preenchidos corretamente.\n\n" +
            "Deseja finalizar o cadastro?"
        );

        if (!confirmar) {

            evento.preventDefault();
        }

    });

});
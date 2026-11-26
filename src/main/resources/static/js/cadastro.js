
document.addEventListener("DOMContentLoaded", function () {

    const formulario = document.getElementById("formularioCadastro");

    if (!formulario) {
        return;
    }

    const nome = document.getElementById("nome");
    const cpf = document.getElementById("cpf");
    const telefone = document.getElementById("telefone");
    const email = document.getElementById("email");
    const senha = document.getElementById("senha");

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

    cpf.addEventListener("input", function () {
        let valor = somenteNumeros(this.value).slice(0, 11);

        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d)/, "$1.$2");
        valor = valor.replace(/(\d{3})(\d{1,2})$/, "$1-$2");

        this.value = valor;
    });

    telefone.addEventListener("input", function () {
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

    function validarEmail(valor) {
        const formatoEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return formatoEmail.test(valor);
    }

    function validarTelefone(valor) {
        const telefoneNumerico = somenteNumeros(valor);

        return telefoneNumerico.length === 10 ||
            telefoneNumerico.length === 11;
    }

    function validarFormulario() {
        let valido = true;

        if (!validarObrigatorio(nome, "Informe seu nome completo.")) {
            valido = false;
        }

        if (!validarObrigatorio(cpf, "Informe seu CPF.")) {
            valido = false;
        } else if (!validarCPF(cpf.value)) {
            mostrarErro(cpf, "Informe um CPF válido.");
            valido = false;
        } else {
            mostrarSucesso(cpf);
        }

        if (!validarObrigatorio(telefone, "Informe seu telefone.")) {
            valido = false;
        } else if (!validarTelefone(telefone.value)) {
            mostrarErro(telefone, "Informe um telefone válido com DDD.");
            valido = false;
        } else {
            mostrarSucesso(telefone);
        }

        if (!validarObrigatorio(email, "Informe seu e-mail.")) {
            valido = false;
        } else if (!validarEmail(email.value.trim())) {
            mostrarErro(email, "Informe um e-mail válido.");
            valido = false;
        } else {
            mostrarSucesso(email);
        }

        if (!validarObrigatorio(senha, "Crie uma senha.")) {
            valido = false;
        } else if (senha.value.length < 6) {
            mostrarErro(senha, "A senha deve ter pelo menos 6 caracteres.");
            valido = false;
        } else {
            mostrarSucesso(senha);
        }

        const camposObrigatorios = formulario.querySelectorAll(
            "input[required]"
        );

        camposObrigatorios.forEach(function (campo) {
            if (
                campo !== nome &&
                campo !== cpf &&
                campo !== telefone &&
                campo !== email &&
                campo !== senha
            ) {
                if (!validarObrigatorio(
                    campo,
                    "Preencha este campo."
                )) {
                    valido = false;
                }
            }
        });

        return valido;
    }

    formulario.querySelectorAll(
        ".form-control, .form-select"
    ).forEach(function (campo) {

        campo.addEventListener("input", function () {
            limparValidacao(this);
        });

        campo.addEventListener("change", function () {
            limparValidacao(this);
        });
    });

    formulario.addEventListener("submit", function (evento) {

        if (!validarFormulario()) {
            evento.preventDefault();

            const primeiroErro = document.querySelector(".is-invalid");

            if (primeiroErro) {
                primeiroErro.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });

                primeiroErro.focus();
            }

            return;
        }

        if (!confirm("Deseja finalizar o cadastro do tutor?")) {
            evento.preventDefault();
        }
    });

});

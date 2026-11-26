document.addEventListener("DOMContentLoaded", function () {

    const campoSenha = document.getElementById("senha");
    const botaoMostrar = document.getElementById("mostrarSenha");

    if (!campoSenha || !botaoMostrar) {
        return;
    }

    botaoMostrar.addEventListener("click", function () {

        const mostrar = campoSenha.type === "password";

        campoSenha.type = mostrar ? "text" : "password";

        const icone = botaoMostrar.querySelector("i");

        if (icone) {
            icone.classList.toggle("bi-eye", !mostrar);
            icone.classList.toggle("bi-eye-slash", mostrar);
        }

        botaoMostrar.setAttribute(
            "aria-label",
            mostrar ? "Ocultar senha" : "Mostrar senha"
        );
    });
});
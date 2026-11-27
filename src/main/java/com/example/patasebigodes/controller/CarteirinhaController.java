
package com.example.patasebigodes.controller;

import com.example.patasebigodes.model.Animal;
import com.example.patasebigodes.model.Tutor;
import com.example.patasebigodes.repository.AnimalRepository;
import com.example.patasebigodes.repository.TutorRepository;
import com.example.patasebigodes.repository.ConsultaRepository;
import com.example.patasebigodes.repository.AtualizacaoMedicaRepository;
import com.example.patasebigodes.repository.VacinaRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/carteirinha")
public class CarteirinhaController {

    private final AnimalRepository animalRepository;
    private final TutorRepository tutorRepository;
    private final AtualizacaoMedicaRepository atualizacaoMedicaRepository;
    private final VacinaRepository vacinaRepository;
    private final ConsultaRepository consultaRepository;

    public CarteirinhaController(
            AnimalRepository animalRepository,
            TutorRepository tutorRepository,
            AtualizacaoMedicaRepository atualizacaoMedicaRepository,
            VacinaRepository vacinaRepository,
            ConsultaRepository consultaRepository) {

        this.animalRepository = animalRepository;
        this.tutorRepository = tutorRepository;
        this.atualizacaoMedicaRepository = atualizacaoMedicaRepository;
        this.vacinaRepository = vacinaRepository;
        this.consultaRepository = consultaRepository;
    }

    @GetMapping
    public String carteirinha(HttpSession session, Model model) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);

        if (tutor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        model.addAttribute("tutor", tutor);
        model.addAttribute("animais", animais);

        if (animais.isEmpty()) {
            model.addAttribute("animal", new Animal());
            model.addAttribute("atualizacoes", List.of());
            model.addAttribute("vacinas", List.of());
            model.addAttribute("totalAtualizacoes", 0);
            model.addAttribute("totalVacinas", 0);
            model.addAttribute("semPet", true);

            if (!model.containsAttribute("avisoPet")) {
                model.addAttribute(
                        "avisoPet",
                        "Você ainda não possui nenhum pet cadastrado. " +
                                "Cadastre seu primeiro pet para visualizar a carteirinha."
                );
            }

            return "carteirinha";
        }

        return "redirect:/carteirinha/" + animais.get(0).getId();
    }

    @GetMapping("/{id}")
    public String visualizarCarteirinha(
            @PathVariable Long id,
            HttpSession session,
            Model model) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            return "redirect:/carteirinha";
        }

        carregarCarteirinha(animal, tutorId, model);

        return "carteirinha";
    }

    @PostMapping("/adicionar")
    public String adicionarAnimal(
            @RequestParam String nome,
            @RequestParam String especie,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) LocalDate dataNascimento,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) Double peso,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Tutor tutor = tutorRepository.findById(tutorId).orElse(null);

        if (tutor == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Animal animal = new Animal();

        animal.setNome(nome);
        animal.setEspecie(especie);
        animal.setRaca(raca);
        animal.setSexo(sexo);
        animal.setDataNascimento(dataNascimento);
        animal.setCor(cor);
        animal.setPeso(peso);
        animal.setTutor(tutor);

        Animal animalSalvo = animalRepository.save(animal);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Pet cadastrado com sucesso!"
        );

        return "redirect:/carteirinha/" + animalSalvo.getId();
    }

    @PostMapping("/{id}/atualizar")
    public String atualizarCadastro(
            @PathVariable Long id,
            @RequestParam String nomeAnimal,
            @RequestParam(required = false) LocalDate dataNascimento,
            @RequestParam(required = false) String sexo,
            @RequestParam(required = false) String raca,
            @RequestParam(required = false) String cor,
            @RequestParam(required = false) String nomeTutor,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String celular,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String bairro,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            return "redirect:/carteirinha";
        }

        Tutor tutor = animal.getTutor();

        if (tutor == null) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível localizar os dados do tutor."
            );

            return "redirect:/carteirinha/" + id;
        }

        if (cpf != null && !cpf.isBlank()
                && tutorRepository.existsByCpfAndIdNot(cpf, tutorId)) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este CPF já está cadastrado."
            );

            return "redirect:/carteirinha/" + id;
        }

        if (email != null && !email.isBlank()
                && tutorRepository.existsByEmailAndIdNot(email, tutorId)) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este e-mail já está cadastrado."
            );

            return "redirect:/carteirinha/" + id;
        }

        animal.setNome(nomeAnimal);
        animal.setDataNascimento(dataNascimento);
        animal.setSexo(sexo);
        animal.setRaca(raca);
        animal.setCor(cor);

        tutor.setNome(nomeTutor);
        tutor.setCpf(cpf);
        tutor.setTelefone(celular);
        tutor.setEmail(email);
        tutor.setCidade(cidade);
        tutor.setBairro(bairro);

        tutorRepository.save(tutor);
        animalRepository.save(animal);

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Cadastro atualizado com sucesso!"
        );

        return "redirect:/carteirinha/" + id;
    }

    @PostMapping("/{id}/foto")
    public String atualizarFoto(
            @PathVariable Long id,
            @RequestParam("foto") MultipartFile foto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            return "redirect:/carteirinha";
        }

        if (foto == null || foto.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Selecione uma imagem para enviar."
            );

            return "redirect:/carteirinha/" + id;
        }

        String tipo = foto.getContentType();

        if (tipo == null ||
                !(tipo.equals("image/jpeg")
                        || tipo.equals("image/png")
                        || tipo.equals("image/webp"))) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Envie uma imagem JPG, PNG ou WEBP."
            );

            return "redirect:/carteirinha/" + id;
        }

        try {
            Path pasta = Paths.get("uploads/pets")
                    .toAbsolutePath()
                    .normalize();

            Files.createDirectories(pasta);

            String extensao;

            if (tipo.equals("image/jpeg")) {
                extensao = ".jpg";
            } else if (tipo.equals("image/png")) {
                extensao = ".png";
            } else {
                extensao = ".webp";
            }

            String nomeArquivo = UUID.randomUUID() + extensao;

            Path caminhoArquivo = pasta.resolve(nomeArquivo);

            foto.transferTo(caminhoArquivo);

            animal.setFoto("/uploads/pets/" + nomeArquivo);

            animalRepository.save(animal);

            redirectAttributes.addFlashAttribute(
                    "sucesso",
                    "Foto atualizada com sucesso!"
            );

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Não foi possível salvar a foto."
            );
        }

        return "redirect:/carteirinha/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluirAnimal(
            @PathVariable Long id,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long tutorId = obterTutorId(session);

        if (tutorId == null) {
            return "redirect:/login";
        }

        Animal animal = animalRepository
                .findByIdAndTutor_Id(id, tutorId)
                .orElse(null);

        if (animal == null) {
            return "redirect:/carteirinha";
        }

        boolean possuiConsultas =
                consultaRepository.existsByAnimal_Id(id);

        boolean possuiAtualizacoes =
                atualizacaoMedicaRepository.existsByAnimal_Id(id);

        boolean possuiVacinas =
                vacinaRepository.existsByAnimal_Id(id);

        if (possuiConsultas || possuiAtualizacoes || possuiVacinas) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    "Este pet possui registros médicos ou consultas. " +
                            "Não é possível excluí-lo."
            );

            return "redirect:/carteirinha/" + id;
        }

        animalRepository.delete(animal);

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        if (animais.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "avisoPet",
                    "Pet excluído. Você ainda não possui pets cadastrados."
            );

            return "redirect:/carteirinha";
        }

        redirectAttributes.addFlashAttribute(
                "sucesso",
                "Pet excluído com sucesso."
        );

        return "redirect:/carteirinha/" + animais.get(0).getId();
    }

    private void carregarCarteirinha(
            Animal animal,
            Long tutorId,
            Model model) {

        List<Animal> animais =
                animalRepository.findByTutor_IdOrderByIdAsc(tutorId);

        List<?> atualizacoes =
                atualizacaoMedicaRepository
                        .findByAnimal_IdOrderByDataAtendimentoDesc(
                                animal.getId()
                        );

        List<?> vacinas =
                vacinaRepository
                        .findByAnimal_IdOrderByDataAplicacaoDesc(
                                animal.getId()
                        );

        model.addAttribute("animal", animal);
        model.addAttribute("animais", animais);
        model.addAttribute("tutor", animal.getTutor());

        model.addAttribute("atualizacoes", atualizacoes);
        model.addAttribute("vacinas", vacinas);

        model.addAttribute("totalAtualizacoes", atualizacoes.size());
        model.addAttribute("totalVacinas", vacinas.size());

        model.addAttribute("semPet", false);
    }

    private Long obterTutorId(HttpSession session) {

        Object tutorId = session.getAttribute("tutorId");

        if (tutorId instanceof Long) {
            return (Long) tutorId;
        }

        if (tutorId instanceof Integer) {
            return ((Integer) tutorId).longValue();
        }

        return null;
    }
}

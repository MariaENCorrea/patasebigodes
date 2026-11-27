
package com.example.patasebigodes.service;

import com.example.patasebigodes.model.Consulta;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultaPrazoService {

    public boolean podeEditar(Consulta consulta) {

        if (consulta == null || consulta.getData() == null
                || consulta.getHorario() == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime dataHoraConsulta = LocalDateTime.of(
                consulta.getData(),
                consulta.getHorario()
        );

        return !dataHoraConsulta.isBefore(agora.plusHours(24));
    }

    public boolean podeCancelar(Consulta consulta) {

        if (consulta == null || consulta.getData() == null
                || consulta.getHorario() == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime dataHoraConsulta = LocalDateTime.of(
                consulta.getData(),
                consulta.getHorario()
        );

        return !dataHoraConsulta.isBefore(agora.plusHours(4));
    }

    public void validarEdicao(Consulta consulta) {

        if (!podeEditar(consulta)) {
            throw new IllegalArgumentException(
                    "A edição só é permitida com pelo menos 24 horas de antecedência."
            );
        }
    }

    public void validarCancelamento(Consulta consulta) {

        if (!podeCancelar(consulta)) {
            throw new IllegalArgumentException(
                    "O cancelamento só é permitido com pelo menos 4 horas de antecedência."
            );
        }
    }
}

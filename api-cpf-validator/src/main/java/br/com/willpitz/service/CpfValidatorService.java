package br.com.willpitz.service;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.willpitz.exception.NotFoundException;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.lang.Boolean.TRUE;

@Service
public class CpfValidatorService {

    private final CPFValidator cpfValidator = new CPFValidator();

    public Mono<Boolean> validateCpf(final String cpf) {
        return Try.of(() -> {
                cpfValidator.assertValid(cpf);
                return Mono.just(TRUE);
            })
            .getOrElseThrow(throwable -> {
                throw new NotFoundException("O cpf informado é inválido");
            });
    }
}

package br.com.willpitz.api.service;

import br.com.caelum.stella.validation.CPFValidator;
import br.com.willpitz.exception.NotFoundException;
import br.com.willpitz.service.CpfValidatorService;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static java.lang.Boolean.TRUE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CpfValidatorServiceTest {

    @InjectMocks
    private CpfValidatorService service;
    private final CPFValidator cpfValidator = new CPFValidator();

    @Test
    void shouldValidateCpf() {
        final String randomValid = cpfValidator.generateRandomValid();

        var resultMono = service.validateCpf(randomValid);

        StepVerifier.create(resultMono)
            .expectNext(TRUE)
            .verifyComplete();
    }

    @Test
    void shouldReturnNotFoundException() {
        Try.run(() -> {
            service.validateCpf("invalidCpf");
        }).onFailure(throwable -> {
            assertEquals(throwable.getMessage(), "O cpf informado é inválido");
            assertEquals(throwable.getClass(), NotFoundException.class);
        });
    }
}

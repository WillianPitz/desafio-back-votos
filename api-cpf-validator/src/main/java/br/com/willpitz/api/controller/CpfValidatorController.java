package br.com.willpitz.api.controller;

import br.com.willpitz.service.CpfValidatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/validate")
@AllArgsConstructor
@Tag(name = "CPF")
public class CpfValidatorController {

    private CpfValidatorService service;

    @GetMapping
    @Operation(summary = "Valida um cpf")
    public Mono<Boolean> createPoll(@RequestParam String cpf) {
        return service.validateCpf(cpf);
    }
}

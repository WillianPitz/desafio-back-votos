package br.com.willpitz.api.client;

import br.com.willpitz.config.FeignErrorDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(
    name = "CpfValidatorClient",
    url = "${api.client.cpf-validator}",
    configuration = FeignErrorDecoder.class
)
public interface CpfValidatorClient {

    @GetMapping("/validate")
    Mono<Boolean> validateCpf(@RequestParam final String cpf);
}

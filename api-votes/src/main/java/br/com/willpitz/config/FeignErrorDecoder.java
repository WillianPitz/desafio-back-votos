package br.com.willpitz.config;

import br.com.willpitz.api.client.response.ClientErrorResponse;
import br.com.willpitz.exception.GenericIntegrationError;
import br.com.willpitz.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {
        var httpStatus = HttpStatus.valueOf(response.status());
        final String message = handleResponseBody(response);

        if (HttpStatus.NOT_FOUND.equals(httpStatus)) {
            throw new NotFoundException(message);
        }

        return new GenericIntegrationError();
    }

    private String handleResponseBody(Response response) {
        if (Objects.isNull(response.body())) {
            return null;
        }

        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(bodyIs, ClientErrorResponse.class).getMessage();
        } catch (IOException e) {
            log.error("Exception on handle response", e);

            return null;
        }
    }
}

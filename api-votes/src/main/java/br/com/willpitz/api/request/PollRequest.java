package br.com.willpitz.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PollRequest {

    @NotNull(message = "name is required")
    @Schema(description = "Nome da pauta")
    private String name;
    @NotNull(message = "description is required")
    @Schema(description = "Descrição da pauta")
    private String description;
}

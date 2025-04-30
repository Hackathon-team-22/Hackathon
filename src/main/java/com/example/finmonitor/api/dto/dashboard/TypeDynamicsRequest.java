package com.example.finmonitor.api.dto.dashboard;

import com.example.finmonitor.application.enums.Period;
import com.example.finmonitor.application.enums.TxnType;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
public class TypeDynamicsRequest {

    @NotNull
    private TxnType type;

    @NotNull
    private Period period;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime start;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime end;


}
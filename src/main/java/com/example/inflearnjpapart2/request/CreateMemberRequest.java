package com.example.inflearnjpapart2.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMemberRequest {
    // @NotNull = "" 허용
    // @NotEmpty = ""도 허용하지 않음. 0보다 큰 길이의 문자열이 들어가야 한다.
    @NotEmpty
    private String name;
}

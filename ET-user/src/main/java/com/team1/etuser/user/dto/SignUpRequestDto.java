package com.team1.etuser.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestDto {
    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, message = "아이디는 공백 없이 4글자 이상이어야 합니다.")
    @Pattern(regexp = "^[^\\s]*$", message = "아이디에는 공백을 포함할 수 없습니다.")
    private String uid;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 4, message = "비밀번호는 공백 없이 4글자 이상이어야 합니다.")
    @Pattern(regexp = "^[^\\s]*$", message = "비밀번호에는 공백을 포함할 수 없습니다.")
    private String pwd;

    @NotBlank(message = "이름을 입력해주세요.")
    @Pattern(regexp = "^[^\\s]*$", message = "이름에는 공백을 포함할 수 없습니다.")
    private String name;
}

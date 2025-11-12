package com.likelion.nextworld.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequest {

  @NotBlank(message = "이름은 필수 입력 항목입니다.")
  @Pattern(regexp = "^[가-힣]{2,10}$", message = "이름은 한글 2~10자 이내로 입력해주세요.")
  private String name; //

  @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
  @Pattern(
      regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[A-Za-z\\d]{8,}$",
      message = "닉네임은 영문과 숫자를 포함하여 8자 이상이어야 합니다.")
  private String nickname;

  @NotBlank(message = "이메일은 필수 입력 항목입니다.")
  @Email(message = "올바른 이메일 형식이 아닙니다.")
  private String email;

  @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
  @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
  private String passwordConfirm;
}

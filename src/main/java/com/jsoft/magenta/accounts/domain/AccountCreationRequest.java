package com.jsoft.magenta.accounts.domain;

import com.jsoft.magenta.util.validation.annotations.ValidImage;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationRequest
{
  @Valid
  private Account account;

  @ValidImage
  private MultipartFile coverImage;

  @ValidImage
  private MultipartFile profileImage;

  @ValidImage
  private MultipartFile logoImage;
}

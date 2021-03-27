package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.validation.annotations.ValidImage;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

  @Override
  public boolean isValid(MultipartFile file,
      ConstraintValidatorContext constraintValidatorContext) {
    if (file != null) {
      String contentType = file.getContentType();
      return contentType.equals(MediaType.IMAGE_JPEG_VALUE) || contentType
          .equals(MediaType.IMAGE_JPEG);
    }
    return true;
  }
}

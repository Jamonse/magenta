package com.jsoft.magenta.util.validation.validators;

import com.google.common.base.Strings;
import com.jsoft.magenta.util.validation.annotations.ValidPhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {
    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (Strings.isNullOrEmpty(phoneNumber))
            return false;
        final String phoneNumberPattern = "\\d{10}|(?:\\d{3}-)\\d{7}|(?:\\d{4}-)\\d{6}|(?:\\d{3}-){2}\\d{4}";
        Pattern pattern = Pattern.compile(phoneNumberPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}

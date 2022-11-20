package com.example.springbootproject.validation;

import lombok.SneakyThrows;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {


    @Override
    public void initialize(final ValidPassword validPassword) {

    }

    @SneakyThrows
    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        //customizing validation messages
        Properties props = new Properties();
        InputStream inputStream = getClass()
                .getClassLoader()
                .getResourceAsStream("passay.properties");
        props.load(inputStream);
        MessageResolver resolver = new PropertiesMessageResolver(props);

        PasswordValidator passwordValidator = new PasswordValidator(resolver, Arrays.asList(

        // length between 8 and 16 characters
//        new LengthRule(8, 16),

          // Relax password rules for testing
        new LengthRule(2, 16)

        // at least one upper-case character
//        new CharacterRule(EnglishCharacterData.UpperCase, 1),

        // at least one lower-case character
//        new CharacterRule(EnglishCharacterData.LowerCase, 1),

        // at least one digit character
//        new CharacterRule(EnglishCharacterData.Digit, 1),

        // at least one symbol (special character)
//        new CharacterRule(EnglishCharacterData.Special, 1)

        // no whitespace
//        new WhitespaceRule(),

        // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
//        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
        // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 12345)
//        new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));



        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }


        // sending all error messages at once
//        List<String> messages = passwordValidator.getMessages(result);
//        String messageTemplate = String.join(",", messages);
//        constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)
//                .addConstraintViolation()
//                .disableDefaultConstraintViolation();


        //Sending one message each time failed validation.
        String message =passwordValidator.getMessages(result)
                .stream()
                .findFirst()
                .get();
        constraintValidatorContext.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }


}
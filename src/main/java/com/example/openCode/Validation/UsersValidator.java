package com.example.openCode.Validation;

import com.example.openCode.CompilationModule.Model.Users.Users;
import com.example.openCode.CompilationModule.Repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UsersValidator implements Validator {

    UsersRepo usersRepo;

    @Autowired
    public UsersValidator(UsersRepo usersRepo) {
        this.usersRepo = usersRepo;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Users.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "username.empty");
        Users user = (Users) obj;
        if(usersRepo.findByUsername(user.getUsername()) != null) {
            errors.rejectValue("username", "username.exists");
        }
    }
}

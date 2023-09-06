package io.github.bialekmm.AccelDataManager.controller;

import io.github.bialekmm.AccelDataManager.entity.UserEntity;
import io.github.bialekmm.AccelDataManager.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserEntity user = new UserEntity();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@ModelAttribute("user") UserEntity userEntity, BindingResult result, Model model){
        UserEntity existingUser = userService.findByEmail(userEntity.getEmail());

        if(existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()){
            result.rejectValue("email", "!email", "There is already an account registered with the same email");
        }
        if(result.hasErrors()){
            model.addAttribute("user", userEntity);
            return "/register";
        }
        userService.saveUser(userEntity);
        return "redirect:/register?success";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

}

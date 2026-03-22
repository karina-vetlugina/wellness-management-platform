package ca.gbc.comp3095.apigateway.controller;



import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('admin')")
    public String test() {
        return "hi_admin";
    }


    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('user')")
    public String test2() {
        return "hi_user";
    }

}

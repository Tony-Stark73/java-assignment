package portfolio.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String testHome() {
        return "App is running!";
    }

    @GetMapping("/hello")
    public String testHello() {
        return "Portfolio system OK!";
    }
}

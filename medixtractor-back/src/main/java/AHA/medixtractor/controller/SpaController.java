package AHA.medixtractor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping({"/recherche", "/medicament/{cis}"})
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}

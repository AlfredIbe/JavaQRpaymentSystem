package com.UNN.xchange.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/landing.html"; // Redirect to the static index.html file
    }

    @GetMapping("/loginSeller")
    public String sellerLogin() {
        return "/Sign_in(seller).html"; // Redirect to the static index.html file
    }

    @GetMapping("/loginBuyer")
    public String buyerLogin() {
        return "/Sign_in(buyyer).html"; // Redirect to the static index.html file
    }
}

package pl.arturzaczek.makaoweb.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping(value = {"/","/home", "/index"})
    public String getHome(){
        return "home";
    }
}

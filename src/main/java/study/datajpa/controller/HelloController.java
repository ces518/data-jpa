package study.datajpa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * User: june
 * Date: 2020-02-22
 * Time: 22:34
 **/
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello () {
        return "hello";
    }
}

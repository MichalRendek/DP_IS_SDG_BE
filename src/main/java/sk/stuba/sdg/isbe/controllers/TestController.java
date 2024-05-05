package sk.stuba.sdg.isbe.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;

@RestController
@RequestMapping("api/test/exceptions")
public class TestController {

    @GetMapping("/notFound")
    public void throwCustomExceptionExample() {
        throw new NotFoundCustomException("custom not_found_exception message");
    }
}

package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.services.BoxService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/boxes")
public class BoxController {

    private final BoxService boxService;
    private final ValidateInputService validateInputService;

    public BoxController(BoxService boxService, ValidateInputService validateInputService) {
        this.boxService = boxService;
        this.validateInputService = validateInputService;
    }

    @GetMapping
    public ResponseEntity<List<Box>> getAll(){
        try{
            List<Box> boxes = boxService.getAll();
            return new ResponseEntity<>(boxes, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Box box, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            boxService.save(box);
            return new ResponseEntity<>(box, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.ClientsTransport;
import ru.pin120.carwashAPI.services.BoxService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

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

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid Box box, BindingResult bindingResult){
        try{
            Optional<Box> boxOptional = boxService.getById(id);
            if(boxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Бокс с номером = %d не существует в базе данных",id), HttpStatus.BAD_REQUEST);
            }
            Box existedBox = boxOptional.get();
            if(existedBox.getBoxId().longValue() != box.getBoxId().longValue()){
                return new ResponseEntity<>("Параметр (номер) не совпадает с номером бокса", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            existedBox.setBoxStatus(box.getBoxStatus());
            boxService.save(existedBox);

            return new ResponseEntity<>(existedBox, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Box> boxOptional = boxService.getById(id);
            if(boxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Бокс с номером = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Box existedBox = boxOptional.get();
            // условие, что нельзя удалять бокс
//            if(!existedTransport.getBookings().isEmpty()){
//                return new ResponseEntity<>(String.format("Нельзя удалить транспорт %s %s, так как он указан в заказе", existedTransport.getTransport().getTrMark(), existedTransport.getTransport().getTrModel()), HttpStatus.BAD_REQUEST);
//            }

            boxService.delete(existedBox.getBoxId());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

}

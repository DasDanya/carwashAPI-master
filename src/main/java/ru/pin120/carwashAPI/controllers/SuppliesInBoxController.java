package ru.pin120.carwashAPI.controllers;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.AddSuppliesFromBoxDTO;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.SuppliesInBox;
import ru.pin120.carwashAPI.models.Supply;
import ru.pin120.carwashAPI.services.SuppliesInBoxService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/suppliesInBox")
public class SuppliesInBoxController {

    private final ValidateInputService validateInputService;
    private final SuppliesInBoxService suppliesInBoxService;

    public SuppliesInBoxController(ValidateInputService validateInputService, SuppliesInBoxService suppliesInBoxService) {
        this.validateInputService = validateInputService;
        this.suppliesInBoxService = suppliesInBoxService;
    }

    @GetMapping("/byBox")
    public ResponseEntity<?> getByBox(@RequestParam(value = "boxId") Long boxId,
                                      @RequestParam(value = "pageIndex") int pageIndex,
                                      @RequestParam(value = "name", required = false) String supName,
                                      @RequestParam(value = "category", required = false) String supCategory,
                                      @RequestParam(value = "operator", required = false) String operator,
                                      @RequestParam(value = "count", required = false) Integer supCount){
        try{
            List<SuppliesInBox> suppliesInBox = suppliesInBoxService.get(boxId, pageIndex, supName, supCategory, operator, supCount);
            return new ResponseEntity<>(suppliesInBox, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody @Valid SuppliesInBox suppliesInBox, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            SuppliesInBox supplies = suppliesInBoxService.add(suppliesInBox);
            return new ResponseEntity<>(supplies, HttpStatus.OK);
        }catch (Exception e){
            if(e instanceof EntityNotFoundException || e instanceof IllegalArgumentException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<SuppliesInBox> suppliesInBox = suppliesInBoxService.getById(id);
            if(suppliesInBox.isEmpty()){
                return new ResponseEntity<>(String.format("Запись с id = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            suppliesInBoxService.delete(suppliesInBox.get());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/transferToWarehouse/{id}")
    public ResponseEntity<?> transferToWarehouse(@PathVariable("id") Long id, @RequestBody @Valid AddSuppliesFromBoxDTO addSuppliesFromBoxDTO, BindingResult bindingResult){
        try{
            Optional<SuppliesInBox> suppliesInBoxOptional = suppliesInBoxService.getById(id);
            if(suppliesInBoxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Запись с id = %d не существует в базе данных", id), HttpStatus.BAD_REQUEST);
            }
            SuppliesInBox existedsuppliesInBox = suppliesInBoxOptional.get();
            if(existedsuppliesInBox.getSibId().longValue() != addSuppliesFromBoxDTO.getSuppliesInBox().getSibId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id изменяемой записи", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            suppliesInBoxService.transferToWarehouse(addSuppliesFromBoxDTO, existedsuppliesInBox);
            return new ResponseEntity<>(existedsuppliesInBox, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid SuppliesInBox suppliesInBox, BindingResult bindingResult){
        try{
            Optional<SuppliesInBox> suppliesInBoxOptional = suppliesInBoxService.getById(id);
            if(suppliesInBoxOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Запись с id = %d не существует в базе данных", id), HttpStatus.BAD_REQUEST);
            }
            SuppliesInBox existedsuppliesInBox = suppliesInBoxOptional.get();
            if(existedsuppliesInBox.getSibId().longValue() != suppliesInBox.getSibId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id изменяемой записи", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(suppliesInBoxService.existsOther(suppliesInBox)){
                return new ResponseEntity<>(String.format("В базе данных уже существует запись об автомоечном средстве с id = %d в боксе %d", suppliesInBox.getSupply().getSupId(), suppliesInBox.getBox().getBoxId()), HttpStatus.BAD_REQUEST);
            }

            suppliesInBoxService.edit(suppliesInBox, existedsuppliesInBox);
            return new ResponseEntity<>(existedsuppliesInBox, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

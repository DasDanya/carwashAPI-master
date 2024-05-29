package ru.pin120.carwashAPI.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.carwashAPI.Exceptions.FileIsNotImageException;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.Supply;
import ru.pin120.carwashAPI.services.SupplyService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/supplies")
public class SupplyController {

    private final ValidateInputService validateInputService;
    private final SupplyService supplyService;

    public SupplyController(ValidateInputService validateInputService, SupplyService supplyService) {
        this.validateInputService = validateInputService;
        this.supplyService = supplyService;
    }

    @GetMapping
    public ResponseEntity<?> get(@RequestParam(value = "pageIndex") Integer pageIndex,
                                 @RequestParam(value = "name", required = false) String supName,
                                 @RequestParam(value = "category", required = false) String supCategory,
                                 @RequestParam(value = "operator", required = false) String operator,
                                 @RequestParam(value = "count", required = false) Integer supCount) {
        try{
            List<Supply> supplies;
            if(supName == null && supCategory == null && operator == null && supCount == null){
                supplies = supplyService.get(pageIndex);
            }else{
                supplies = supplyService.search(pageIndex, supName, supCategory, operator, supCount);
            }

            return new ResponseEntity<>(supplies, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getPhoto/{photoName}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable("photoName")String photoName){
        try{
            return new ResponseEntity<>(supplyService.getPhoto(photoName), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestPart @Valid Supply supply, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(supplyService.exists(supply)){
                return new ResponseEntity<>(String.format("В базе данных уже существует автомоечное средство с названием %s, с категорией %s и объёмом/количеством единицы средства = %d",
                       supply.getSupName(), supply.getCategory().getCSupName(), supply.getSupMeasure()), HttpStatus.CONFLICT);
            }
            supplyService.create(supply, photo);
            return new ResponseEntity<>(supply, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof FileIsNotImageException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Supply> supplyOptional = supplyService.getById(id);
            if(supplyOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Автомоечное средство с id = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Supply existedSupply = supplyOptional.get();
            if(!existedSupply.getSuppliesInBoxes().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить автомоечное средство %s %s, так как оно указано в боксе %d", existedSupply.getCategory().getCSupName(), existedSupply.getSupName(), existedSupply.getSuppliesInBoxes().get(0).getBox().getBoxId()), HttpStatus.BAD_REQUEST);
            }else{
                supplyService.delete(existedSupply);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestPart @Valid Supply supply, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            Optional<Supply> supplyOptional = supplyService.getById(id);
            if(supplyOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Автомоечное средство с id = %d не существует в базе данных", id), HttpStatus.BAD_REQUEST);
            }
            Supply existedSupply = supplyOptional.get();
            if(existedSupply.getSupId().longValue() != supply.getSupId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id автомоечного средства", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
//            if(supplyService.existsWithoutCurrent(supply)){
//                return new ResponseEntity<>(String.format("В базе данных уже существует автомоечное средство с названием %s, с категорией %s и объёмом/количеством единицы средства = %d",
//                        supply.getSupName(), supply.getCategory().getCSupName(), supply.getSupMeasure()), HttpStatus.CONFLICT);
//            }

            supplyService.edit(existedSupply, supply, photo);

            return new ResponseEntity<>(existedSupply, HttpStatus.OK);

        }catch (Exception e){
            if(e instanceof FileIsNotImageException){
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }else {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }
}

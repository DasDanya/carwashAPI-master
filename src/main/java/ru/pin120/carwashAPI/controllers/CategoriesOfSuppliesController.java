package ru.pin120.carwashAPI.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.CategoryOfSupplies;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.models.Service;
import ru.pin120.carwashAPI.services.CategoriesOfSuppliesService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoriesOfSupplies")
public class CategoriesOfSuppliesController {

    private final CategoriesOfSuppliesService categoriesOfSuppliesService;
    private final ValidateInputService validateInputService;

    public CategoriesOfSuppliesController(CategoriesOfSuppliesService categoriesOfSuppliesService, ValidateInputService validateInputService) {
        this.categoriesOfSuppliesService = categoriesOfSuppliesService;
        this.validateInputService = validateInputService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryOfSupplies>> get(@RequestParam(value = "csupName", required = false) String csupName){
        try{
            List<CategoryOfSupplies> categories;
            if(csupName == null) {
                categories = categoriesOfSuppliesService.getAll();
            }else{
                categories = categoriesOfSuppliesService.search(csupName);
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryOfSupplies categoryOfSupplies, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoriesOfSuppliesService.exists(categoryOfSupplies)){
                return new ResponseEntity<>(String.format("Категория %s уже существует (без учёта регистра)", categoryOfSupplies.getCSupName()), HttpStatus.CONFLICT);
            }

            categoriesOfSuppliesService.save(categoryOfSupplies);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfSupplies, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{cSupName}")
    public ResponseEntity<String> deleteService(@PathVariable("cSupName") String cSupName){
        try{

            Optional<CategoryOfSupplies> categoryOfSuppliesOptional = categoriesOfSuppliesService.getByCSupName(cSupName);
            if(categoryOfSuppliesOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Категория %s отсутствует в базе данных", cSupName),HttpStatus.BAD_REQUEST);
            }

            CategoryOfSupplies categoryOfSupplies = categoryOfSuppliesOptional.get();

            // написать условие для удаления
//            if(!category.getServices().isEmpty()){
//                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как у неё есть услуги", category.getCatName()), HttpStatus.BAD_REQUEST);
//            }else{
//                categoryOfServicesService.delete(category.getCatName());
//            }
            categoriesOfSuppliesService.delete(categoryOfSupplies);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

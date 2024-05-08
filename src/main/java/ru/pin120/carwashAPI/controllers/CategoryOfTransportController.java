package ru.pin120.carwashAPI.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.services.CategoryOfTransportService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoryOfTransport")
public class CategoryOfTransportController {

    private final CategoryOfTransportService categoryOfTransportService;

    private final ValidateInputService validateInputService;

    public CategoryOfTransportController(CategoryOfTransportService categoryOfTransportService, ValidateInputService validateInputService) {
        this.categoryOfTransportService = categoryOfTransportService;
        this.validateInputService = validateInputService;
    }


    @GetMapping
    public ResponseEntity<List<CategoryOfTransport>> get(){
        List<CategoryOfTransport> categoriesOfCars = null;
        try{
            categoriesOfCars = categoryOfTransportService.getAll();
        }catch (Exception e){
            return new ResponseEntity<>(categoriesOfCars, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesOfCars, HttpStatus.OK);
    }

    @GetMapping("/{catTrName}")
    public ResponseEntity<List<CategoryOfTransport>> getByCatTrName(@PathVariable("catTrName") String catTrName){
        List<CategoryOfTransport> categoriesOfCars = null;
        try{
            //catTrName = URLDecoder.decode(catTrName, "UTF-8");
            categoriesOfCars = categoryOfTransportService.getByTrNameIgnoreCase(catTrName);
        }catch (Exception e){
            return new ResponseEntity<>(categoriesOfCars, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesOfCars, HttpStatus.OK);
    }

    @GetMapping("/emptyCategoryTransport/{servName}")
    public ResponseEntity<List<CategoryOfTransport>> getCategoryTransportWithoutPriceAndTime(@PathVariable(name = "servName") String servName){
        List<CategoryOfTransport> categoryOfTransports = null;
        try{
            //servName = URLDecoder.decode(servName, "UTF-8");
            categoryOfTransports = categoryOfTransportService.getCategoriesOfTransportWithoutPriceAndTime(servName);
        }catch (Exception e){
            return new ResponseEntity<>(categoryOfTransports, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfTransports, HttpStatus.OK);
    }



    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryOfTransport categoryOfTransport, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoryOfTransportService.existsByCatTrName(categoryOfTransport.getCatTrName())){
                return new ResponseEntity<>(String.format("Категория %s уже существует", categoryOfTransport.getCatTrName()), HttpStatus.CONFLICT);
            }
            categoryOfTransportService.save(categoryOfTransport);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfTransport, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id")  Long id){
        try{
            Optional<CategoryOfTransport> categoryOfCars = categoryOfTransportService.getById(id);
            if(categoryOfCars.isEmpty()){
                return new ResponseEntity<>(String.format("Категория с id = %d отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            CategoryOfTransport existedCategoryOfTransport = categoryOfCars.get();
            if(!existedCategoryOfTransport.getCars().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как к данной категории привязан транспорт", existedCategoryOfTransport.getCatTrName()), HttpStatus.BAD_REQUEST);
            }

            categoryOfTransportService.delete(existedCategoryOfTransport.getCatTrName());

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid CategoryOfTransport categoryOfTransport, BindingResult bindingResult){
        try{
            Optional<CategoryOfTransport> optionalCategoryOfTransport = categoryOfTransportService.getById(id);
            if(optionalCategoryOfTransport.isEmpty()){
                return new ResponseEntity<>(String.format("Категория с id = %d не существует",id), HttpStatus.BAD_REQUEST);
            }
            CategoryOfTransport existsCategoryOfTransport = optionalCategoryOfTransport.get();
            if(existsCategoryOfTransport.getCatTrId().longValue() != categoryOfTransport.getCatTrId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id категории", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoryOfTransportService.existsCategoryOfTransport(categoryOfTransport.getCatTrName(), categoryOfTransport.getCatTrId())){
                return new ResponseEntity<>(String.format("В базе данных уже существует категория с таким названием: %s (без учёта регистра)",categoryOfTransport.getCatTrName()), HttpStatus.CONFLICT);
            }


            existsCategoryOfTransport.setCatTrName(categoryOfTransport.getCatTrName());
            categoryOfTransportService.save(existsCategoryOfTransport);

            return new ResponseEntity<>(existsCategoryOfTransport, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

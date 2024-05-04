package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.BindWithCategoryDTO;
import ru.pin120.carwashAPI.dtos.CategoriesWithServicesDTO;
import ru.pin120.carwashAPI.dtos.EditCategoryOrServiceDTO;
import ru.pin120.carwashAPI.models.CategoryOfServices;
import ru.pin120.carwashAPI.services.CategoryOfServicesService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoryOfServices")
public class CategoryOfServicesController {

    private final CategoryOfServicesService categoryOfServicesService;
    private final ValidateInputService validateInputService;

    @Autowired
    private MessageSource messageSource;

    public CategoryOfServicesController(CategoryOfServicesService categoryOfServicesService, ValidateInputService validateInputService) {
        this.categoryOfServicesService = categoryOfServicesService;
        this.validateInputService = validateInputService;

    }

    @GetMapping
    public ResponseEntity<List<CategoryOfServices>> getAll(){
        List<CategoryOfServices> categories = new ArrayList<>();
        try{
            categories = (List<CategoryOfServices>) categoryOfServicesService.getAll();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>(categories, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/getAllCatNames")
    public ResponseEntity<List<String>> getAllCatNames(){
        List<String> catNames = new ArrayList<>();
        try{
            catNames = categoryOfServicesService.getCatNames();
        }catch (Exception e){
            return new ResponseEntity<>(catNames, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(catNames, HttpStatus.OK);
    }

    @GetMapping("/getCatNamesByParameter")
    public ResponseEntity<List<String>> getCatNamesByParameter(@RequestParam ("parameter") String parameter){
        List<String> catNames = null;
        try{
            catNames = categoryOfServicesService.getCatNamesByParameter(parameter);
        }catch (Exception e){
            return new ResponseEntity<>(catNames, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(catNames, HttpStatus.OK);
    }

    @GetMapping("/getCategoriesWithServices")
    public ResponseEntity<List<CategoriesWithServicesDTO>> getCategoriesWithServices(){
        List<CategoriesWithServicesDTO> categoriesWithServices = null;
        try{
            categoriesWithServices = categoryOfServicesService.getCategoriesWithServices();
        }catch (Exception e){
            return new ResponseEntity<>(categoriesWithServices, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesWithServices, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCategoryOfServices(@RequestBody @Valid CategoryOfServices categoryOfServices, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoryOfServicesService.existsCategoryOfServices(categoryOfServices.getCatName())){
                return new ResponseEntity<>(String.format("Категория %s уже существует", categoryOfServices.getCatName()), HttpStatus.CONFLICT);
            }
            categoryOfServicesService.create(categoryOfServices);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfServices, HttpStatus.OK);
    }

    @PutMapping("/edit")
    public ResponseEntity<String> editCategoryOfServices(@RequestBody @Valid EditCategoryOrServiceDTO editCategoryOrServiceDTO, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            } else {
                if(!editCategoryOrServiceDTO.getNewName().equalsIgnoreCase(editCategoryOrServiceDTO.getPastName())) {
                    if (categoryOfServicesService.existsCategoryOfServices(editCategoryOrServiceDTO.getNewName())) {
                        return new ResponseEntity<>(String.format("Категория %s уже существует", editCategoryOrServiceDTO.getNewName()), HttpStatus.CONFLICT);
                    }

                    categoryOfServicesService.edit(editCategoryOrServiceDTO);
                }
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(String.format("Успешное изменение название категории: с %s на %s !", editCategoryOrServiceDTO.getPastName(), editCategoryOrServiceDTO.getNewName()), HttpStatus.OK);
    }

//    @DeleteMapping("/delete")
//    public ResponseEntity<String> deleteCategoryOfServices(@RequestBody @Valid CategoryOfServices categoryOfServices, BindingResult bindingResult){
//        if (bindingResult.hasErrors()) {
//            return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
//        }
//        try{
//            Optional<CategoryOfServices> categoryOfServicesOptional = categoryOfServicesService.getCategoryByName(categoryOfServices.getCatName());
//            if(categoryOfServicesOptional.isEmpty()){
//                return new ResponseEntity<>(String.format("Категория %s отсутствует в базе данных", categoryOfServices.getCatName()),HttpStatus.BAD_REQUEST);
//            }
//
//            CategoryOfServices category = categoryOfServicesOptional.get();
//            if(!category.getServices().isEmpty()){
//                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как у неё есть услуги", category.getCatName()), HttpStatus.BAD_REQUEST);
//            }else{
//                categoryOfServicesService.delete(category.getCatName());
//            }
//
//        }catch (Exception e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>(String.format("Категория %s успешно удалена!", categoryOfServices.getCatName()), HttpStatus.NO_CONTENT);
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCategoryOfServices(@PathVariable("id") String id){
        try{
            Optional<CategoryOfServices> categoryOfServicesOptional = categoryOfServicesService.getCategoryByName(id);
            if(categoryOfServicesOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Категория %s отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            CategoryOfServices category = categoryOfServicesOptional.get();
            if(!category.getServices().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как у неё есть услуги", category.getCatName()), HttpStatus.BAD_REQUEST);
            }else{
                categoryOfServicesService.delete(category.getCatName());
            }

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }


}

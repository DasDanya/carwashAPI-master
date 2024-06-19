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

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о категориях услуг
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoryOfServices")
public class CategoryOfServicesController {

    /**
     * Сервис для работы с категориями услуг
     */
    private final CategoryOfServicesService categoryOfServicesService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param categoryOfServicesService сервис для работы с категориями услуг
     * @param validateInputService сервис для валидации входных данных
     */
    public CategoryOfServicesController(CategoryOfServicesService categoryOfServicesService, ValidateInputService validateInputService) {
        this.categoryOfServicesService = categoryOfServicesService;
        this.validateInputService = validateInputService;

    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий услуг
     * @return ResponseEntity со списком категорий услуг и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> getAll(){
        List<CategoryOfServices> categories;
        try{
            categories = (List<CategoryOfServices>) categoryOfServicesService.getAll();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка названий категорий
     * @return ResponseEntity со списком названий категорий и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getAllCatNames")
    public ResponseEntity<?> getAllCatNames(){
        List<String> catNames;
        try{
            catNames = categoryOfServicesService.getCatNames();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(catNames, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий услуг по заданному параметру поиска
     * @param parameter параметр поиска
     * @return ResponseEntity со списком названий категорий и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getCatNamesByParameter/{parameter}")
    public ResponseEntity<?> getCatNamesByParameter(@PathVariable ("parameter") String parameter){
        List<String> catNames = null;
        try{
            //parameter = URLDecoder.decode(parameter, "UTF-8");
            catNames = categoryOfServicesService.getCatNamesByParameter(parameter);
        }catch (Exception e){
            return new ResponseEntity<>(catNames, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(catNames, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий вместе с её услугами
     * @return ResponseEntity со списком категорий и её услуг и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getCategoriesWithServices")
    public ResponseEntity<?> getCategoriesWithServices(){
        List<CategoriesWithServicesDTO> categoriesWithServices = null;
        try{
            categoriesWithServices = categoryOfServicesService.getCategoriesWithServices();
        }catch (Exception e){
            return new ResponseEntity<>(categoriesWithServices, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesWithServices, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление категории услуг
     * @param categoryOfServices категория услуг
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленной категорией и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> createCategoryOfServices(@RequestBody @Valid CategoryOfServices categoryOfServices, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoryOfServicesService.existsCategoryOfServices(categoryOfServices.getCatName())){
                return new ResponseEntity<>(String.format("Категория %s уже существует (без учёта регистра)", categoryOfServices.getCatName()), HttpStatus.CONFLICT);
            }
            categoryOfServicesService.create(categoryOfServices);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfServices, HttpStatus.OK);
    }

//    @PutMapping("/edit")
//    public ResponseEntity<String> editCategoryOfServices(@RequestBody @Valid EditCategoryOrServiceDTO editCategoryOrServiceDTO, BindingResult bindingResult){
//        try {
//            if (bindingResult.hasErrors()) {
//                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
//            } else {
//                if(!editCategoryOrServiceDTO.getNewName().equalsIgnoreCase(editCategoryOrServiceDTO.getPastName())) {
//                    if (categoryOfServicesService.existsCategoryOfServices(editCategoryOrServiceDTO.getNewName())) {
//                        return new ResponseEntity<>(String.format("Категория %s уже существует", editCategoryOrServiceDTO.getNewName()), HttpStatus.CONFLICT);
//                    }
//
//                    categoryOfServicesService.edit(editCategoryOrServiceDTO);
//                }
//            }
//        }catch (Exception e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>(String.format("Успешное изменение название категории: с %s на %s !", editCategoryOrServiceDTO.getPastName(), editCategoryOrServiceDTO.getNewName()), HttpStatus.OK);
//    }


    /**
     * Метод, обрабатывающий DELETE запрос на удаление категории услуг
     * @param name название категории
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteCategoryOfServices(@PathVariable("name") String name){
        try{
            //name = URLDecoder.decode(name, "UTF-8");
            Optional<CategoryOfServices> categoryOfServicesOptional = categoryOfServicesService.getCategoryByName(name);
            if(categoryOfServicesOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Категория %s отсутствует в базе данных", name),HttpStatus.BAD_REQUEST);
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

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


/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о категориях расходных материалов
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoriesOfSupplies")
public class CategoriesOfSuppliesController {

    /**
     * Сервис для работы с категориями расходных материалов
     */
    private final CategoriesOfSuppliesService categoriesOfSuppliesService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param categoriesOfSuppliesService сервис для работы с категориями расходных материалов
     * @param validateInputService сервис для валидации входных данных
     */
    public CategoriesOfSuppliesController(CategoriesOfSuppliesService categoriesOfSuppliesService, ValidateInputService validateInputService) {
        this.categoriesOfSuppliesService = categoriesOfSuppliesService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий расходных материалов
     * @param csupName название категории
     * @return ResponseEntity со списком категорий и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> get(@RequestParam(value = "csupName", required = false) String csupName){
        try{
            List<CategoryOfSupplies> categories;
            if(csupName == null) {
                categories = categoriesOfSuppliesService.getAll();
            }else{
                categories = categoriesOfSuppliesService.search(csupName);
            }
            return new ResponseEntity<>(categories, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление категории расходных материалов
     * @param categoryOfSupplies категория расходных материалов
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленной категорией и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
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

    /**
     * Метод, обрабатывающий DELETE запрос на удаление категории расходных материалов
     * @param cSupName название категории
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{cSupName}")
    public ResponseEntity<String> deleteService(@PathVariable("cSupName") String cSupName){
        try{

            Optional<CategoryOfSupplies> categoryOfSuppliesOptional = categoriesOfSuppliesService.getByCSupName(cSupName);
            if(categoryOfSuppliesOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Категория %s отсутствует в базе данных", cSupName),HttpStatus.BAD_REQUEST);
            }

            CategoryOfSupplies categoryOfSupplies = categoryOfSuppliesOptional.get();

            if(!categoryOfSupplies.getSupplies().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как она указана в расходном материале", categoryOfSupplies.getCSupName()), HttpStatus.BAD_REQUEST);
            }else{
                categoriesOfSuppliesService.delete(categoryOfSupplies);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

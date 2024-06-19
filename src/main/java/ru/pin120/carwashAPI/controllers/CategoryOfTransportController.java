package ru.pin120.carwashAPI.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.services.CategoryOfTransportService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о категориях транспорта
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categoriesOfTransport")
public class CategoryOfTransportController {

    /**
     * Сервис для работы с категориями транспорта
     */
    private final CategoryOfTransportService categoryOfTransportService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;

    /**
     * Конструктор для внедрения зависимостей
     * @param categoryOfTransportService сервис для работы с категориями транспорта
     * @param validateInputService сервис для валидации входных данных
     */
    public CategoryOfTransportController(CategoryOfTransportService categoryOfTransportService, ValidateInputService validateInputService) {
        this.categoryOfTransportService = categoryOfTransportService;
        this.validateInputService = validateInputService;
    }


    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий транспорта
     * @return ResponseEntity со списком категорий транспорта и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> get(){
        List<CategoryOfTransport> categoriesOfCars;
        try{
            categoriesOfCars = categoryOfTransportService.getAll();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesOfCars, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение доступных категорий для транспорта
     * @param mark марка транспорта
     * @param model модель транспорта
     * @param trId id транспорта
     * @return @return ResponseEntity со списком категорий транспорта и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/availableCategories")
    public ResponseEntity<?> getAvailableCategories(@RequestParam(value = "mark") String mark, @RequestParam(value = "model")String model, @RequestParam(value = "trId", required = false) Long trId){
        List<CategoryOfTransport> categoryOfTransports = new ArrayList<>();
        try{
            if(trId == null) {
                categoryOfTransports = categoryOfTransportService.getAvailableCategoriesByMarkAndModel(mark, model);
            }else{
                categoryOfTransports = categoryOfTransportService.getAvailableCategoriesByMarkAndModel(mark, model,trId);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfTransports, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий транспорта по названию
     * @param catTrName название
     * @return ResponseEntity со списком категорий транспорта и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/{catTrName}")
    public ResponseEntity<?> getByCatTrName(@PathVariable("catTrName") String catTrName){
        List<CategoryOfTransport> categoriesOfCars = null;
        try{
            //catTrName = URLDecoder.decode(catTrName, "UTF-8");
            categoriesOfCars = categoryOfTransportService.getByTrNameIgnoreCase(catTrName);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoriesOfCars, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка категорий транспорта, для которых не установлена стоимость и время выполнения конкретной услуги
     * @param servName название услуги
     * @return ResponseEntity со списком категорий транспорта и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/emptyCategoryTransport/{servName}")
    public ResponseEntity<?> getCategoryTransportWithoutPriceAndTime(@PathVariable(name = "servName") String servName){
        List<CategoryOfTransport> categoryOfTransports = null;
        try{
            //servName = URLDecoder.decode(servName, "UTF-8");
            categoryOfTransports = categoryOfTransportService.getCategoriesOfTransportWithoutPriceAndTime(servName);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfTransports, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление категории транспорта
     * @param categoryOfTransport категория транспорта
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленной категорией и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid CategoryOfTransport categoryOfTransport, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(categoryOfTransportService.existsByCatTrName(categoryOfTransport.getCatTrName())){
                return new ResponseEntity<>(String.format("Категория %s уже существует (без учёта регистра)", categoryOfTransport.getCatTrName()), HttpStatus.CONFLICT);
            }
            categoryOfTransportService.save(categoryOfTransport);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(categoryOfTransport, HttpStatus.OK);
    }


    /**
     * Метод, обрабатывающий DELETE запрос на удаление категории транспорта
     * @param id id категории
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id")  Long id){
        try{
            Optional<CategoryOfTransport> categoryOfCars = categoryOfTransportService.getById(id);
            if(categoryOfCars.isEmpty()){
                return new ResponseEntity<>(String.format("Категория с id = %d отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            CategoryOfTransport existedCategoryOfTransport = categoryOfCars.get();
            if(!existedCategoryOfTransport.getTransports().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как к ней привязан транспорт", existedCategoryOfTransport.getCatTrName()), HttpStatus.BAD_REQUEST);
            }

            categoryOfTransportService.delete(existedCategoryOfTransport.getCatTrName());

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о категории транспорта
     * @param id id категории
     * @param categoryOfTransport категория транспорта с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с измененной категорией и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
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

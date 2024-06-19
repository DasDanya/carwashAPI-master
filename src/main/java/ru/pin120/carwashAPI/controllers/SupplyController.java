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

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о расходных материалах
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/supplies")
public class SupplyController {

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;

    /**
     * Сервис для работы с расходными материалами
     */
    private final SupplyService supplyService;


    /**
     * Конструктор для внедрения зависимостей
     * @param validateInputService сервис для валидации входных данных
     * @param supplyService сервис для работы с расходными материалами
     */
    public SupplyController(ValidateInputService validateInputService, SupplyService supplyService) {
        this.validateInputService = validateInputService;
        this.supplyService = supplyService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка расходных материалов с учётом пагинации
     * @param pageIndex индекс страницы
     * @param supName название расходного материала
     * @param supCategory категория расходного материала
     * @param operator оператор сравнения стоимости
     * @param supCount стоимость расходного материала
     * @return ResponseEntity со списком расходных материалов и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
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

    /**
     * Метод, обрабатывающий GET запрос на получение фотографии расходного материала
     * @param photoName название фотографии
     * @return ResponseEntity с фотографией расходного материала в виде массива байт и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getPhoto/{photoName}")
    public ResponseEntity<?> getPhoto(@PathVariable("photoName")String photoName){
        try{
            return new ResponseEntity<>(supplyService.getPhoto(photoName), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление расходного материала
     * @param supply расходный материал
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @param photo фотография расходного материала
     * @return ResponseEntity с добавленным расходным материалом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestPart @Valid Supply supply, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(supplyService.exists(supply)){
                return new ResponseEntity<>(String.format("В базе данных уже существует расходный материал с названием %s, с категорией %s и объёмом/количеством единицы средства = %d",
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

    /**
     * Метод, обрабатывающий DELETE запрос на удаление расходного материала
     * @param id расходного материала
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Supply> supplyOptional = supplyService.getById(id);
            if(supplyOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Расходный материал с id = %d не существует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Supply existedSupply = supplyOptional.get();
            if(!existedSupply.getSuppliesInBoxes().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить расходный материал %s %s, так как он указан в боксе %d", existedSupply.getCategory().getCSupName(), existedSupply.getSupName(), existedSupply.getSuppliesInBoxes().get(0).getBox().getBoxId()), HttpStatus.BAD_REQUEST);
            }else{
                supplyService.delete(existedSupply);
            }
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о расходном материале
     * @param id id расходного материала
     * @param supply расходный материал с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @param photo фотография расходного материала
     * @return ResponseEntity с измененными данными о расходном материале и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestPart @Valid Supply supply, BindingResult bindingResult, @RequestPart(required = false) MultipartFile photo){
        try{
            Optional<Supply> supplyOptional = supplyService.getById(id);
            if(supplyOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Расходный материал с id = %d не существует в базе данных", id), HttpStatus.BAD_REQUEST);
            }
            Supply existedSupply = supplyOptional.get();
            if(existedSupply.getSupId().longValue() != supply.getSupId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id расходного материала", HttpStatus.BAD_REQUEST);
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

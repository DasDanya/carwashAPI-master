package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.PriceList;
import ru.pin120.carwashAPI.services.PriceListService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о позициях прайс-листа
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/priceList")
public class PriceListController {

    /**
     * Сервис для работы с позициями прайс-листа
     */
    private final PriceListService priceListService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param priceListService сервис для работы с позициями прайс-листа
     * @param validateInputService сервис для валидации входных данных
     */
    public PriceListController(PriceListService priceListService, ValidateInputService validateInputService) {
        this.priceListService = priceListService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение позиций прайс-листа определенной услуги
     * @param servName название услуги
     * @return ResponseEntity со списком позиций прайс-листа и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/{servName}")
    public ResponseEntity<?> get(@PathVariable(name = "servName") String servName){
        List<PriceList> priceListList = null;
        try {
            priceListList = priceListService.getByServName(servName);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(priceListList, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка позиций прайс-листа определенной категории транспорта
     * @param catTrId id категории транспорта
     * @return ResponseEntity со списком позиций прайс-листа и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getPriceListOfCategoryTransport")
    public ResponseEntity<?> getPriceListOfCategoryTransport(@RequestParam(value = "catTrId") Long catTrId){
        try{
            List<ServiceWithPriceListDTO> serviceWithPriceListDTOS = priceListService.getTransportPriceList(catTrId);
            return new ResponseEntity<>(serviceWithPriceListDTOS, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение позиций прайс-листа определенной услуги с учётом параметров поиска
     * @param servName название услуги
     * @param catTrName название категории транспорта
     * @param priceOperator оператор сравнения стоимости выполнения услуги
     * @param price стоимость
     * @param timeOperator оператор сравнения времени выполнения услуги
     * @param time время выполнения
     * @return ResponseEntity со списком позиций прайс-листа и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping
    public ResponseEntity<?> search(@RequestParam(value = "servName") String servName, @RequestParam(value = "catTrName", required = false) String catTrName, @RequestParam(value = "priceOperator", required = false) String priceOperator,
                       @RequestParam(value = "price", required = false) Integer price, @RequestParam(value = "timeOperator", required = false) String timeOperator,
                       @RequestParam(value = "time", required = false) Integer time){

        //System.out.println(servName + "\n" + catTrName + "\n" + priceOperator + "\n" + price + "\n" + timeOperator + "\n" + time);
        List<PriceList> priceListPositions = null;
        try {
            priceListPositions = priceListService.search(servName,catTrName,priceOperator,price,timeOperator,time);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(priceListPositions, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление позиции в прайс-лист
     * @param priceListPosition позиция
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленной позицией и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPriceListPosition(@RequestBody @Valid PriceList priceListPosition, BindingResult bindingResult){
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(priceListService.existPriceListPosition(priceListPosition)){
                return new ResponseEntity<>(String.format("В базе данных уже существует позиция прайс-листа для категории транспорта %s и услуги %s", priceListPosition.getCategoryOfTransport().getCatTrName(), priceListPosition.getService().getServName()), HttpStatus.CONFLICT);
            }

            priceListService.save(priceListPosition);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(priceListPosition, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о позиции прайс-листа
     * @param plId id позиции
     * @param priceListPosition позиция с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с измененными данными о позиции и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/edit/priceAndTime/{id}")
    public ResponseEntity<?> editPriceAndTime(@PathVariable("id") Long plId, @RequestBody @Valid PriceList priceListPosition, BindingResult bindingResult){
        try{
            Optional<PriceList> priceListOptional = priceListService.getById(plId);
            if(priceListOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Позиция с id = %d не существует",plId), HttpStatus.BAD_REQUEST);
            }

            PriceList existPriceListPosition = priceListOptional.get();
            if(existPriceListPosition.getPlId().longValue() != priceListPosition.getPlId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id позиции", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            existPriceListPosition.setPlPrice(priceListPosition.getPlPrice());
            existPriceListPosition.setPlTime(priceListPosition.getPlTime());
            priceListService.save(existPriceListPosition);

            return new ResponseEntity<>(existPriceListPosition, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий DELETE запрос на удаление позиции прайс-листа
     * @param plId id позиции прайс-листа
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long plId){
        try{
            Optional<PriceList> priceListOptional = priceListService.getById(plId);
            if(priceListOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Позиция с id = %d отсутствует в базе данных", plId),HttpStatus.BAD_REQUEST);
            }

            priceListService.delete(priceListOptional.get());

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

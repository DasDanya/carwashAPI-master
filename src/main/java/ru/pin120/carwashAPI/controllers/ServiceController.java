package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.dtos.BindWithCategoryDTO;
import ru.pin120.carwashAPI.dtos.ServiceDTO;
import ru.pin120.carwashAPI.dtos.ServiceWithPriceListDTO;
import ru.pin120.carwashAPI.models.CategoryOfServices;
import ru.pin120.carwashAPI.models.ClientsTransport;
import ru.pin120.carwashAPI.models.Service;
import ru.pin120.carwashAPI.services.ServService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными об услугах автомойки
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    /**
     * Сервис для работы с услугами автомойки
     */
    private final ServService servService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param servService сервис для работы с услугами автомойки
     * @param validateInputService сервис для валидации входных данных
     */
    public ServiceController(ServService servService, ValidateInputService validateInputService) {
        this.servService = servService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка услуг определенной категории
     * @param categoryName название категории
     * @return ResponseEntity со списком услуг автомойки и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/{categoryName}")
    public ResponseEntity<?> getByCategoryName(@PathVariable(name = "categoryName") String categoryName){
        List<Service> services;
        try{
            //categoryName = URLDecoder.decode(categoryName, "UTF-8");
            services = servService.getByCategoryName(categoryName);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(services, HttpStatus.OK);
    }


//    public ResponseEntity<List<ServiceDTO>> getAll(){
//        List<ServiceDTO> serviceDTOS = new ArrayList<>();
//        try{
//            serviceDTOS = servService.getAllServices();
//        }catch (Exception e){
//            return new ResponseEntity<>(serviceDTOS, HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>(serviceDTOS, HttpStatus.OK);
//    }

    /**
     * Метод, обрабатывающий GET запрос на получение ServiceDTO по названию услуги
     * @param servName название услуги
     * @return ResponseEntity со SerivceDTO и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/getByServName/{servName}")
    public ResponseEntity<ServiceDTO> getByServName(@PathVariable("servName") String servName){
        ServiceDTO serviceDTO = null;
        try{
            //servName = URLDecoder.decode(servName, "UTF-8");
            serviceDTO = servService.getDTOByServName(servName);
        }catch (Exception e){
            return new ResponseEntity<>(serviceDTO, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(serviceDTO, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий GET запрос на получение услуги по её названию
     * @param servName название услуги
     * @return ResponseEntity со услугой и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/get/{servName}")
    public ResponseEntity<?> getService(@PathVariable("servName") String servName){
        try{
            Optional<Service> serviceOptional = servService.getByServName(servName);
            return serviceOptional.map(service -> new ResponseEntity<>(service, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий POST запрос на добавление услуги
     * @param serviceDTO услуга
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленной услугой и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PostMapping("/create")
    public ResponseEntity<?> createService(@RequestBody @Valid ServiceDTO serviceDTO, BindingResult bindingResult){
        Service service = null;
        try{
            if(bindingResult.hasErrors()){
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(servService.existsService(serviceDTO.getServName())){
                return new ResponseEntity<>(String.format("Услуга %s уже существует (без учёта регистра)", serviceDTO.getServName()), HttpStatus.CONFLICT);
            }
            servService.create(serviceDTO);
            service = servService.getByServName(serviceDTO.getServName()).get();

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(service, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий PUT запрос на изменение списка привязанных категорий расходных материалов к услуге
     * @param servName название услуги
     * @param service услуга с новыми привязанными категориями расходных материалов
     * @return ResponseEntity с услугой и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/necessaryCategoriesOfSupplies/{servName}")
    public ResponseEntity<?> editCategoriesOfSupplies(@PathVariable("servName") String servName,@RequestBody Service service){
        try{
            Optional<Service> serviceOptional = servService.getByServName(servName);
            if(serviceOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Услуга %s отсутствует в базе данных",servName), HttpStatus.BAD_REQUEST);
            }
            Service existedService = serviceOptional.get();
            if(!existedService.getServName().equals(servName)){
                return new ResponseEntity<>("Параметр \"Название услуги\" не совпадает с названием услуги", HttpStatus.BAD_REQUEST);
            }

            existedService.setCategoriesOfSupplies(service.getCategoriesOfSupplies());
            servService.edit(existedService);

            return new ResponseEntity<>(existedService, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий PUT запрос на привязку услуг к указанной категории
     * @param bindWithCategoryDTO DTO для привязки услуг к указанной категории
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity со списком услуг указанной категории и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/bindServicesToCategory")
    public ResponseEntity<?> bindServicesToCategory(@RequestBody @Valid BindWithCategoryDTO bindWithCategoryDTO, BindingResult bindingResult){
        List<Service> services;
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try{
            servService.bindServicesWithCategory(bindWithCategoryDTO.getParameter(), bindWithCategoryDTO.getCatNameToBind());
            services = servService.getByCategoryName(bindWithCategoryDTO.getCatNameToBind());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий PUT запрос на привязку услуги к указанной категории
     * @param bindWithCategoryDTO DTO для привязки услуги к указанной категории
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с услугой и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
    @PutMapping("/bindServiceToCategory")
    public ResponseEntity<?> bindServiceToCategory(@RequestBody @Valid BindWithCategoryDTO bindWithCategoryDTO, BindingResult bindingResult){
        Service service;
        if(bindingResult.hasErrors()){
            return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
        }
        try{
            servService.bindServiceWithCategory(bindWithCategoryDTO.getParameter(), bindWithCategoryDTO.getCatNameToBind());
            service = servService.getByServName(bindWithCategoryDTO.getParameter()).get();
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(service, HttpStatus.OK);
    }

    /**
     * Метод, обрабатывающий DELETE запрос на удаление услуги автомойки
     * @param name название услуги
     * @return ResponseEntity с сообщением и статус-кодом
     */
    @DeleteMapping("/delete/{name}")
    public ResponseEntity<String> deleteService(@PathVariable("name") String name){
        try{
            //id = URLDecoder.decode(id, "UTF-8");
            Optional<Service> serviceOptional = servService.getByServName(name);
            if(serviceOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Услуга %s отсутствует в базе данных", name),HttpStatus.BAD_REQUEST);
            }

            Service service = serviceOptional.get();

            if(!service.getBookings().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить услугу %s, так как она указана в заказе", service.getServName()), HttpStatus.BAD_REQUEST);
            }

            servService.delete(service.getServName());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

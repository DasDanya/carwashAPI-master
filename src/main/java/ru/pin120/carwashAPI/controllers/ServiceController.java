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

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/services")
public class ServiceController {

    private final ServService servService;

    private final ValidateInputService validateInputService;

    @Autowired
    private MessageSource messageSource;

    public ServiceController(ServService servService, ValidateInputService validateInputService) {
        this.servService = servService;
        this.validateInputService = validateInputService;
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<List<Service>> getByCategoryName(@PathVariable(name = "categoryName") String categoryName){
        List<Service> services = new ArrayList<>();
        try{
            //categoryName = URLDecoder.decode(categoryName, "UTF-8");
            services = servService.getByCategoryName(categoryName);
        }catch (Exception e){
            return new ResponseEntity<>(services, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(services, HttpStatus.OK);
    }
    public ResponseEntity<List<ServiceDTO>> getAll(){
        List<ServiceDTO> serviceDTOS = new ArrayList<>();
        try{
            serviceDTOS = servService.getAllServices();
        }catch (Exception e){
            return new ResponseEntity<>(serviceDTOS, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(serviceDTOS, HttpStatus.OK);
    }

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

    @GetMapping("/get/{servName}")
    public ResponseEntity<Service> getService(@PathVariable("servName") String servName){
        try{
            Optional<Service> serviceOptional = servService.getByServName(servName);
            return serviceOptional.map(service -> new ResponseEntity<>(service, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

    @PutMapping("/bindServicesToCategory")
    public ResponseEntity<?> bindServicesToCategory(@RequestBody @Valid BindWithCategoryDTO bindWithCategoryDTO, BindingResult bindingResult){
        List<Service> services = null;
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

    @PutMapping("/bindServiceToCategory")
    public ResponseEntity<?> bindServiceToCategory(@RequestBody @Valid BindWithCategoryDTO bindWithCategoryDTO, BindingResult bindingResult){
        Service service = null;
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



//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteService(@RequestBody @Valid ServiceDTO serviceDTO, BindingResult bindingResult){
//        if (bindingResult.hasErrors()) {
//            return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
//        }
//        try{
//            Optional<Service> serviceOptional = servService.getByServName(serviceDTO.getServName());
//            if(serviceOptional.isEmpty()){
//                return new ResponseEntity<>(String.format("Услуга %s отсутствует в базе данных", serviceDTO.getServName()),HttpStatus.BAD_REQUEST);
//            }
//
//            Service service = serviceOptional.get();
//
//            // написать условие для удаления
////            if(!category.getServices().isEmpty()){
////                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как у неё есть услуги", category.getCatName()), HttpStatus.BAD_REQUEST);
////            }else{
////                categoryOfServicesService.delete(category.getCatName());
////            }
//            servService.delete(service.getServName());
//        }catch (Exception e){
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//        return new ResponseEntity<>(String.format("Услуга %s успешно удалена!", serviceDTO.getServName()), HttpStatus.NO_CONTENT);
//    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteService(@PathVariable("id") String id){
        try{
            //id = URLDecoder.decode(id, "UTF-8");
            Optional<Service> serviceOptional = servService.getByServName(id);
            if(serviceOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Услуга %s отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Service service = serviceOptional.get();

            // написать условие для удаления
//            if(!category.getServices().isEmpty()){
//                return new ResponseEntity<>(String.format("Нельзя удалить категорию %s, так как у неё есть услуги", category.getCatName()), HttpStatus.BAD_REQUEST);
//            }else{
//                categoryOfServicesService.delete(category.getCatName());
//            }
            servService.delete(service.getServName());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

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
                return new ResponseEntity<>(String.format("Услуга %s уже существует", serviceDTO.getServName()), HttpStatus.CONFLICT);
            }
            servService.create(serviceDTO);
            service = servService.getByServName(serviceDTO.getServName()).get();

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(service, HttpStatus.OK);
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

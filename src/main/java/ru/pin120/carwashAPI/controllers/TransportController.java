package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.models.Transport;
import ru.pin120.carwashAPI.services.TransportService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transport")
public class TransportController {

    private final TransportService transportService;
    private final ValidateInputService validateInputService;

    public TransportController(TransportService transportService, ValidateInputService validateInputService) {
        this.transportService = transportService;
        this.validateInputService = validateInputService;
    }

    @GetMapping
    public ResponseEntity<List<Transport>> getByPage(@RequestParam(value = "pageIndex") Integer pageIndex, @RequestParam(value = "category", required = false) String category,
                                                     @RequestParam(value = "mark", required = false) String mark, @RequestParam(value = "model",required = false) String model){
        List<Transport> transports = new ArrayList<>();
        try{
            if(category == null && mark == null && model == null) {
                transports = transportService.getByPage(pageIndex);
            }else{
                transports = transportService.search(pageIndex, category, mark, model);
            }
        }catch (Exception e){
            return new ResponseEntity<>(transports, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(transports, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Transport transport, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(transportService.existsTransport(transport)){
                return new ResponseEntity<>(String.format("В базе данных уже существует транспорт с маркой %s моделью %s и идентификатором категории %d",transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId()),HttpStatus.CONFLICT);
            }

            transportService.save(transport);

            return new ResponseEntity<>(transport, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid Transport transport, BindingResult bindingResult){
        try{
            Optional<Transport> optionalTransport = transportService.getById(id);
            if(optionalTransport.isEmpty()){
                return new ResponseEntity<>(String.format("Транспорт с id = %d не существует",id), HttpStatus.BAD_REQUEST);
            }
            Transport existsTransport = optionalTransport.get();
            if(existsTransport.getTrId().longValue() != transport.getTrId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id транспорта", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(transportService.existsOtherTransport(transport)){
                return new ResponseEntity<>(String.format("В базе данных уже существует транспорт с маркой %s моделью %s и идентификатором категории %d",transport.getTrMark(), transport.getTrModel(), transport.getCategoryOfTransport().getCatTrId()),HttpStatus.CONFLICT);
            }

            existsTransport.setTrMark(transport.getTrMark());
            existsTransport.setTrModel(transport.getTrModel());
            existsTransport.setCategoryOfTransport(transport.getCategoryOfTransport());

            transportService.save(existsTransport);

            return new ResponseEntity<>(existsTransport, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Transport> transportOptional = transportService.getById(id);
            if(transportOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Транспорт с id = %d отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Transport existedTransport = transportOptional.get();
            if(!existedTransport.getClientsTransport().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить транспорт %s %s, так как с ним связан личный транспорт клиента", existedTransport.getTrMark(), existedTransport.getTrModel()), HttpStatus.BAD_REQUEST);
            }

            transportService.deleteById(existedTransport.getTrId());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

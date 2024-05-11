package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.Client;
import ru.pin120.carwashAPI.models.Transport;
import ru.pin120.carwashAPI.services.ClientService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    private final ValidateInputService validateInputService;

    public ClientController(ClientService clientService, ValidateInputService validateInputService) {
        this.clientService = clientService;
        this.validateInputService = validateInputService;
    }

    @GetMapping
    public ResponseEntity<List<Client>> getByPage(@RequestParam(value = "pageIndex") Integer pageIndex, @RequestParam(value = "surname",required = false) String surname, @RequestParam(value = "name", required = false) String name,
                                                  @RequestParam(value = "phone",required = false) String phone, @RequestParam(value = "discount", required = false) Integer discount, @RequestParam(value = "filterDiscountOperator",required = false) String filterDiscountOperator){
        List<Client> clients = null;
        try{
            if(surname == null && name == null && phone == null && filterDiscountOperator == null && discount == null) {
                clients = clientService.getByPage(pageIndex);
            }else{
                clients = clientService.search(pageIndex,surname,name,phone,discount, filterDiscountOperator);
            }
        }catch (Exception e){
            return new ResponseEntity<>(clients, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid Client client, BindingResult bindingResult){
        try {
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            clientService.save(client);
            return new ResponseEntity<>(client, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid Client client, BindingResult bindingResult){
        try{
            Optional<Client> clientOptional = clientService.getById(id);
            if(clientOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Клиент с id = %d не существует",id), HttpStatus.BAD_REQUEST);
            }
            Client existsClient = clientOptional.get();
            if(existsClient.getClId().longValue() != client.getClId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id клиента", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            existsClient.setClSurname(client.getClSurname());
            existsClient.setClName(client.getClName());
            existsClient.setClPhone(client.getClPhone());
            existsClient.setClDiscount(client.getClDiscount());

            clientService.save(existsClient);

            return new ResponseEntity<>(existsClient, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<Client> clientOptional = clientService.getById(id);
            if(clientOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Клиент с id = %d отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            Client existedClient = clientOptional.get();
            if(!existedClient.getTransports().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить клиента %s %s, так как к нему привязан личный транспорт", existedClient.getClSurname(), existedClient.getClName()), HttpStatus.BAD_REQUEST);
            }

            clientService.deleteById(existedClient.getClId());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

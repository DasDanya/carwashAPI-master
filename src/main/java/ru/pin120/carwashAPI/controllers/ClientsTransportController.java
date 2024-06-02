package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.ClientsTransport;
import ru.pin120.carwashAPI.models.Transport;
import ru.pin120.carwashAPI.services.ClientsTransportService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clientsTransport")
public class ClientsTransportController {

    private final ClientsTransportService clientsTransportService;

    private final ValidateInputService validateInputService;

    public ClientsTransportController(ClientsTransportService clientsTransportService, ValidateInputService validateInputService) {
        this.clientsTransportService = clientsTransportService;
        this.validateInputService = validateInputService;
    }

    @GetMapping("/byClient")
    public ResponseEntity<List<ClientsTransport>> getByClientId(@RequestParam(value = "clId") Long clientId, @RequestParam(value = "mark", required = false) String mark, @RequestParam(value = "model", required = false) String model,
                                                                @RequestParam(value = "category", required = false) String category, @RequestParam(value = "stateNumber", required = false) String stateNumber){
        try{
            List<ClientsTransport> clientsTransports;
            if (mark == null && model == null && category == null && stateNumber == null) {
                clientsTransports = clientsTransportService.getByClientId(clientId);
            } else {
                clientsTransports = clientsTransportService.search(clientId, mark, model, category, stateNumber);
            }

            return new ResponseEntity<>(clientsTransports, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/byStateNumber")
    public ResponseEntity<List<ClientsTransport>> getByStateNumber(@RequestParam(value = "stateNumber") String stateNumber){
        try{
            List<ClientsTransport> clientsTransports;
            clientsTransports = clientsTransportService.getByStateNumber(stateNumber);
            return new ResponseEntity<>(clientsTransports, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid ClientsTransport clientsTransport, BindingResult bindingResult){
        try{
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }
            if(clientsTransportService.existsClientTransport(clientsTransport)){
                return new ResponseEntity<>(String.format("У клиента %s %s уже существует транспорт %s %s с категорией %s с гос.номером %s",clientsTransport.getClient().getClSurname(), clientsTransport.getClient().getClName(),
                clientsTransport.getTransport().getTrMark(), clientsTransport.getTransport().getTrModel(), clientsTransport.getTransport().getCategoryOfTransport().getCatTrName(), clientsTransport.getClTrStateNumber()),HttpStatus.CONFLICT);
            }

            clientsTransportService.save(clientsTransport);

            return new ResponseEntity<>(clientsTransport, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id, @RequestBody @Valid ClientsTransport clientsTransport, BindingResult bindingResult){
        try{
            Optional<ClientsTransport> clientsTransportOptional = clientsTransportService.findById(id);
            if(clientsTransportOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Личный транспорт с id = %d не существует",id), HttpStatus.BAD_REQUEST);
            }
            ClientsTransport existedClientTransport = clientsTransportOptional.get();
            if(existedClientTransport.getClTrId().longValue() != clientsTransport.getClTrId().longValue()){
                return new ResponseEntity<>("Параметр id не совпадает с id личного транспорта", HttpStatus.BAD_REQUEST);
            }
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(validateInputService.getErrors(bindingResult), HttpStatus.BAD_REQUEST);
            }

            if(clientsTransportService.existsOtherClientTransport(clientsTransport)){
                return new ResponseEntity<>(String.format("У клиента %s %s уже существует транспорт %s %s с категорией %s с гос.номером %s",clientsTransport.getClient().getClSurname(), clientsTransport.getClient().getClName(),
                        clientsTransport.getTransport().getTrMark(), clientsTransport.getTransport().getTrModel(), clientsTransport.getTransport().getCategoryOfTransport().getCatTrName(), clientsTransport.getClTrStateNumber()),HttpStatus.CONFLICT);
            }

            existedClientTransport.setClient(clientsTransport.getClient());
            existedClientTransport.setTransport(clientsTransport.getTransport());
            existedClientTransport.setClTrStateNumber(clientsTransport.getClTrStateNumber());

            clientsTransportService.save(existedClientTransport);

            return new ResponseEntity<>(existedClientTransport, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long id){
        try{
            Optional<ClientsTransport> clientsTransportOptional = clientsTransportService.findById(id);
            if(clientsTransportOptional.isEmpty()){
                return new ResponseEntity<>(String.format("Личный транспорт с id = %d отсутствует в базе данных", id),HttpStatus.BAD_REQUEST);
            }

            ClientsTransport existedTransport = clientsTransportOptional.get();
            if(!existedTransport.getBookings().isEmpty()){
                return new ResponseEntity<>(String.format("Нельзя удалить транспорт %s %s %s, так как он указан в заказе", existedTransport.getTransport().getTrMark(), existedTransport.getTransport().getTrModel(), existedTransport.getClTrStateNumber()), HttpStatus.BAD_REQUEST);
            }

            clientsTransportService.deleteById(existedTransport.getClTrId());
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.noContent().build();
    }
}

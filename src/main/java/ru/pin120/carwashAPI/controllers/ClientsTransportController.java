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

/**
 * REST контроллер, обрабатывающий HTTP-запросы для работы с данными о транспорте клиента
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/clientsTransport")
public class ClientsTransportController {

    /**
     * Сервис для работы с транспортом клиента
     */
    private final ClientsTransportService clientsTransportService;

    /**
     * Сервис для валидации входных данных
     */
    private final ValidateInputService validateInputService;


    /**
     * Конструктор для внедрения зависимостей
     * @param clientsTransportService сервис для работы с транспортом клиента
     * @param validateInputService сервис для валидации входных данных
     */
    public ClientsTransportController(ClientsTransportService clientsTransportService, ValidateInputService validateInputService) {
        this.clientsTransportService = clientsTransportService;
        this.validateInputService = validateInputService;
    }

    /**
     * Метод, обрабатывающий GET запос на получение списка транспорта клиента
     * @param clientId id клиента
     * @param mark марка транспорта
     * @param model модель транспорта
     * @param category категория
     * @param stateNumber госномер
     * @return ResponseEntity со списком транспорта клиента и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/byClient")
    public ResponseEntity<?> getByClientId(@RequestParam(value = "clId") Long clientId, @RequestParam(value = "mark", required = false) String mark, @RequestParam(value = "model", required = false) String model,
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
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Метод, обрабатывающий GET запрос на получение списка транспорта клиента по госномеру
     * @param stateNumber госномер
     * @return ResponseEntity со списком транспорта клиента и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом 500
     */
    @GetMapping("/byStateNumber")
    public ResponseEntity<?> getByStateNumber(@RequestParam(value = "stateNumber") String stateNumber){
        try{
            List<ClientsTransport> clientsTransports;
            clientsTransports = clientsTransportService.getByStateNumber(stateNumber);
            return new ResponseEntity<>(clientsTransports, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Метод, обрабатывающий POST запрос на добавление транспорта клиента
     * @param clientsTransport транспорт клиента
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с добавленным транспортом и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
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

    /**
     * Метод, обрабатывающий PUT запрос на изменение данных о личном транспорте клиента
     * @param id id транспорта клиента
     * @param clientsTransport транспорт клиента с новыми данными
     * @param bindingResult экземпляр интерфейса для обработки результатов валидации данных
     * @return ResponseEntity с измененными данными о личном транспорте клиента и статус-кодом 200, если все прошло успешно, иначе ResponseEntity с сообщением об ошибке и статус-кодом
     */
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

    /**
     * Метод, обрабатывающий DELETE запрос на удаление личного транспорта клиента
     * @param id id личного транспорта клиента
     * @return ResponseEntity с сообщением и статус-кодом
     */
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

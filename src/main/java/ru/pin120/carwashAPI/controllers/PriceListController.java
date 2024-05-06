package ru.pin120.carwashAPI.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.pin120.carwashAPI.models.PriceList;
import ru.pin120.carwashAPI.services.PriceListService;
import ru.pin120.carwashAPI.services.ValidateInputService;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/priceList")
public class PriceListController {

    private final PriceListService priceListService;
    private final ValidateInputService validateInputService;

    public PriceListController(PriceListService priceListService, ValidateInputService validateInputService) {
        this.priceListService = priceListService;
        this.validateInputService = validateInputService;
    }

    @GetMapping("/{servName}")
    public ResponseEntity<List<PriceList>> get(@PathVariable(name = "servName") String servName){
        List<PriceList> priceListList = null;
        try {
            priceListList = priceListService.getByServName(servName);

        }catch (Exception e){
            return new ResponseEntity<>(priceListList, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(priceListList, HttpStatus.OK);
    }

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

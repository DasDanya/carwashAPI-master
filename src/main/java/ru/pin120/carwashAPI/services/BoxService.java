package ru.pin120.carwashAPI.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.repositories.BoxRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BoxService {

    private final BoxRepository boxRepository;

    public BoxService(BoxRepository boxRepository) {
        this.boxRepository = boxRepository;
    }

    public List<Box> getAll(){
        return boxRepository.findAllByOrderByBoxId();
    }

    public void save(Box box){
        boxRepository.save(box);
    }

    public Optional<Box> getById(Long boxId){
        return boxRepository.findById(boxId);
    }

    public void delete(Long boxId) {
        boxRepository.deleteById(boxId);
    }
}

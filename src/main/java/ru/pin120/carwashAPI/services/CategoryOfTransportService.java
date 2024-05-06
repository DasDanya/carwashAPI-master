package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.CategoryOfTransport;
import ru.pin120.carwashAPI.repositories.CategoryOfTransportRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CategoryOfTransportService {

    private final CategoryOfTransportRepository categoryOfTransportRepository;

    public CategoryOfTransportService(CategoryOfTransportRepository categoryOfTransportRepository) {
        this.categoryOfTransportRepository = categoryOfTransportRepository;
    }

    public List<CategoryOfTransport> getAll(){
        Sort sort = Sort.by(Sort.Direction.ASC, "catTrName");

        return (List<CategoryOfTransport>) categoryOfTransportRepository.findAll(sort);
    }

    public List<CategoryOfTransport> getCategoriesOfTransportWithoutPriceAndTime(String servName){
        return categoryOfTransportRepository.findCategoriesOfTransportWithoutPriceAndTimeByServName(servName);
    }

    public boolean existsByCatTrName(String catTrName){
        return categoryOfTransportRepository.existsByCatTrNameIgnoreCase(catTrName);
    }

    public Optional<CategoryOfTransport> getById(Long catTrId){
        return categoryOfTransportRepository.findByCatTrId(catTrId);
    }

    public void save(CategoryOfTransport categoryOfTransport) {
        categoryOfTransportRepository.save(categoryOfTransport);
    }

    @Transactional
    public void delete(String catTrName) {
        categoryOfTransportRepository.deleteByCatTrName(catTrName);
    }

    public List<CategoryOfTransport> getByTrNameIgnoreCase(String trName) {
        return categoryOfTransportRepository.findByCatTrNameContainsIgnoreCaseOrderByCatTrNameAsc(trName);
    }

    public boolean existsCategoryOfTransport(String catTrName, Long catTrId){
        Optional<CategoryOfTransport> categoryOfTransportOptional = categoryOfTransportRepository.findByCatTrNameIgnoreCase(catTrName);
        if(categoryOfTransportOptional.isEmpty()){
            return false;
        }else{
            if(Objects.equals(categoryOfTransportOptional.get().getCatTrId(), catTrId)){
                return false;
            }else{
                return true;
            }
        }
    }
}

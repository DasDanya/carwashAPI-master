package ru.pin120.carwashAPI.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pin120.carwashAPI.models.CategoryOfSupplies;
import ru.pin120.carwashAPI.repositories.CategoriesOfSuppliesRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriesOfSuppliesService {

    private final CategoriesOfSuppliesRepository categoriesOfSuppliesRepository;

    public CategoriesOfSuppliesService(CategoriesOfSuppliesRepository categoriesOfSuppliesRepository) {
        this.categoriesOfSuppliesRepository = categoriesOfSuppliesRepository;
    }

    public List<CategoryOfSupplies> getAll(){
        return categoriesOfSuppliesRepository.findAllByOrderByCSupNameAsc();
    }

    public void save(CategoryOfSupplies categoryOfSupplies) {
        categoriesOfSuppliesRepository.save(categoryOfSupplies);
    }

    public boolean exists(CategoryOfSupplies categoryOfSupplies) {
        return categoriesOfSuppliesRepository.existsBycSupNameIgnoreCase(categoryOfSupplies.getCSupName());
    }

    public Optional<CategoryOfSupplies> getByCSupName(String cSupName) {
        return categoriesOfSuppliesRepository.findById(cSupName);
    }

    @Transactional
    public void delete(CategoryOfSupplies categoryOfSupplies) {
        categoriesOfSuppliesRepository.deleteById(categoryOfSupplies.getCSupName());
    }

    public List<CategoryOfSupplies> search(String csupName) {
        return categoriesOfSuppliesRepository.findByCSupNameContainsIgnoreCase(csupName);
    }
}

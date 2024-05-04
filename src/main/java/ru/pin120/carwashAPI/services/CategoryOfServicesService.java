package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.CategoriesWithServicesDTO;
import ru.pin120.carwashAPI.dtos.EditCategoryOrServiceDTO;
import ru.pin120.carwashAPI.models.CategoryOfServices;
import ru.pin120.carwashAPI.repositories.CategoryOfServicesRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryOfServicesService {

    private final CategoryOfServicesRepository categoryOfServicesRepository;

    public CategoryOfServicesService(CategoryOfServicesRepository categoryOfServicesRepository) {
        this.categoryOfServicesRepository = categoryOfServicesRepository;
    }


    public List<CategoriesWithServicesDTO> getCategoriesWithServices(){
        List<CategoriesWithServicesDTO> categoriesWithServices = new ArrayList<>();

        List<String[]> results = categoryOfServicesRepository.findCategoriesWithServices();
        for(String[] result: results){
            String category = result[0];
            String service = result[1];

            Optional<CategoriesWithServicesDTO> optionalCategoriesWithServices = categoriesWithServices.stream()
                    .filter(c -> c.getCategoryName().equals(category))
                    .findFirst();

            if(optionalCategoriesWithServices.isEmpty()){
                CategoriesWithServicesDTO categoriesWithServicesDTO = new CategoriesWithServicesDTO();
                categoriesWithServicesDTO.setCategoryName(category);
                List<String> services = new ArrayList<>();
                services.add(service);
                categoriesWithServicesDTO.setServicesOfCategory(services);

                categoriesWithServices.add(categoriesWithServicesDTO);
            }else{
                optionalCategoriesWithServices.get().getServicesOfCategory().add(service);
            }
        }

        return categoriesWithServices;
    }

    public void create(CategoryOfServices categoryOfServices){
        categoryOfServicesRepository.save(categoryOfServices);
    }

    public boolean existsCategoryOfServices(String catName){
        return categoryOfServicesRepository.existsByCatNameIgnoreCase(catName);
    }

    public void edit(EditCategoryOrServiceDTO editCategoryOrServiceDTO) throws Exception{
        Optional<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findByCatName(editCategoryOrServiceDTO.getPastName());
        if(categoryOfServices.isEmpty()){
            throw new EntityNotFoundException(String.format("Категория %s не существует в базе данных",editCategoryOrServiceDTO.getPastName()));
        }else{
            CategoryOfServices existsCategoryOfServices = categoryOfServices.get();
            existsCategoryOfServices.setCatName(editCategoryOrServiceDTO.getNewName());
            categoryOfServicesRepository.save(existsCategoryOfServices);
        }
    }

    public Iterable<CategoryOfServices> getAll(){
        Sort sort = Sort.by(Sort.Direction.ASC, "catName");
        Iterable<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findAll(sort);
        for(CategoryOfServices category: categoryOfServices){
            category.getServices().sort(Comparator.comparing(ru.pin120.carwashAPI.models.Service::getServName));
        }
        return categoryOfServices;
    }

    public List<String> getCatNames(){
        return categoryOfServicesRepository.findAllCatNamesSortedAsc();
    }

    public List<String> getCatNamesByParameter(String parameter){
        return categoryOfServicesRepository.findCatNamesByParameterAsc(parameter);
    }

    public Optional<CategoryOfServices> getCategoryByName(String catName){
        return categoryOfServicesRepository.findByCatName(catName);
    }

    @Transactional
    public void delete(String catName) {
        categoryOfServicesRepository.deleteByCatName(catName);
    }
}

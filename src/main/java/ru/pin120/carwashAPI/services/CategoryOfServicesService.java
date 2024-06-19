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

/**
 * Сервис категорий услуг
 */
@Service
public class CategoryOfServicesService {

    /**
     * Репозиторий категорий услуг
     */
    private final CategoryOfServicesRepository categoryOfServicesRepository;

    /**
     * Внедрение зависимости
     * @param categoryOfServicesRepository репозиторий категорий услуг
     */
    public CategoryOfServicesService(CategoryOfServicesRepository categoryOfServicesRepository) {
        this.categoryOfServicesRepository = categoryOfServicesRepository;
    }

    /**
     * Получение списка категорий услуг вместе с услугами
     * @return Список категорий вместе с услугами
     */
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

    /**
     * Сохранение категории услуги
     * @param categoryOfServices категория услуг
     */
    public void create(CategoryOfServices categoryOfServices){
        categoryOfServicesRepository.save(categoryOfServices);
    }

    /**
     * Проверяет, существует ли категория услуг с заданным названием
     * @param catName название
     * @return true, если существует, иначе false
     */
    public boolean existsCategoryOfServices(String catName){
        return categoryOfServicesRepository.existsByCatNameIgnoreCase(catName);
    }

//    public void edit(EditCategoryOrServiceDTO editCategoryOrServiceDTO) throws Exception{
//        Optional<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findByCatName(editCategoryOrServiceDTO.getPastName());
//        if(categoryOfServices.isEmpty()){
//            throw new EntityNotFoundException(String.format("Категория %s не существует в базе данных",editCategoryOrServiceDTO.getPastName()));
//        }else{
//            CategoryOfServices existsCategoryOfServices = categoryOfServices.get();
//            existsCategoryOfServices.setCatName(editCategoryOrServiceDTO.getNewName());
//            categoryOfServicesRepository.save(existsCategoryOfServices);
//        }
//    }

    /**
     * Получение списка всех категорий услуг
     * @return Категории услуг
     */
    public Iterable<CategoryOfServices> getAll(){
        Sort sort = Sort.by(Sort.Direction.ASC, "catName");
        Iterable<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findAll(sort);
        for(CategoryOfServices category: categoryOfServices){
            category.getServices().sort(Comparator.comparing(ru.pin120.carwashAPI.models.Service::getServName));
        }
        return categoryOfServices;
    }

    /**
     * Получение списка названий категорий услуг
     * @return Список названий категорий услуг
     */
    public List<String> getCatNames(){
        return categoryOfServicesRepository.findAllCatNamesSortedAsc();
    }

    /**
     * Получение списка названий категорий услуг по указанному параметру
     * @param parameter параметр
     * @return Список названий категорий услуг
     */
    public List<String> getCatNamesByParameter(String parameter){
        return categoryOfServicesRepository.findCatNamesByParameterAsc(parameter);
    }

    /**
     * Получение категории с заданным названием
     * @param catName название категории
     * @return Объект Optional с категорией, если она существует
     */
    public Optional<CategoryOfServices> getCategoryByName(String catName){
        return categoryOfServicesRepository.findByCatName(catName);
    }

    /**
     * Удаление категории услуг по названию
     * @param catName название
     */
    @Transactional
    public void delete(String catName) {
        categoryOfServicesRepository.deleteByCatName(catName);
    }
}

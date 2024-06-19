package ru.pin120.carwashAPI.services;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.dtos.ServiceDTO;
import ru.pin120.carwashAPI.models.CategoryOfServices;
import ru.pin120.carwashAPI.repositories.CategoryOfServicesRepository;
import ru.pin120.carwashAPI.repositories.ServiceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Сервис услуг
 */
@Service
public class ServService {

    /**
     * Репозиторий услуг
     */
    private final ServiceRepository serviceRepository;

    /**
     * Репозиторий категорий услуг
     */
    private final CategoryOfServicesRepository categoryOfServicesRepository;

    /**
     * Внедрение зависимостей
     * @param serviceRepository репозиторий услуг
     * @param categoryOfServicesRepository репозиторий категорий услуг
     */
    public ServService(ServiceRepository serviceRepository, CategoryOfServicesRepository categoryOfServicesRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryOfServicesRepository = categoryOfServicesRepository;
    }

    /**
     * Проверяет существование услуги с указанным названием
     * @param servName название
     * @return true, если существует, иначе false
     */
    public boolean existsService(String servName){
        return serviceRepository.existsByServNameIgnoreCase(servName);
    }

    /**
     * Получение услуги по названию
     * @param servName название
     * @return Объект Optional с услугой, если она существует
     */
    public Optional<ru.pin120.carwashAPI.models.Service> getByServName(String servName){
        return serviceRepository.findByServName(servName);
    }

    /**
     * Получение DTO услуги с указанным названием
     * @param servName название
     * @return DTO усуги
     */
    public ServiceDTO getDTOByServName(String servName){
        ServiceDTO serviceDTO = new ServiceDTO();

        Optional<ru.pin120.carwashAPI.models.Service> optionalService = serviceRepository.findByServName(servName);
        if(optionalService.isPresent()){
            ru.pin120.carwashAPI.models.Service existsService = optionalService.get();
            serviceDTO.setServName(existsService.getServName());
            serviceDTO.setCatName(existsService.getCategory().getCatName());
        }

        return serviceDTO;
    }

    /**
     * Добавление услуги
     * @param serviceDTO DTO услуги
     */
    public void create(ServiceDTO serviceDTO){
        Optional<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findByCatName(serviceDTO.getCatName());
        if(categoryOfServices.isEmpty()){
            throw new EntityNotFoundException("В базе данных не существует категории " + serviceDTO.getCatName());
        }else{
            ru.pin120.carwashAPI.models.Service service = new ru.pin120.carwashAPI.models.Service(serviceDTO.getServName(), categoryOfServices.get());
            serviceRepository.save(service);
        }
    }

    /**
     * Изменение данных об услуге
     * @param service услуга
     */
    public void edit(ru.pin120.carwashAPI.models.Service service){
        serviceRepository.save(service);
    }

    /**
     * Получение списка услуг указанной категории
     * @param categoryName название категории
     * @return Список услуг указанной категории
     */
    public List<ru.pin120.carwashAPI.models.Service> getByCategoryName(String categoryName){
        return serviceRepository.findByCategory_CatNameOrderByServNameAsc(categoryName);
    }

    /**
     * Привязка услуг к новой категории
     * @param pastCategoryName название старой категории
     * @param newCategoryName  название новой категории
     */
    public void bindServicesWithCategory(String pastCategoryName, String newCategoryName){
        //serviceRepository.bindServicesWithNewCategory(pastCategoryName, newCategoryName);
        Optional<CategoryOfServices> pastCategory = categoryOfServicesRepository.findByCatName(pastCategoryName);
        Optional<CategoryOfServices> newCategory = categoryOfServicesRepository.findByCatName(newCategoryName);

        if(pastCategory.isPresent() && newCategory.isPresent()){
            List<ru.pin120.carwashAPI.models.Service> services = serviceRepository.findByCategory(pastCategory.get());
            for(ru.pin120.carwashAPI.models.Service service: services){
                service.setCategory(newCategory.get());
                serviceRepository.save(service);
            }
        }else{
            if(pastCategory.isEmpty()){
                throw new EntityNotFoundException("В базе данных не сущетсвует категории " + pastCategoryName);
            }else{
                throw new EntityNotFoundException("В базе данных не существует категории " + newCategoryName);
            }
        }
    }

    /**
     * Привязка услуги к новой категории
     * @param servName Название услуги
     * @param catName Название категории
     */
    public void bindServiceWithCategory(String servName,String catName) {
        Optional<CategoryOfServices> newCategory = categoryOfServicesRepository.findByCatName(catName);
        Optional<ru.pin120.carwashAPI.models.Service> service = serviceRepository.findByServName(servName);

        if(newCategory.isPresent() && service.isPresent()){
            ru.pin120.carwashAPI.models.Service existsService = service.get();
            existsService.setCategory(newCategory.get());
            serviceRepository.save(existsService);
        }else{
            if(newCategory.isEmpty()){
                throw new EntityNotFoundException("В базе данных не существует категории " + catName);
            }else{
                throw new EntityNotFoundException("В базе данных не существует услуги" + servName);
            }
        }
    }

    /**
     * Удаление услуги по названию
     * @param servName название
     */
    @Transactional
    public void delete(String servName) {
        serviceRepository.deleteByServName(servName);
    }

}

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

@Service
public class ServService {

    private final ServiceRepository serviceRepository;

    private final CategoryOfServicesRepository categoryOfServicesRepository;

    public ServService(ServiceRepository serviceRepository, CategoryOfServicesRepository categoryOfServicesRepository) {
        this.serviceRepository = serviceRepository;
        this.categoryOfServicesRepository = categoryOfServicesRepository;
    }

    public boolean existsService(String servName){
        return serviceRepository.existsByServNameIgnoreCase(servName);
    }

    public Optional<ru.pin120.carwashAPI.models.Service> getByServName(String servName){
        return serviceRepository.findByServName(servName);
    }

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

    public void create(ServiceDTO serviceDTO){
        Optional<CategoryOfServices> categoryOfServices = categoryOfServicesRepository.findByCatName(serviceDTO.getCatName());
        if(categoryOfServices.isEmpty()){
            throw new EntityNotFoundException("В базе данных не существует категории " + serviceDTO.getCatName());
        }else{
            ru.pin120.carwashAPI.models.Service service = new ru.pin120.carwashAPI.models.Service(serviceDTO.getServName(), categoryOfServices.get());
            serviceRepository.save(service);
        }
    }

    public List<ru.pin120.carwashAPI.models.Service> getByCategoryName(String categoryName){
        return serviceRepository.findByCategory_CatNameOrderByServNameAsc(categoryName);
    }

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

    public void bindServiceWithCategory(String servName,String catName) throws Exception {
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

    @Transactional
    public void delete(String servName) {
        serviceRepository.deleteByServName(servName);
    }

    public List<ServiceDTO> getAllServices(){
        List<ru.pin120.carwashAPI.models.Service> allServices = serviceRepository.findAllSortedByCategoryNameAndServiceName();
        List<ServiceDTO> serviceDTOS = new ArrayList<>();
        for(ru.pin120.carwashAPI.models.Service service: allServices){
            ServiceDTO serviceDTO = new ServiceDTO(service.getServName(), service.getCategory().getCatName());
            serviceDTOS.add(serviceDTO);
        }

        return serviceDTOS;
    }
}

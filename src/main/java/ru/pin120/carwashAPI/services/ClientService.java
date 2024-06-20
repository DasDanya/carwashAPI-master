package ru.pin120.carwashAPI.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.pin120.carwashAPI.models.Client;
import ru.pin120.carwashAPI.repositories.ClientRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сервис клиента
 */
@Service
public class ClientService {


    /**
     * Репозиторий клиента
     */
    private final ClientRepository clientRepository;

    @Autowired
    private Environment environment;

    /**
     * Внедрений зависимости
     * @param clientRepository репозиторий клиента
     */
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    /**
     * Получение списка клиентов с учётом пагинации
     * @param pageIndex индекс страницы
     * @return Список клиентов
     */
    public List<Client> getByPage(Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, Integer.parseInt(environment.getProperty("COUNT_ITEMS_IN_PAGE")), Sort.by("clSurname", "clName", "clPhone", "clDiscount"));
        return clientRepository.findAll(pageable).getContent();
    }

    /**
     * Сохранение клиента
     * @param client клиент
     */
    public void save(Client client) {
        clientRepository.save(client);
    }

    /**
     * Получение клиента по id
     * @param id id клиента
     * @return Объект Optional с клиентом, если он существует
     */
    public Optional<Client> getById(Long id) {
        return clientRepository.findByClId(id);
    }

    /**
     * Удаление клиента по id
     * @param clId id клиента
     */
    @Transactional
    public void deleteById(Long clId) {
        clientRepository.deleteByClId(clId);
    }


    /**
     * Поиск клиентов
     * @param pageIndex индекс страницы
     * @param surname фамилия
     * @param name имя
     * @param phone номер телефона
     * @param discount скидка
     * @param filterDiscountOperator оператор сравнения скидки
     * @return Список найденных клиентов
     */
    public List<Client> search(Integer pageIndex, String surname, String name, String phone, Integer discount, String filterDiscountOperator) {
        Pageable pageable = PageRequest.of(pageIndex, Integer.parseInt(environment.getProperty("COUNT_ITEMS_IN_PAGE")), Sort.by("clSurname", "clName", "clPhone", "clDiscount"));
        if(surname != null && name != null && phone != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator){
                case "<":
                    return clientRepository.query1(surname,name,phone,discount,pageable);
                case ">":
                    return clientRepository.query2(surname,name,phone,discount,pageable);
                case "=":
                    return clientRepository.query3(surname,name,phone,discount,pageable);
            }
        }else if(surname != null && name != null && phone != null){
            return clientRepository.query4(surname,name,phone,pageable);
        }else if(surname != null && name != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query5(surname, name, discount, pageable);
                case ">":
                    return clientRepository.query6(surname, name, discount, pageable);
                case "=":
                    return clientRepository.query7(surname, name, discount, pageable);
            }
        }else if(surname != null && phone != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query8(surname,phone, discount, pageable);
                case ">":
                    return clientRepository.query9(surname, phone, discount, pageable);
                case "=":
                    return clientRepository.query10(surname, phone, discount, pageable);
            }
        }else if(name != null && phone != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query11(name,phone, discount, pageable);
                case ">":
                    return clientRepository.query12(name, phone, discount, pageable);
                case "=":
                    return clientRepository.query13(name, phone, discount, pageable);
            }
        }else if(surname != null && name != null){
            return clientRepository.query14(surname,name, pageable);
        }else if(surname != null && phone != null){
            return clientRepository.query15(surname,phone, pageable);
        }else if(surname != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query16(surname, discount, pageable);
                case ">":
                    return clientRepository.query17(surname, discount, pageable);
                case "=":
                    return clientRepository.query18(surname, discount, pageable);
            }
        }else if(name != null && phone != null){
            return clientRepository.query19(name,phone, pageable);
        }else if (name != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query20(name, discount, pageable);
                case ">":
                    return clientRepository.query21(name, discount, pageable);
                case "=":
                    return clientRepository.query22(name, discount, pageable);
            }
        }else if (phone != null && filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query23(phone, discount, pageable);
                case ">":
                    return clientRepository.query24(phone, discount, pageable);
                case "=":
                    return clientRepository.query25(phone, discount, pageable);
            }
        }else if(surname != null){
            return clientRepository.query26(surname, pageable);
        }else if(name != null){
            return clientRepository.query27(name, pageable);
        }else if(phone != null){
            return clientRepository.query28(phone, pageable);
        }else if(filterDiscountOperator != null && discount != null){
            switch (filterDiscountOperator) {
                case "<":
                    return clientRepository.query29(discount, pageable);
                case ">":
                    return clientRepository.query30(discount, pageable);
                case "=":
                    return clientRepository.query31(discount, pageable);
            }
        }

        return null;
    }
}

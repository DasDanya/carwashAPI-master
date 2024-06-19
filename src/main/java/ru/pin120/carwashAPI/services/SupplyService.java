package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.carwashAPI.Exceptions.FileIsNotImageException;
import ru.pin120.carwashAPI.models.Supply;
import ru.pin120.carwashAPI.repositories.SupplyRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис расходных материалов
 */
@Service
public class SupplyService {

    private final int COUNT_ITEMS_IN_PAGE = 12;
    private static final String PATH_TO_PHOTOS= "src/main/resources/static/images/supplies/";
    private static final String NAME_DEFAULT_PHOTO = "noPhoto.jpeg";

    /**
     * Репозиторий расходных материалов
     */
    private final SupplyRepository supplyRepository;
    /**
     * Сервис для работы с изображениями
     */
    private final FilesService filesService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Внедрение зависимостей
     * @param supplyRepository репозиторий расходных материалов
     * @param filesService сервис для работы с изображениями
     */
    public SupplyService(SupplyRepository supplyRepository, FilesService filesService) {
        this.supplyRepository = supplyRepository;
        this.filesService = filesService;
    }

    /**
     * Получение расходных материалов по индексу страницы
     * @param pageIndex индекс страницы
     * @return Список расходных материалов
     */
    public List<Supply> get(Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE, Sort.by( "category.cSupName", "supName", "supCount"));
        return supplyRepository.findAll(pageable).getContent();
    }

    /**
     * Поиск расходных материалов
     * @param pageIndex индекс страницы
     * @param supName название
     * @param supCategory категория
     * @param operator оператор сравнения количества
     * @param supCount количество
     * @return Список найденных расходных материалов
     */
    public List<Supply> search(Integer pageIndex, String supName, String supCategory, String operator, Integer supCount) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE);
        String baseQuery = "SELECT s FROM Supply s";
        String partQuery = "";
        Map<String, Object> parameters = new HashMap<>();
        if(supName != null){
            partQuery = " LOWER(s.supName) LIKE LOWER(CONCAT('%', :name, '%'))";
            parameters.put("name", supName);
        }
        if(supCategory != null){
            if(partQuery.isBlank()){
                partQuery = " LOWER(s.category.cSupName) LIKE LOWER(CONCAT('%', :category, '%'))";
            }else{
                partQuery += " AND LOWER(s.category.cSupName) LIKE LOWER(CONCAT('%', :category, '%'))";
            }
            parameters.put("category", supCategory);
        }
        if(operator != null && supCount != null){
            if(partQuery.isBlank()){
                partQuery = " s.supCount "+ operator +" :count";
            }else{
                partQuery += " AND s.supCount "+operator+" :count";
            }
            parameters.put("count", supCount);
        }
        if(!partQuery.isBlank()) {
            partQuery += " ORDER BY s.category.cSupName, s.supName, s.supCount ASC";
            baseQuery = baseQuery + " WHERE " + partQuery;
        }else{
            baseQuery += " ORDER BY s.category.cSupName, s.supName, s.supCount ASC";
        }

        TypedQuery<Supply> query = entityManager.createQuery(baseQuery, Supply.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

    /**
     * Проверяет существование расходного материала
     * @param supply расходный материал
     * @return true, если существует, иначе false
     */
    public boolean exists(Supply supply) {
        return supplyRepository.countBySupNameAndCatNameAndMeasure(supply.getSupName(), supply.getCategory().getCSupName(), supply.getSupMeasure()) > 0;
    }


    /**
     * Создание расходного материала
     * @param supply данные о расходном материале
     * @param photo фотография расходного материала
     * @throws IOException если произошла ошибка сохранения фотографии
     */
    public void create(Supply supply, MultipartFile photo) throws IOException {
        boolean saveDefaultPhoto = photo == null;
        if(saveDefaultPhoto){
            supply.setSupPhotoName(NAME_DEFAULT_PHOTO);
            supplyRepository.save(supply);
        }else{
            if(filesService.isImage(photo)) {
                supplyRepository.save(supply);
                String photoName = supply.getSupId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, PATH_TO_PHOTOS + photoName);
                supply.setSupPhotoName(photoName);
                supplyRepository.save(supply);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }
    }

    /**
     * Сохранение расходного материала
     * @param supply расходный материал
     */
    public void save(Supply supply){
        supplyRepository.save(supply);
    }

    /**
     * Получение фотографии расходного материала
     * @param photoName название фотографии
     * @return Фотография в виде массива байт
     * @throws IOException если произошда ошибка получения фотографии
     */
    public byte[] getPhoto(String photoName) throws IOException {
        return filesService.getFile(PATH_TO_PHOTOS + photoName);
    }

    /**
     * Получение расходного материала по id
     * @param id id расходного материала
     * @return Объект Optional с расходным материалом, если он существует
     */
    public Optional<Supply> getById(Long id) {
        return supplyRepository.findById(id);
    }

    /**
     * Удаление расходного материала
     * @param supply расходный материал
     * @throws IOException если произошла ошибка удаления фотографии
     */
    @Transactional
    public void delete(Supply supply) throws IOException {
        String filename = supply.getSupPhotoName();
        supplyRepository.delete(supply);
        if(!filename.equals(NAME_DEFAULT_PHOTO)){
            filesService.deleteFile(PATH_TO_PHOTOS + filename);
        }

    }

    /**
     * Изменение данных о расходном материале
     * @param existedSupply существующий расходный материал
     * @param supply новые данные о расходном материале
     * @param photo фотография расходного материала
     * @throws IOException если произошла ошибка работы с фотографией расходного материала
     */
    public void edit(Supply existedSupply, Supply supply, MultipartFile photo) throws IOException {
        existedSupply.setSupCount(supply.getSupCount());
        if(photo != null){
            if(filesService.isImage(photo)){
                if(!existedSupply.getSupPhotoName().equals(NAME_DEFAULT_PHOTO)){
                    filesService.deleteFile(PATH_TO_PHOTOS + existedSupply.getSupPhotoName());
                }
                String photoName = existedSupply.getSupId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, PATH_TO_PHOTOS + photoName);
                existedSupply.setSupPhotoName(photoName);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }

        supplyRepository.save(existedSupply);
    }
}

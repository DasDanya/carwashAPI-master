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

@Service
public class SupplyService {

    private final int COUNT_ITEMS_IN_PAGE = 2;
    private static final String PATH_TO_PHOTOS= "src/main/resources/static/images/supplies/";
    private static final String NAME_DEFAULT_PHOTO = "noPhoto.jpeg";

    private final SupplyRepository supplyRepository;
    private final FilesService filesService;

    @PersistenceContext
    private EntityManager entityManager;

    public SupplyService(SupplyRepository supplyRepository, FilesService filesService) {
        this.supplyRepository = supplyRepository;
        this.filesService = filesService;
    }

    public List<Supply> get(Integer pageIndex) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE, Sort.by( "category.cSupName", "supName", "supCount"));
        return supplyRepository.findAll(pageable).getContent();
    }

    public List<Supply> search(Integer pageIndex, String supName, String supCategory, String operator, Integer supCount) {
        Pageable pageable = PageRequest.of(pageIndex, COUNT_ITEMS_IN_PAGE);
        String baseQuery = "SELECT s FROM Supply s";
        String partQuery = "";
        Map<String, Object> parameters = new HashMap<>();
        System.out.println(operator);
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

    public boolean exists(Supply supply) {
        return supplyRepository.countBySupNameAndCatNameAndMeasure(supply.getSupName(), supply.getCategory().getCSupName(), supply.getSupMeasure()) > 0;
    }
    
    public boolean existsWithoutCurrent(Supply supply){
        return supplyRepository.countBySupNameAndCatNameAndMeasureWithoutCurrent(supply.getSupName(), supply.getCategory().getCSupName(), supply.getSupMeasure(), supply.getSupId()) > 0;
    }

    public void create(Supply supply, MultipartFile photo) throws Exception {
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

    public byte[] getPhoto(String photoName) throws IOException {
        return filesService.getFile(PATH_TO_PHOTOS + photoName);
    }

    public Optional<Supply> getById(Long id) {
        return supplyRepository.findById(id);
    }

    @Transactional
    public void delete(Supply supply) throws Exception {
        String filename = supply.getSupPhotoName();
        supplyRepository.delete(supply);
        if(!filename.equals(NAME_DEFAULT_PHOTO)){
            filesService.deleteFile(PATH_TO_PHOTOS + filename);
        }

    }

    public void edit(Supply existedSupply, Supply supply, MultipartFile photo) throws Exception {
        existedSupply.setSupCount(supply.getSupCount());
        if(photo != null){
            if(filesService.isImage(photo)){
                if(!existedSupply.getSupPhotoName().equals(NAME_DEFAULT_PHOTO)){
                    filesService.deleteFile(PATH_TO_PHOTOS + existedSupply.getSupPhotoName());
                }
                String photoName = existedSupply.getSupPhotoName() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, PATH_TO_PHOTOS + photoName);
                existedSupply.setSupPhotoName(photoName);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }

        supplyRepository.save(existedSupply);
    }
}

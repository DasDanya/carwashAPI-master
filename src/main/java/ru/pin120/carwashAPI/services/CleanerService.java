package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.pin120.carwashAPI.Exceptions.FileIsNotImageException;
import ru.pin120.carwashAPI.dtos.CleanerDTO;
import ru.pin120.carwashAPI.dtos.WorkScheduleDTO;
import ru.pin120.carwashAPI.models.Box;
import ru.pin120.carwashAPI.models.Cleaner;
import ru.pin120.carwashAPI.models.CleanerStatus;
import ru.pin120.carwashAPI.models.WorkSchedule;
import ru.pin120.carwashAPI.repositories.CleanerRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class CleanerService {

    private static final String PATH_TO_PHOTOS= "src/main/resources/static/images/cleaners/";
    private static final String NAME_DEFAULT_PHOTO = "avatardefault.jpg";
    private final CleanerRepository cleanerRepository;
    private final FilesService filesService;
    private final WorkScheduleService workScheduleService;
    private final BoxService boxService;
    @PersistenceContext
    private EntityManager entityManager;

    public CleanerService(CleanerRepository cleanerRepository, FilesService filesService, WorkScheduleService workScheduleService, BoxService boxService) {
        this.cleanerRepository = cleanerRepository;
        this.filesService = filesService;
        this.workScheduleService = workScheduleService;
        this.boxService = boxService;
    }

    public List<Cleaner> get(String surname, String name, String patronymic, String phone, CleanerStatus status){
        String baseQuery = "SELECT cl FROM Cleaner cl";
        String partQuery = "";
        Map<String, Object> parameters = new HashMap<>();
        if(surname != null){
            partQuery = " LOWER(cl.clrSurname) LIKE LOWER(CONCAT('%', :surname, '%'))";
            parameters.put("surname", surname);
        }
        if(name != null){
            if(partQuery.isBlank()){
                partQuery = " LOWER(cl.clrName) LIKE LOWER(CONCAT('%', :name, '%'))";
            }else{
                partQuery += " AND LOWER(cl.clrName) LIKE LOWER(CONCAT('%', :name, '%'))";
            }
            parameters.put("name", name);
        }
        if(patronymic != null){
            if(partQuery.isBlank()){
                partQuery = " LOWER(cl.clrPatronymic) LIKE LOWER(CONCAT('%', :patronymic, '%'))";
            }else{
                partQuery += " AND LOWER(cl.clrPatronymic) LIKE LOWER(CONCAT('%', :patronymic, '%'))";
            }
            parameters.put("patronymic", patronymic);
        }
        if(phone != null){
            if(partQuery.isBlank()){
                partQuery = " LOWER(cl.clrPhone) LIKE LOWER(CONCAT('%', :phone, '%'))";
            }else{
                partQuery += " AND LOWER(cl.clrPhone) LIKE LOWER(CONCAT('%', :phone, '%'))";
            }
            parameters.put("phone", phone);
        }
        if(status != null){
            if(partQuery.isBlank()){
                partQuery = " cl.clrStatus = :status";
            }else{
                partQuery += " AND cl.clrStatus = :status";
            }
            parameters.put("status", status);
        }
        if(!partQuery.isBlank()) {
            partQuery += " ORDER BY cl.clrStatus, cl.clrSurname, cl.clrName, cl.clrPatronymic, cl.clrPhone ASC";
            baseQuery = baseQuery + " WHERE " + partQuery;
        }else{
            baseQuery += " ORDER BY cl.clrStatus, cl.clrSurname, cl.clrName, cl.clrPatronymic, cl.clrPhone ASC";
        }

        TypedQuery<Cleaner> query = entityManager.createQuery(baseQuery, Cleaner.class);
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        return query.getResultList();
    }


    public List<CleanerDTO> getCleanersWithWorkSchedule(LocalDate startInterval, LocalDate endInterval, Long boxId, boolean currentMonth){
        List<Cleaner> cleaners = (List<Cleaner>) cleanerRepository.findAll();
        Predicate<Cleaner> predicate = getCleanerPredicate(startInterval, endInterval, boxId, currentMonth);
        Box box = boxService.getById(boxId).orElse(null);
        if(box == null){
            throw new EntityNotFoundException(String.format("Бокс с номером %d не найден",boxId));
        }

        return cleaners.stream()
                .filter(predicate)
                .sorted(Comparator.comparing(Cleaner::getClrStatus)
                        .thenComparing(Cleaner::getClrSurname)
                        .thenComparing(Cleaner::getClrName)
                        .thenComparing(Cleaner::getClrPatronymic)
                        .thenComparing(Cleaner::getClrPhone))
                .map(cleaner -> {
                    List<WorkScheduleDTO> filteredSchedules = cleaner.getWorkSchedules().stream()
                            .filter(ws -> ws.getBox().getBoxId().equals(boxId) && !ws.getWsWorkDay().isBefore(startInterval) && !ws.getWsWorkDay().isAfter(endInterval))
                            .sorted(Comparator.comparing(WorkSchedule::getWsWorkDay))
                            .map(WorkScheduleService::toDTO)
                            .collect(Collectors.toList());

                    return new CleanerDTO(
                            cleaner.getClrId(),
                            cleaner.getClrSurname(),
                            cleaner.getClrName(),
                            cleaner.getClrPatronymic(),
                            cleaner.getClrPhone(),
                            cleaner.getClrStatus(),
                            box,
                            filteredSchedules
                    );
                })
                //.filter(dto -> !dto.getWorkSchedules().isEmpty())
                .collect(Collectors.toList());
    }

    private static Predicate<Cleaner> getCleanerPredicate(LocalDate startInterval, LocalDate endInterval, Long boxId, boolean currentMonth) {
        Predicate<Cleaner> predicate;
        if(currentMonth){
            predicate = cleaner -> cleaner.getClrStatus() == CleanerStatus.ACT ||
                    (cleaner.getClrStatus() == CleanerStatus.DISMISSED &&
                            cleaner.getWorkSchedules().stream()
                                    .anyMatch(ws -> ws.getBox().getBoxId().equals(boxId) && !ws.getWsWorkDay().isBefore(startInterval) && !ws.getWsWorkDay().isAfter(endInterval)));
        }else{
            predicate = cleaner ->cleaner.getWorkSchedules().stream()
                    .anyMatch(ws -> ws.getBox().getBoxId().equals(boxId) && !ws.getWsWorkDay().isBefore(startInterval) && !ws.getWsWorkDay().isAfter(endInterval));
        }
        return predicate;
    }


    public byte[] getPhoto(String photoName) throws IOException {
        return filesService.getFile(PATH_TO_PHOTOS + photoName);
    }

    public Optional<Cleaner> getById(Long clrId){
        return cleanerRepository.findById(clrId);
    }

    public void create(Cleaner cleaner, MultipartFile photo) throws Exception {
        cleaner.setClrStatus(CleanerStatus.ACT);
        boolean saveDefaultPhoto = photo == null;
        if(saveDefaultPhoto){
            cleaner.setClrPhotoName(NAME_DEFAULT_PHOTO);
            cleanerRepository.save(cleaner);
        }else{
            if(filesService.isImage(photo)) {
                cleanerRepository.save(cleaner);
                String photoName = cleaner.getClrId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, PATH_TO_PHOTOS + photoName);
                cleaner.setClrPhotoName(photoName);
                cleanerRepository.save(cleaner);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }
    }

    @Transactional
    public void delete(Cleaner cleaner) throws Exception {
        String filename = cleaner.getClrPhotoName();
        cleanerRepository.delete(cleaner);
        if(!filename.equals(NAME_DEFAULT_PHOTO)){
            filesService.deleteFile(PATH_TO_PHOTOS + filename);
        }
    }

    @Transactional
    public void edit(Cleaner existedCleaner, Cleaner cleaner, MultipartFile photo) throws Exception {
        existedCleaner.setClrSurname(cleaner.getClrSurname());
        existedCleaner.setClrName(cleaner.getClrName());
        existedCleaner.setClrPatronymic(cleaner.getClrPatronymic());
        existedCleaner.setClrPhone(cleaner.getClrPhone());
        existedCleaner.setClrStatus(cleaner.getClrStatus());
        if(existedCleaner.getClrStatus() == CleanerStatus.DISMISSED){
            workScheduleService.deleteByClrIdAndStartDate(existedCleaner.getClrId(), LocalDate.now());
        }


        if(photo != null){
            if(filesService.isImage(photo)){
                if(!existedCleaner.getClrPhotoName().equals(NAME_DEFAULT_PHOTO)){
                    filesService.deleteFile(PATH_TO_PHOTOS + existedCleaner.getClrPhotoName());
                }
                String photoName = existedCleaner.getClrId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, PATH_TO_PHOTOS + photoName);
                existedCleaner.setClrPhotoName(photoName);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }

        cleanerRepository.save(existedCleaner);

    }
}

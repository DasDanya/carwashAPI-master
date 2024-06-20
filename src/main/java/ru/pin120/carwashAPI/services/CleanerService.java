package ru.pin120.carwashAPI.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

/**
 * Сервис для мойщика
 */
@Service
public class CleanerService {

    /**
     * Репозиторий мойщика
     */
    private final CleanerRepository cleanerRepository;
    /**
     * Сервис для работы с фотографиями
     */
    private final FilesService filesService;
    /**
     * Сервис рабочих дней
     */
    private final WorkScheduleService workScheduleService;
    /**
     * Сервис боксов
     */
    private final BoxService boxService;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private Environment environment;

    /**
     * Внедрение зависимостей
     * @param cleanerRepository репозиторий мойщика
     * @param filesService сервис для работы с фотографиями
     * @param workScheduleService сервис рабочих дней
     * @param boxService сервис боксов
     */
    public CleanerService(CleanerRepository cleanerRepository, FilesService filesService, WorkScheduleService workScheduleService, BoxService boxService) {
        this.cleanerRepository = cleanerRepository;
        this.filesService = filesService;
        this.workScheduleService = workScheduleService;
        this.boxService = boxService;
    }

    /**
     * Получение списка мойщиков по различным параметрам
     * @param surname фамилия
     * @param name имя
     * @param patronymic отчество
     * @param phone номер телефона
     * @param status статус
     * @return Список мойщиков
     */
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

    /**
     * Получение списка мойщиков с их рабочими днями
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param boxId id бокса
     * @param currentMonth текущий ли месяц
     * @return Список мойщиков с их рабочими днями
     */
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

    /**
     * Получение предиката для фильтрации мойщиков
     * @param startInterval начало временного интервала
     * @param endInterval конец временного интервала
     * @param boxId id бокса
     * @param currentMonth текущий ли месяц
     * @return Предикат для фильтрации мойщиков
     */
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

    /**
     * Получение фотографии мойщика
     * @param photoName название фотографии
     * @return Фотография мойщика
     * @throws IOException если произошла ошибка при чтении файла фотографии
     */
    public byte[] getPhoto(String photoName) throws IOException {
        return filesService.getFile(environment.getProperty("PATH_TO_PHOTOS_CLEANER") + photoName);
    }

    /**
     * Получение мойщика по id
     * @param clrId id мойщика
     * @return Объект Optional с мойщиком, если он существует
     */
    public Optional<Cleaner> getById(Long clrId){
        return cleanerRepository.findById(clrId);
    }

    /**
     * Создание мойщика
     * @param cleaner данные о мойщике
     * @param photo фотография мойщика
     * @throws IOException если произошла ошибка при сохранении фотографии
     */
    public void create(Cleaner cleaner, MultipartFile photo) throws IOException {
        cleaner.setClrStatus(CleanerStatus.ACT);
        boolean saveDefaultPhoto = photo == null;
        if(saveDefaultPhoto){
            cleaner.setClrPhotoName(environment.getProperty("NAME_DEFAULT_PHOTO_CLEANER"));
            cleanerRepository.save(cleaner);
        }else{
            if(filesService.isImage(photo)) {
                cleanerRepository.save(cleaner);
                String photoName = cleaner.getClrId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, environment.getProperty("PATH_TO_PHOTOS_CLEANER") + photoName);
                cleaner.setClrPhotoName(photoName);
                cleanerRepository.save(cleaner);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }
    }

    /**
     * Удаление мойщика
     * @param cleaner мойщик
     * @throws IOException если произошла ошибка при удалении фотографии мойщика
     */
    @Transactional
    public void delete(Cleaner cleaner) throws IOException {
        String filename = cleaner.getClrPhotoName();
        cleanerRepository.delete(cleaner);
        if(!filename.equals(environment.getProperty("NAME_DEFAULT_PHOTO_CLEANER"))){
            filesService.deleteFile(environment.getProperty("PATH_TO_PHOTOS_CLEANER") + filename);
        }
    }

    /**
     * Изменение данных о мойщике
     * @param existedCleaner изменяемый мойщик
     * @param cleaner новые данные о мойщике
     * @param photo фотография мойщика
     * @throws IOException если произошла ошибка при работе с фотографией мойщика
     */
    @Transactional
    public void edit(Cleaner existedCleaner, Cleaner cleaner, MultipartFile photo) throws IOException {
        existedCleaner.setClrSurname(cleaner.getClrSurname());
        existedCleaner.setClrName(cleaner.getClrName());
        existedCleaner.setClrPatronymic(cleaner.getClrPatronymic());
        existedCleaner.setClrPhone(cleaner.getClrPhone());
        existedCleaner.setClrStatus(cleaner.getClrStatus());
        if(existedCleaner.getClrStatus() == CleanerStatus.DISMISSED){
            WorkSchedule workSchedule = existedCleaner.getWorkSchedules().stream()
                    .filter(w->w.getWsWorkDay().equals(LocalDate.now()))
                    .findFirst()
                    .orElse(null);
            if(workSchedule != null){
                throw new IllegalArgumentException(String.format("Нельзя уволить мойщика %s %s %s, так как он сегодня работает", existedCleaner.getClrSurname(), existedCleaner.getClrName(), existedCleaner.getClrPatronymic() == null ? "" : existedCleaner.getClrPatronymic()));
            }
            workScheduleService.deleteByClrIdAndStartDate(existedCleaner.getClrId(), LocalDate.now());
        }


        if(photo != null){
            if(filesService.isImage(photo)){
                if(!existedCleaner.getClrPhotoName().equals(environment.getProperty("NAME_DEFAULT_PHOTO_CLEANER"))){
                    filesService.deleteFile(environment.getProperty("PATH_TO_PHOTOS_CLEANER") + existedCleaner.getClrPhotoName());
                }
                String photoName = existedCleaner.getClrId() + photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf('.'));
                filesService.saveImage(photo, environment.getProperty("PATH_TO_PHOTOS_CLEANER") + photoName);
                existedCleaner.setClrPhotoName(photoName);
            }else{
                throw new FileIsNotImageException("Файл не является изображением");
            }
        }

        cleanerRepository.save(existedCleaner);

    }
}

package ru.pin120.carwashAPI.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Модель транспорта клиента
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clients_transport",uniqueConstraints = @UniqueConstraint(columnNames = {"cl_tr_state_number","tr_id","cl_id"}))
public class ClientsTransport {

    /**
     * id транспорта клиента
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clTrId;

    /**
     * Госномер
     */
    @Column(nullable = false, name = "cl_tr_state_number", length = 9)
    @Size(max = 9, message = "Максимальная длина = 9 символов")
    @NotBlank(message = "Необходимо ввести гос номер транспорта")
    private String clTrStateNumber;

    /**
     * Транспорт
     */
    @ManyToOne
    @NotNull(message = "Необходимо указать данные о транспорте")
    @JoinColumn(name = "tr_id", nullable = false)
    @Valid
    private Transport transport;

    /**
     * Клиент
     */
    @ManyToOne
    @NotNull(message = "Необходимо указать владельца")
    @JoinColumn(name = "cl_id",nullable = false)
    @Valid
    private Client client;

    /**
     * Список заказов
     */
    @OneToMany(mappedBy = "clientTransport")
    @JsonIgnore
    private List<Booking> bookings;

}

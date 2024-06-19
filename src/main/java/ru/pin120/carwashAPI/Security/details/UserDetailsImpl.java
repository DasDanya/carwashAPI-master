package ru.pin120.carwashAPI.security.details;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.pin120.carwashAPI.models.User;

import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса {@link UserDetails} для Spring Security.
 * Этот класс представляет аутентифицированного пользователя и его полномочия
 */
@Getter
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    /**
     * Пользователь
     */
    private User user;

    /**
     * Полномочия
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Возвращает полномочия, предоставленные пользователю
     *
     * @return Коллекция предоставленных полномочий
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Возвращает пароль, используемый для аутентификации пользователя
     *
     * @return Пароль пользователя
     */
    @Override
    public String getPassword() {return user.getUsPassword();}

    /**
     * Возвращает имя пользователя
     *
     * @return Имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsName();
    }

    /**
     * Указывает, истек ли срок действия учетной записи пользователя
     *
     * @return true, если срок действия учетной записи не истек, иначе false
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Указывает, заблокирован ли пользователь или нет
     *
     * @return true, если пользователь не заблокирован, иначе false
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Указывает, истек ли срок действия учетных данных (пароля) пользователя
     *
     * @return true, если срок действия учетных данных не истек, иначе false
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Указывает, включен или отключен пользователь
     *
     * @return true, если пользователь включен, иначе false
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Фабричный метод для создания экземпляра UserDetailsImpl
     *
     * @param user пользователь
     * @return экземпляр UserDetailsImpl
     */

    public static UserDetailsImpl build(User user){
        SimpleGrantedAuthority role = new SimpleGrantedAuthority(user.getUsRole().name());
        List<GrantedAuthority> authorities = List.of(role);

        return new UserDetailsImpl(user, authorities);
    }
}

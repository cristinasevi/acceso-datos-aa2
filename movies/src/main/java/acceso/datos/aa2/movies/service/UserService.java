package acceso.datos.aa2.movies.service;

import acceso.datos.aa2.movies.domain.User;
import acceso.datos.aa2.movies.dto.UserDto;
import acceso.datos.aa2.movies.dto.UserOutDto;
import acceso.datos.aa2.movies.exception.UserNotFoundException;
import acceso.datos.aa2.movies.repository.UserRepository;
import acceso.datos.aa2.movies.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    public User add(User user) {
        return userRepository.save(user);
    }

    public void delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    public List<UserOutDto> findAll(Boolean premium, Boolean active, LocalDate registrationDateFrom) {
        List<User> users;

        boolean hasPremium = premium != null;
        boolean hasActive = active != null;
        boolean hasRegistrationDateFrom = registrationDateFrom != null;

        if (hasPremium && hasActive && hasRegistrationDateFrom) {
            // 3 filtros
            users = userRepository.findByPremiumAndActiveAndRegistrationDateGreaterThanEqual(premium, active, registrationDateFrom);
        } else if (hasPremium && hasActive) {
            // premium + active
            users = userRepository.findByPremiumAndActive(premium, active);
        } else if (hasPremium && hasRegistrationDateFrom) {
            // premium + registrationDateFrom
            users = userRepository.findByPremiumAndRegistrationDateGreaterThanEqual(premium, registrationDateFrom);
        } else if (hasActive && hasRegistrationDateFrom) {
            // active + registrationDateFrom
            users = userRepository.findByActiveAndRegistrationDateGreaterThanEqual(active, registrationDateFrom);
        } else if (hasPremium) {
            // Solo premium
            users = userRepository.findByPremium(premium);
        } else if (hasActive) {
            // Solo active
            users = userRepository.findByActive(active);
        } else if (hasRegistrationDateFrom) {
            // Solo registrationDateFrom
            users = userRepository.findByRegistrationDateGreaterThanEqual(registrationDateFrom);
        } else {
            // Sin filtros
            users = userRepository.findAll();
        }

        return modelMapper.map(users, new TypeToken<List<UserOutDto>>() {}.getType());
    }

    public UserDto findById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        // Campo calculado
        if (user.getBirthDate() != null) {
            userDto.setAge(DateUtil.calculateAge(user.getBirthDate()));
        }

        return userDto;
    }

    public User modify(long id, User user) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        modelMapper.map(user, existingUser);
        existingUser.setId(id);

        return userRepository.save(existingUser);
    }
}

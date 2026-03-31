package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findByPremium(Boolean premium);

    List<User> findByActive(Boolean active);

    List<User> findByRegistrationDateGreaterThanEqual(LocalDate registrationDateFrom);

    // Combinaciones de 2 filtros
    List<User> findByPremiumAndActive(Boolean premium, Boolean active);

    List<User> findByPremiumAndRegistrationDateGreaterThanEqual(Boolean premium, LocalDate registrationDateFrom);

    List<User> findByActiveAndRegistrationDateGreaterThanEqual(Boolean active, LocalDate registrationDateFrom);

    // Combinación de 3 filtros
    List<User> findByPremiumAndActiveAndRegistrationDateGreaterThanEqual(Boolean premium, Boolean active, LocalDate registrationDateFrom);
}

package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.Studio;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudioRepository extends CrudRepository<Studio, Long> {

    List<Studio> findAll();

    List<Studio> findByCountry(String country);

    List<Studio> findByActive(Boolean active);

    List<Studio> findByFoundationYearGreaterThanEqual(Integer foundationYear);

    // Combinaciones de 2 filtros
    List<Studio> findByCountryAndActive(String country, Boolean active);

    List<Studio> findByCountryAndFoundationYearGreaterThanEqual(String country, Integer foundationYear);

    List<Studio> findByActiveAndFoundationYearGreaterThanEqual(Boolean active, Integer foundationYear);

    // Combinación de 3 filtros
    List<Studio> findByCountryAndActiveAndFoundationYearGreaterThanEqual(String country, Boolean active, Integer foundationYear);
}

package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.Director;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectorRepository extends CrudRepository<Director, Long> {

    List<Director> findAll();

    List<Director> findByNationality(String nationality);

    List<Director> findByActive(Boolean active);

    List<Director> findByAwardsGreaterThanEqual(Integer minAwards);

    // Combinaciones de 2 filtros
    List<Director> findByNationalityAndActive(String nationality, Boolean active);

    List<Director> findByNationalityAndAwardsGreaterThanEqual(String nationality, Integer minAwards);

    List<Director> findByActiveAndAwardsGreaterThanEqual(Boolean active, Integer minAwards);

    // Combinación de 3 filtros
    List<Director> findByNationalityAndActiveAndAwardsGreaterThanEqual(String nationality, Boolean active, Integer minAwards);
}

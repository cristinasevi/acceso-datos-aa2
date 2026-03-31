package acceso.datos.aa2.movies.repository;

import acceso.datos.aa2.movies.domain.Actor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActorRepository extends CrudRepository<Actor, Long> {

    List<Actor> findAll();

    List<Actor> findByNationality(String nationality);

    List<Actor> findByActive(Boolean active);

    List<Actor> findByActorType(String actorType);

    // Combinaciones de 2 filtros
    List<Actor> findByNationalityAndActive(String nationality, Boolean active);

    List<Actor> findByNationalityAndActorType(String nationality, String actorType);

    List<Actor> findByActiveAndActorType(Boolean active, String actorType);

    // Combinación de 3 filtros
    List<Actor> findByNationalityAndActiveAndActorType(String nationality, Boolean active, String actorType);
}

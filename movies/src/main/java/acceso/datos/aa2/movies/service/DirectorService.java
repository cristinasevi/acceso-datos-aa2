package acceso.datos.aa2.movies.service;

import acceso.datos.aa2.movies.domain.Director;
import acceso.datos.aa2.movies.dto.DirectorDto;
import acceso.datos.aa2.movies.dto.DirectorOutDto;
import acceso.datos.aa2.movies.exception.DirectorNotFoundException;
import acceso.datos.aa2.movies.repository.DirectorRepository;
import acceso.datos.aa2.movies.util.DateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectorService {

    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Director add(Director director) {
        return directorRepository.save(director);
    }

    public void delete(long id) throws DirectorNotFoundException {
        Director director = directorRepository.findById(id)
                .orElseThrow(DirectorNotFoundException::new);
        directorRepository.delete(director);
    }

    public List<DirectorOutDto> findAll(String nationality, Boolean active, Integer minAwards) {
        List<Director> directors;

        boolean hasNationality = nationality != null && !nationality.isEmpty();
        boolean hasActive = active != null;
        boolean hasMinAwards = minAwards != null;

        if (hasNationality && hasActive && hasMinAwards) {
            // 3 filtros
            directors = directorRepository.findByNationalityAndActiveAndAwardsGreaterThanEqual(nationality, active, minAwards);
        } else if (hasNationality && hasActive) {
            // nationality + active
            directors = directorRepository.findByNationalityAndActive(nationality, active);
        } else if (hasNationality && hasMinAwards) {
            // nationality + minAwards
            directors = directorRepository.findByNationalityAndAwardsGreaterThanEqual(nationality, minAwards);
        } else if (hasActive && hasMinAwards) {
            // active + minAwards
            directors = directorRepository.findByActiveAndAwardsGreaterThanEqual(active, minAwards);
        } else if (hasNationality) {
            // Solo nationality
            directors = directorRepository.findByNationality(nationality);
        } else if (hasActive) {
            // Solo active
            directors = directorRepository.findByActive(active);
        } else if (hasMinAwards) {
            // Solo minAwards
            directors = directorRepository.findByAwardsGreaterThanEqual(minAwards);
        } else {
            // Sin filtros
            directors = directorRepository.findAll();
        }

        return modelMapper.map(directors, new TypeToken<List<DirectorOutDto>>() {}.getType());
    }

    public DirectorDto findById(long id) throws DirectorNotFoundException {
        Director director = directorRepository.findById(id)
                .orElseThrow(DirectorNotFoundException::new);

        DirectorDto directorDto = modelMapper.map(director, DirectorDto.class);

        // Campo calculado
        if (director.getBirthDate() != null) {
            directorDto.setAge(DateUtil.calculateAge(director.getBirthDate()));
        }

        return directorDto;
    }

    public Director modify(long id, Director director) throws DirectorNotFoundException {
        Director existingDirector = directorRepository.findById(id)
                .orElseThrow(DirectorNotFoundException::new);

        modelMapper.map(director, existingDirector);
        existingDirector.setId(id);

        return directorRepository.save(existingDirector);
    }
}

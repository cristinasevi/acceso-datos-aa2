package acceso.datos.aa2.movies.service;

import acceso.datos.aa2.movies.domain.Studio;
import acceso.datos.aa2.movies.dto.StudioDto;
import acceso.datos.aa2.movies.dto.StudioOutDto;
import acceso.datos.aa2.movies.exception.StudioNotFoundException;
import acceso.datos.aa2.movies.repository.StudioRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudioService {

    @Autowired
    private StudioRepository studioRepository;

    @Autowired
    private ModelMapper modelMapper;

    public Studio add(Studio studio) {
        return studioRepository.save(studio);
    }

    public void delete(long id) throws StudioNotFoundException {
        Studio studio = studioRepository.findById(id)
                .orElseThrow(StudioNotFoundException::new);
        studioRepository.delete(studio);
    }

    public List<StudioOutDto> findAll(String country, Integer foundationYear, Boolean active) {
        List<Studio> studios;

        boolean hasCountry = country != null && !country.isEmpty();
        boolean hasFoundationYear = foundationYear != null;
        boolean hasActive = active != null;

        if (hasCountry && hasFoundationYear && hasActive) {
            // 3 filtros
            studios = studioRepository.findByCountryAndActiveAndFoundationYearGreaterThanEqual(country, active, foundationYear);
        } else if (hasCountry && hasFoundationYear) {
            // country + foundationYear
            studios = studioRepository.findByCountryAndFoundationYearGreaterThanEqual(country, foundationYear);
        } else if (hasCountry && hasActive) {
            // country + active
            studios = studioRepository.findByCountryAndActive(country, active);
        } else if (hasFoundationYear && hasActive) {
            // foundationYear + active
            studios = studioRepository.findByActiveAndFoundationYearGreaterThanEqual(active, foundationYear);
        } else if (hasCountry) {
            // Solo country
            studios = studioRepository.findByCountry(country);
        } else if (hasFoundationYear) {
            // Solo foundationYear
            studios = studioRepository.findByFoundationYearGreaterThanEqual(foundationYear);
        } else if (hasActive) {
            // Solo active
            studios = studioRepository.findByActive(active);
        } else {
            // Sin filtros
            studios = studioRepository.findAll();
        }

        return modelMapper.map(studios, new TypeToken<List<StudioOutDto>>() {}.getType());
    }

    public StudioDto findById(long id) throws StudioNotFoundException {
        Studio studio = studioRepository.findById(id)
                .orElseThrow(StudioNotFoundException::new);

        return modelMapper.map(studio, StudioDto.class);
    }

    public Studio modify(long id, Studio studio) throws StudioNotFoundException {
        Studio existingStudio = studioRepository.findById(id)
                .orElseThrow(StudioNotFoundException::new);

        modelMapper.map(studio, existingStudio);
        existingStudio.setId(id);

        return studioRepository.save(existingStudio);
    }
}

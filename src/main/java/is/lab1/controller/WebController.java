package is.lab1.controller;

import is.lab1.model.City;
import is.lab1.model.Climate;
import is.lab1.model.Government;
import is.lab1.model.StandardOfLiving;
import is.lab1.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class WebController {
    
    private final CityService cityService;

    @Autowired
    public WebController(CityService cityService) {
        this.cityService = cityService;
    }

    private static final String CLIMATES = "climates";
    private static final String GOVERNMENTS = "governments";
    private static final String STANDARD_OF_LIVINGS = "standard_of_living";
    
    @GetMapping("/")
    public String index(Model model,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "id") String sortBy,
                       @RequestParam(defaultValue = "asc") String sortDir,
                       @RequestParam(required = false) String name) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<City> cities = cityService.getCitiesByName(name, pageable);
        
        model.addAttribute("cities", cities);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cities.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("name", name);
        model.addAttribute(CLIMATES, Climate.values());
        model.addAttribute(GOVERNMENTS, Government.values());
        model.addAttribute(STANDARD_OF_LIVINGS, StandardOfLiving.values());
        
        return "index";
    }
    
    private static final String REDIRECT_HOME = "redirect:/";
    
    @GetMapping("/city/{id}")
    public String viewCity(@PathVariable Integer id, Model model) {
        Optional<City> city = cityService.getCityById(id);
        if (city.isPresent()) {
            model.addAttribute("city", city.get());
            return "city-detail";
        }
        return REDIRECT_HOME;
    }
    
    @GetMapping("/city/new")
    public String newCityForm(Model model) {
        model.addAttribute("city", new City());
        model.addAttribute(CLIMATES, Climate.values());
        model.addAttribute(GOVERNMENTS, Government.values());
        model.addAttribute("standardsOfLiving", StandardOfLiving.values());
        return "city-form";
    }
    
    @GetMapping("/city/edit/{id}")
    public String editCityForm(@PathVariable Integer id, Model model) {
        Optional<City> city = cityService.getCityById(id);
        if (city.isPresent()) {
            model.addAttribute("city", city.get());
            model.addAttribute(CLIMATES, Climate.values());
            model.addAttribute(GOVERNMENTS, Government.values());
            model.addAttribute("standardsOfLiving", StandardOfLiving.values());
            return "city-form";
        }
        return REDIRECT_HOME;
    }
    
    @PostMapping("/city/save")
    public String saveCity(@ModelAttribute City city) {
        try {
            cityService.saveCity(city);
            return REDIRECT_HOME;
        } catch (Exception e) {
            return REDIRECT_HOME + "city/new?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/city/update/{id}")
    public String updateCity(@PathVariable Integer id, @ModelAttribute City city) {
        try {
            city.setId(id);
            cityService.saveCity(city);
            return REDIRECT_HOME + "city/" + id;
        } catch (Exception e) {
            return REDIRECT_HOME + "city/edit/" + id + "?error=" + e.getMessage();
        }
    }
    
    @PostMapping("/city/delete/{id}")
    public String deleteCity(@PathVariable Integer id) {
        try {
            cityService.deleteCity(id);
            return REDIRECT_HOME;
        } catch (Exception e) {
            return REDIRECT_HOME + "?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/special-operations")
    public String specialOperations(Model model) {
        model.addAttribute(CLIMATES, Climate.values());
        return "special-operations";
    }
}

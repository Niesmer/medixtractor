package AHA.medixtractor.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import AHA.medixtractor.dto.MedicamentSummaryView;
import AHA.medixtractor.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping("/cis")
    public List<Long> getFavoriteCis(@RequestHeader("Authorization") String authorization) {
        return favoriteService.getFavoriteCis(authorization);
    }

    @GetMapping
    public List<MedicamentSummaryView> getFavorites(@RequestHeader("Authorization") String authorization) {
        return favoriteService.getFavorites(authorization);
    }

    @GetMapping("/{cis}")
    public boolean isFavorite(
        @RequestHeader("Authorization") String authorization,
        @PathVariable Long cis
    ) {
        return favoriteService.isFavorite(authorization, cis);
    }

    @PostMapping("/{cis}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFavorite(
        @RequestHeader("Authorization") String authorization,
        @PathVariable Long cis
    ) {
        favoriteService.addFavorite(authorization, cis);
    }

    @DeleteMapping("/{cis}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(
        @RequestHeader("Authorization") String authorization,
        @PathVariable Long cis
    ) {
        favoriteService.removeFavorite(authorization, cis);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException exception) {
        return exception.getMessage();
    }
}

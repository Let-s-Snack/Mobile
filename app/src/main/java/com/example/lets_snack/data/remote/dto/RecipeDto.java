package com.example.lets_snack.data.remote.dto;

//import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

public class RecipeDto {
    private String id;
    private String name;
    private String description;
    private String urlPhoto;
    private List<IngredientDto> ingredients;
    private List<CommentDto> coments;
    private List<String> preparationMethods;
    private List<Object> brokenRestrictions;
    private Date creationDate;
    private double rating;
    private boolean isFavorite;


    private void RecipeDto(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public List<IngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientDto> ingredients) {
        this.ingredients = ingredients;
    }

    public List<CommentDto> getComents() {
        return coments;
    }

    public void setComents(List<CommentDto> coments) {
        this.coments = coments;
    }

    public List<String> getPreparationMethods() {
        return preparationMethods;
    }

    public void setPreparationMethods(List<String> preparationMethods) {
        this.preparationMethods = preparationMethods;
    }

    public List<Object> getBrokenRestrictions() {
        return brokenRestrictions;
    }

    public void setBrokenRestrictions(List<Object> brokenRestrictions) {
        this.brokenRestrictions = brokenRestrictions;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public String toString() {
        return "RecipeDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", urlPhoto='" + urlPhoto + '\'' +
                ", ingredients=" + ingredients +
                ", coments=" + coments +
                ", preparationMethods=" + preparationMethods +
                ", brokenRestrictions=" + brokenRestrictions +
                ", creationDate=" + creationDate +
                ", rating=" + rating +
                ", isFavorite=" + isFavorite +
                '}';
    }
}

package com.example.lets_snack.data.remote.dto;

//import org.bson.types.ObjectId;

public class CategoryDto {
    private String id;
    private String name;
    private String urlPhoto;
    private String description;

    private void CategoryDto(){}

    public CategoryDto(String id, String name, String imageUrl, String description) {
        this.id = id;
        this.name = name;
        this.urlPhoto = imageUrl;
        this.description = description;
    }

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

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String imageUrl) {
        this.urlPhoto = imageUrl;
    }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "CategoryDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", urlPhoto='" + urlPhoto + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

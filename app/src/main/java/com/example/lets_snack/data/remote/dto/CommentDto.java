package com.example.lets_snack.data.remote.dto;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class CommentDto {
    private String personsName;
    private String message;
    private Date creationDate;
    private float rating;

    private void CommentDto(){}

    public CommentDto(String personsName, String message, Date creationDate, float rate) {
        this.personsName = personsName;
        this.message = message;
        this.creationDate = creationDate;
        this.rating = rate;
    }

    public String getPersonsName() {
        return personsName;
    }

    public void setPersonsName(String personsName) {
        this.personsName = personsName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreationDate() {
        Date today = new Date();
        long diffInMillies = today.getTime() - creationDate.getTime();
        // convertendo a diferença de milissegundos para dias
        long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return String.valueOf(diffInDays);
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "CommentDto{" +
                "personsName='" + personsName + '\'' +
                ", message='" + message + '\'' +
                ", date='" + creationDate + '\'' +
                ", rate=" + rating +
                '}';
    }
}

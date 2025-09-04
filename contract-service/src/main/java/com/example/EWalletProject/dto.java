package com.example.EWalletProject;



import javax.persistence.Entity;
import java.time.LocalDate;



@Entity
public class dto {


    String text;
    Integer num;
    LocalDate date;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;

    }
}

package com.example.EWalletProject;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class controller {


    @PostMapping("/store")
    public String store(@RequestBody dto data) throws IOException {

        String text= data.getText();
        Integer num = data.getNum();
        LocalDate date = data.getDate();

        try(FileWriter fileWriter = new FileWriter("data.txt", true)){
            try {
                fileWriter.write("Text: " + text + "\n");
                fileWriter.write("Number: " + num + "\n");
                fileWriter.write("Date: " + date.toString() + "\n");
                fileWriter.write("------------------------\n");
            } catch (Exception e) {
                e.printStackTrace();
                return "Error writing to file";
            } finally {
                fileWriter.close();
                return "Data stored successfully!";
            }
        }



    }


}

package com.vallem.Perguntas.API.config;

import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class DataConfiguration {
    public static void provideFolders() {
        File file = new File("data/");
        if(!file.exists()) file.mkdir();
    }
}

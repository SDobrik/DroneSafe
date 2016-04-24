package com.example.owner.dronesafe;

/**
 * Created by Owner on 2016-04-23.
 */
public class WeatherInstance {
    private String description;
    private String wind;
    private String temperature;

    public String getDescription(){
        return description;
    }
    public String getWind(){
        return wind;
    }
    public String getTemperature(){
        return temperature;
    }

    public void setDescription(String description){
        this.description = description;
    }
    public void setWind(String wind){
        this.wind = wind;
    }
    public void setTemperature(String temperature){
        this.temperature = temperature;
    }
}

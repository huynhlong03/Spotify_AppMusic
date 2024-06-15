package com.example.musicapp_android.Data;

public class Artists {
    private String name ;
    private String id;

    public Artists() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getFans() {
        return fans;
    }

    public void setFans(Integer fans) {
        this.fans = fans;
    }

    public Artists(String name, String id, String image, Integer fans) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.fans = fans;
    }

    private String image;
    private Integer fans;


}

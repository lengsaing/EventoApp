package com.app.sangleng.evento;

public class EventItem {
    private String title;
    private String price;
    private String image;
    private int registerCount;

    public EventItem(){


    }

    public EventItem(String event_title, String event_price, String event_image, int count){
        this.title = event_title;
        this.price = event_price;
        this.image = event_image;
        this.registerCount = count;
    }

    public String getTitle(){
        return this.title;
    }

    public String getPrice(){
        return this.price;
    }

    public String getImage(){
        return this.image;
    }

    public int getRegisterCount(){
        return this.registerCount;
    }




}

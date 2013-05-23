package com.atlantbh.nutch.index.alternativedataflow.flow;

public class Product {

    private String name;

    private String availability;

    private String[] features;

    private String[] featureValues;

    private String price;

    private String brand;

    private String url;

    public Product(final String name, final String price, final String availability, final String brand, final String[] features,
            final String[] featureValues, final String url) {
        this.url = url;
        this.name = name.trim();
        this.availability = availability;
        this.features = features;
        this.featureValues = featureValues;
        this.price = price;
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(final String availability) {
        this.availability = availability;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(final String brand) {
        this.brand = brand;
    }

    public String[] getFeatures() {
        return features;
    }

    public void setFeatures(final String[] features) {
        this.features = features;
    }

    public String[] getFeatureValues() {
        return featureValues;
    }

    public void setFeatureValues(final String[] featureValues) {
        this.featureValues = featureValues;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

}

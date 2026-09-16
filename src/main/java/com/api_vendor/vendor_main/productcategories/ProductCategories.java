package com.api_vendor.vendor_main.productcategories;
import jakarta.persistence.*;

@Entity
@Table(name="productcategories")

public class ProductCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int categoryID;

    @Column(name="name")
    private String name;

    @Column(name="description")
    private String description;

    public int getCategoryID(){
        return categoryID;
    }

    public void setCategoryID(int categoryID){
        this.categoryID=categoryID;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description=description;
    }
}

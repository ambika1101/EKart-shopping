package e.droid.scrap.Model;

public class Products {
    // a model to represent a product
    private String pname,description,price,image,category;

    public Products(String pname, String description, String price, String image, String category) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
    }
public Products(){

}
    public String getPname() {
        return pname;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }
}

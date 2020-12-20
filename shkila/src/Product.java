public class Product {

    private long id;
    private String name;
    private int usdPrice;

    public Product(long id, String name, int usdPrice, int counter) {
        this.id = id;
        this.name = name;
        this.usdPrice = usdPrice;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsdPrice() {
        return usdPrice;
    }

    public void setUsdPrice(int usdPrice) {
        this.usdPrice = usdPrice;
    }
}

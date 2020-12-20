import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Client {

    private String name;
    private int usdCash;
    private boolean isBlackListed;
    private Map<Product, Integer> cart;

    public Client(String name, int usdCash) {
        this.name = name;
        this.usdCash = usdCash;
        this.isBlackListed = false;
        this.cart = new TreeMap<>();
    }

    public boolean addProductToCart(Map<Product,Integer> products, Product product, int count) {

        if (products.get(product).intValue() == 0) {
            System.out.println("Out of stock!");
            return false;
        } else if (products.get(product).intValue() < count) {
            System.out.println("Not enough in stock!");
            return false;
        } else cart.put(product, count);
        return true;
    }

    public boolean changeProductCountInCart(Map<Product,Integer> products, Product product, int newCount) {
        if (!cart.containsKey(product)) {
            System.out.println("No such product in cart!");
            return false;
        } else if (newCount == 0) {
            if (cart.containsKey(product)) {
                cart.remove(product);
                return true;
            }
        } else if (newCount > 0) {
            if (cart.containsKey(product)) {
                cart.replace(product, newCount);
            }
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsdCash() {
        return usdCash;
    }

    public void setUsdCash(int usdCash) {
        this.usdCash = usdCash;
    }

    public boolean isBlackListed() {
        return isBlackListed;
    }

    public void setBlackListed(boolean blackListed) {
        isBlackListed = blackListed;
    }

    public Map<Product, Integer> getCart() {
        return cart;
    }

    public void setCart(Map<Product, Integer> cart) {
        this.cart = cart;
    }
}

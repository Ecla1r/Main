import java.util.ArrayList;
import java.util.Map;

public class Store {

    private Map<Product, Integer> products;
    private ArrayList<Client> clients;
    private static long orderCnt = 1;

    public static void main (String [] args) {
        Store store = new Store();
    }
}

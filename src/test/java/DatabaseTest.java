public class DatabaseTest {
    public static void main(String[] args) {
        Database database = new Database();
        database.addFromSearch("Cloverleaf Interchange");
        System.out.println(database.get(database.sizeDB() - 1));
    }
}

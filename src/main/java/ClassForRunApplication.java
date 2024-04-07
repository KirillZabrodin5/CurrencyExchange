import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ClassForRunApplication {
    public static void main(String[] args)  {
        try (

                Connection connection = ConnectionManager.open();
                Statement statement = connection.createStatement()
        )
        {
            ResultSet rs = statement.executeQuery("select * from Currencies");
            while(rs.next())
            {

                Currency currency = new Currency(
                        rs.getInt("ID"),
                        rs.getString("FullName"),
                        rs.getString("code"),
                        rs.getString("Sign")
                );
                System.out.println(currency);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
    }
}

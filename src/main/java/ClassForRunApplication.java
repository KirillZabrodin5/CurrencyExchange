import TipoRestApi.GetCurrency;
import jakarta.servlet.http.HttpServlet;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ClassForRunApplication {
    public static void main(String[] args)  {

        System.out.println(GetCurrency.GETID(2));
//        System.out.println(GetCurrency.GETCODE("USD"));
//        System.out.println(GetCurrency.GETFULLNAME("Russian Ruble"));
//        System.out.println(GetCurrency.GETSIGN("₸"));

//        try(
//                Connection connection = ConnectionManager.open();
//                PreparedStatement statement = connection.prepareStatement("""
//INSERT INTO Currencies(code, full_name, sign)
//       VALUES ('AUDv', 'Australian dollar', 'A€')
//       """)
//        ) {
//            statement.executeUpdate();
//            System.out.println();
//        } catch (SQLException sqlException) {
//            sqlException.printStackTrace();
//        }
    }
}

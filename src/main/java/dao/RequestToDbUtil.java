package dao;

import model.Currency;
import utils.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Класс для получения валюты по заданному коду
*/
public final class RequestToDbUtil {
    public static Currency findCurrencyByCode(String code) {
        String sql = """
                    SELECT *
                    FROM Currencies 
                    WHERE code = ?""";

        Currency curr = null;

        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("id"),
                    rs.getString("full_name"),
                    rs.getString("code"),
                    rs.getString("sign"));
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
        return curr;
    }
}

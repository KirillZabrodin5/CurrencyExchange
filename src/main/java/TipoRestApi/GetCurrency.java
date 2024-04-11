package TipoRestApi;

import model.Currency;
import utils.ConnectionManager;

import java.sql.*;

public final class GetCurrency {
    static ResultSet rs = null;

    public static Currency GETID(Integer parameterForFind)  {
        Currency curr = null;
        String sql = """
                SELECT ID, code, FullName, Sign
                FROM Currencies
                WHERE ID = ?
""";
        try(
                Connection connection = ConnectionManager.open();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, parameterForFind);
            rs = preparedStatement.executeQuery();

            curr = new Currency(
                    rs.getInt("ID"),
                    rs.getString("code"),
                    rs.getString("FullName"),
                    rs.getString("Sign")
            );

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }

        return curr;
    }

    public static Currency GETCODE(String parameterForFind)  {
        String sql = """
                    SELECT ID, code, FullName, Sign
                    FROM Currencies WHERE code = ?""";
        Currency curr = null;
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            stmt.setString(1, parameterForFind);
            rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("ID"),
                    rs.getString("FullName"),
                    rs.getString("code"),
                    rs.getString("Sign"));


        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
        return curr;
    }

    public static Currency GETFULLNAME(String parameterForFind)  {
        String sql = """
                    SELECT ID, code, FullName, Sign
                    FROM Currencies WHERE FullName = ?""";
        Currency curr = null;
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            stmt.setString(1, parameterForFind);
            rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("ID"),
                    rs.getString("FullName"),
                    rs.getString("code"),
                    rs.getString("Sign"));


        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
        return curr;
    }

    public static Currency GETSIGN(String parameterForFind)  {
        String sql = """
                    SELECT ID, code, FullName, Sign
                    FROM Currencies WHERE Sign = ?""";
        Currency curr = null;
        try (
                Connection con = ConnectionManager.open();
                PreparedStatement stmt = con.prepareStatement(sql);
        )
        {
            stmt.setString(1, parameterForFind);
            rs = stmt.executeQuery();

            curr = new Currency(rs.getInt("ID"),
                    rs.getString("FullName"),
                    rs.getString("code"),
                    rs.getString("Sign"));
        }
        catch(SQLException e)
        {
            e.printStackTrace(System.err);
        }
        return curr;
    }
}

package org.example.demo6;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginTable {

    public static void createTable(){
        String sql = "CREATE TABLE IF NOT EXISTS LoginTable (\n"
                + "    Username text PRIMARY KEY,\n"
                + "	   Password text NOT NULL,\n"
                + "    Salt text NOT NULL\n"
                + ");";

        try (Connection conn = LoginBase.connection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}

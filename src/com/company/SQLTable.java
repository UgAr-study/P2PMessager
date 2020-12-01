package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLTable {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    public static void Connect () throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:USERS.s3db");
        System.out.println("Connected");
    }

    public static void CreateDB() throws ClassNotFoundException, SQLException {
        statmt = conn.createStatement();
        statmt.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT,'name' text, 'ip' text, 'pub_key' text);");

        System.out.println("Table created or exists");
    }

    public static void WriteDB(String Name, String Ip, String PublicKey ) throws SQLException
    {
        statmt.execute("INSERT INTO 'users' ('name', 'ip', 'pub_key')" +
                " VALUES ('" + Name +"','" + Ip + "','" + PublicKey + "')");

        System.out.println("Table full filled");
    }

    public static void ReadDB(String Name) throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM 'users' WHERE name = '" + Name + "'");
        System.out.println("OOO");
        //resSet = statmt.executeQuery("SELECT * FROM 'users'");

        int id = resSet.getInt("id");
        String  name = resSet.getString("name");
        String  ip = resSet.getString("ip");
        String  pub_key = resSet.getString("pub_key");

        System.out.println( "ID = " + id );
        System.out.println( "name = " + name );
        System.out.println( "ip = " + ip );
        System.out.println( "public key = " + pub_key );
        System.out.println();

        System.out.println("Table has been read");
    }

    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Connections closed");
    }

    public static void DeleteDB() throws ClassNotFoundException, SQLException
    {
        statmt.execute("DROP TABLE 'users';");

        System.out.println("DB deleted");
    }
}

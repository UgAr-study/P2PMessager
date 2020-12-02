package com.company;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLTable {
    public Connection conn;
    public Statement statmt;
    public ResultSet resSet;
    private String tableName;

    public boolean Connect () {
        conn = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:USERS.s3db");
            return true;
        }catch (ClassNotFoundException | SQLException e) {
            System.out.println("Connection failed");
            return false;
        }
    }

    public boolean CreateDB(String TableName) {
        try {
            tableName = TableName;
            statmt = conn.createStatement();
            statmt.execute("CREATE TABLE if not exists '" + tableName + "' ('id' INTEGER PRIMARY KEY AUTOINCREMENT,'name' text, 'ip' text, 'pub_key' text);");
            return true;
        }catch (SQLException e) {
            System.out.println("Creating failed");
            return false;
        }
    }

    public boolean WriteDB(String Name, String Ip, String PublicKey)
    {
        try {
            statmt.execute("INSERT INTO '"+ tableName +"' ('name', 'ip', 'pub_key')" +
                    " VALUES ('" + Name + "','" + Ip + "','" + PublicKey + "')");
            return true;
        } catch (SQLException e) {
            System.out.println("Writing failed");
            return false;
        }
    }

    public String getPublicKeyByName (String Name) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE name = '" + Name + "'");
            return resSet.getString("pub_key");
        } catch (SQLException e) {
            System.out.println("getPubKeyByName failed");
            return "";
        }
    }

    public String getIpByName (String Name) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE name = '" + Name + "'");
            return resSet.getString("ip");
        } catch (SQLException e) {
            System.out.println("getIpByName failed");
            return "";
        }
    }

    public int getIdByName (String Name) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE name = '" + Name + "'");
            return Integer.parseInt(resSet.getString("id"));
        } catch (SQLException e) {
            System.out.println("getIdByName failed");
            return -1;
        }
    }

    public String getPublicKeyById (int Id) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE id = " + String.valueOf(Id));
            return resSet.getString("pub_key");
        } catch (SQLException e) {
            System.out.println("getPubKeyById failed");
            return "";
        }
    }

    public String getNameById (int Id) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE id = " + String.valueOf(Id));
            return resSet.getString("name");
        } catch (SQLException e) {
            System.out.println("getNameById failed");
            return "";
        }
    }

    public String getIpById (int Id) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE id = " + String.valueOf(Id));
            return resSet.getString("ip");
        } catch (SQLException e) {
            System.out.println("getIpById failed");
            return "";
        }
    }

    public String getNameByPublicKey(String publicKey) {
        try {
            resSet = statmt.executeQuery("SELECT * FROM '"+ tableName +"' WHERE pub_key = " + publicKey);
            return resSet.getString("name");
        } catch (SQLException e) {
            System.out.println("getNameByPublicKey failed");
            return "";
        }
    }

    public boolean DeleteByName (String Name) {
        try {
            statmt.execute("DELETE FROM '"+ tableName +"'WHERE name = '" + Name + "'");
            return true;
        }catch (SQLException e) {
            return false;
        }
    }

    public boolean CloseDB()
    {
        try {
            conn.close();
            statmt.close();
            resSet.close();
            return true;
        } catch (SQLException e) {
            System.out.println("Closing failed");
            return false;
        }

    }


    public boolean DropDB()
    {
        try {
            statmt.execute("DROP TABLE '" + tableName + "';");
            return true;
        }catch (SQLException e) {
            return false;
        }
    }
}

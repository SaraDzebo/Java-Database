package ba.unsa.etf.rpr;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance;
    private Connection conn;
    public static GeografijaDAO getInstance(){
        if(instance==null)
            instance=new GeografijaDAO();
        return instance;
    }
    private GeografijaDAO() {
        try {
            conn= DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    ArrayList<Grad> gradovi(){
        ArrayList<Grad> gradoviBaze=new ArrayList<>();
        return gradoviBaze;
    }
}

package ba.unsa.etf.rpr;

import java.sql.*;
import java.util.ArrayList;

public class GeografijaDAO {
    private static GeografijaDAO instance;
    private PreparedStatement upit;
    private Connection conn;
    public static GeografijaDAO getInstance(){
        if(instance==null)
            instance=new GeografijaDAO();
        return instance;
    }
    private GeografijaDAO() {
        ArrayList<Grad> gradovi = new ArrayList<>();
        ArrayList<Drzava> drzave = new ArrayList<>();

        try {
            conn= DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            upit=conn.prepareStatement("SELECT* FROM grad,drzava WHERE grad.drzava=drzava.id AND drzava.glavni_grad=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (var grad : gradovi) {
            try{
                upit.setInt(1, grad.getDrzava().getIdDrzava());
                upit.setInt(2, grad.getIdGrad());
                upit.executeUpdate();
            } catch (SQLException ignored) {
            }
        }
    }


    ArrayList<Grad> gradovi(){
        ArrayList<Grad> gradoviBaze=new ArrayList<>();
        ResultSet resultGradovi = null;
        try {
            resultGradovi = upit.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            upit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setIdGrad(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNazivGrad(nazivGrad);
                int brojStanovnika = resultGradovi.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = resultGradovi.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));
                gradoviBaze.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gradoviBaze;

    }
}

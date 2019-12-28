package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class GeografijaDAO {
    private static GeografijaDAO instance;
    private PreparedStatement glavniGradUpit,dajDrzavuUpit,obrisiDrzavuUpit,obrisiGradoveZaDrzave,nadjiDrzavuUpit,dajGradoveUpit,dodajGradoveUpit,
    odrediIdGradUpit,odrediIdDrzaveUpit,promijeniGradUpit,dajGradUpit,dodajDrzavuUpit,upit;
    private static Connection conn;



    public Connection getConn() {
        return conn;
    }

    public static void removeInstance() {
        try {
            if (conn != null)
                conn.close();
        } catch(SQLException ignore){}
        instance=null;
    }

   private static void initializa(){
        instance=new GeografijaDAO();
   }
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
            glavniGradUpit =conn.prepareStatement("SELECT* FROM grad,drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
        } catch (SQLException e) {
            regenerisiBazu();
            e.printStackTrace();

            try {
                glavniGradUpit = conn.prepareStatement("SELECT* FROM grad,drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try {
            dajDrzavuUpit=conn.prepareStatement("SELECT * FROM drzava WHERE id=?");
            dajGradUpit=conn.prepareStatement("SELECT * FROM grad WHERE id=?");
            obrisiGradoveZaDrzave=conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
            obrisiDrzavuUpit=conn.prepareStatement("DELETE FROM drzava WHERE id=?");
            nadjiDrzavuUpit=conn.prepareStatement("SELECT * FROM drzava WHERE naziv=?");
            dajGradoveUpit=conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");

            dodajGradoveUpit=conn.prepareStatement("INSERT INTO grad VALUES (?,?,?,?)");
            odrediIdGradUpit=conn.prepareStatement("SELECT MAX(id)+1 FROM grad");
            odrediIdDrzaveUpit=conn.prepareStatement("SELECT MAX(id)+1 FROM drzava");
            dodajDrzavuUpit=conn.prepareStatement("INSERT INTO drzava VALUES (?,?,?)");
            promijeniGradUpit=conn.prepareStatement("UPDATE grad SET naziv=?,broj_stanovnika=?,drzava=? WHERE id=?");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (var grad : gradovi) {
            try{
                glavniGradUpit.setInt(1, grad.getDrzava().getId());
                glavniGradUpit.setInt(2, grad.getId());
                glavniGradUpit.executeUpdate();
            } catch (SQLException ignored) {
            }
        }

    }

    private void regenerisiBazu(){
        try {
            Scanner ulaz=new Scanner(new FileInputStream("baza.db.sql"));
            String sqlUpit="";
            while(ulaz.hasNext()){
                sqlUpit+=ulaz.nextLine();
                if(sqlUpit.charAt(sqlUpit.length()-1)==';'){
                    try {
                        Statement stmt= null;
                        stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit="";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
           // e.printStackTrace();
        }

    }


    ArrayList<Grad> gradovi(){
        ArrayList<Grad> gradoviBaze=new ArrayList<>();
        ResultSet resultGradovi = null;
        try {
            resultGradovi = glavniGradUpit.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            glavniGradUpit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            while (resultGradovi.next()) {
                Grad grad = new Grad();
                int idGrad = resultGradovi.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = resultGradovi.getString(2);
                grad.setNaziv(nazivGrad);
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

    public void izmijeniGrad(Grad grad) {
        try {
            upit=conn.prepareStatement("UPDATE grad SET naziv=?,broj_stanovnika=?,drzava=? WHERE id=?");
            upit.setString(1,grad.getNaziv());
            upit.setInt(2,grad.getBrojStanovnika());
            upit.setInt(3,grad.getDrzava().getId());
            upit.setInt(4,grad.getId());

        } catch (SQLException izuzetak) {
            izuzetak.printStackTrace();
        }

    }
    public void izmijeniDrzava(Drzava drzava) {
        try {
            upit=conn.prepareStatement("UPDATE drzava SET naziv=?,glavni_grad= ? WHERE id=?");
            upit.setString(1,drzava.getNaziv());
            upit.setInt(2,drzava.getGlavniGrad().getId());
            upit.setInt(3,drzava.getId());

        } catch (SQLException izuzetak) {
            izuzetak.printStackTrace();
        }

    }

   /* public Grad glavniGrad(String drzava) {
    }

    public Drzava nadjiDrzavu(String naziv) {
        //getDrzavaBynaziv upit;
        Drzava d=null;
        try {
            getDrzavaBynaziv.clearParameters();
            getDrzavaynaziv.setString(1,drzava);
            ResultSet rs=getDrzavaByNaziv.executeCuery();

        }catch(SQLException e){
            e.printStackTrace();
        }
        return d;

    }*/

    public void dodajDrzavu(Drzava drzava) {
    }

    public void dodajGrad(Grad grad) {

    }

    public void obrisiDrzavu(String drzava) {
        try {
            upit=conn.prepareStatement("DELETE FROM drzava WHERE id=?");
            upit.setString(1,drzava);
            upit.executeUpdate();
        } catch (SQLException greska) {
            greska.printStackTrace();
        }
    }
    public void obrisiGrad(String grad) {
        try {
            upit=conn.prepareStatement("DELETE FROM grad WHERE id=?");
            upit.setString(1,grad);
            upit.executeUpdate();
        } catch (SQLException greska1) {
            greska1.printStackTrace();
        }
    }

}

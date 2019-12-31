package ba.unsa.etf.rpr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class GeografijaDAO {
    private static GeografijaDAO instance;
    private PreparedStatement glavniGradUpit,dajDrzavuUpit,obrisiDrzavuUpit,obrisiGradoveZaDrzave,nadjiDrzavuUpit,dajGradoveUpit,dodajGradoveUpit,
    odrediIdGradUpit,odrediIdDrzaveUpit,promijeniGradUpit,dajGradUpit,dodajDrzavuUpit;
    private static Connection conn;



    public Connection getConn() {
        return conn;
    }

    public static GeografijaDAO getInstance(){
        if(instance==null)
            initialize();
        return instance;
    }

    public static void removeInstance() {
        try {
            if (conn != null)
                conn.close();
        } catch(SQLException ignore){}
        instance=null;
    }

   private static void initialize(){
        instance=new GeografijaDAO();
   }


    private GeografijaDAO() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            glavniGradUpit = conn.prepareStatement("SELECT* FROM grad,drzava WHERE grad.drzava=drzava.id AND drzava.naziv=?");
        } catch (SQLException e) {
            regenerisiBazu();
            // e.printStackTrace();

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
            obrisiDrzavuUpit=conn.prepareStatement("DELETE FROM drzava WHERE naziv=?");
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
        ArrayList<Grad> rezultat=new ArrayList<>();
        try {
            ResultSet rs = glavniGradUpit.executeQuery();
            while (rs.next()) {
                Grad grad = dajGradIzResultSeta(rs);

               /* int idGrad = rs.getInt(1);
                grad.setId(idGrad);
                String nazivGrad = rs.getString(2);
                grad.setNaziv(nazivGrad);
                int brojStanovnika = rs.getInt(3);
                grad.setBrojStanovnika(brojStanovnika);
                int drzavaId = rs.getInt(4);
                grad.setDrzava(new Drzava(drzavaId, "", null));*/
                rezultat.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;

    }

    public void izmijeniGrad(Grad grad) {
        try {
            promijeniGradUpit.setString(1, grad.getNaziv());
            promijeniGradUpit.setInt(2,grad.getBrojStanovnika());
            promijeniGradUpit.setInt(3,grad.getDrzava().getId());
            promijeniGradUpit.setInt(4,grad.getId());
            promijeniGradUpit.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }


   public Grad glavniGrad(String drzava) {
       try {
           glavniGradUpit.setString(1,drzava);
           ResultSet rs=glavniGradUpit.executeQuery();
           if(!rs.next()){
               return null;
           }
                return dajGradIzResultSeta(rs);
       } catch (SQLException e) {
           e.printStackTrace();
           return null;
       }

    }

    private Grad dajGradIzResultSeta(ResultSet rs) throws SQLException {
        Grad grad=new Grad(rs.getInt(1),rs.getString(2),rs.getInt(3),null);
        grad.setDrzava(dajDrzavu(rs.getInt(4),grad));
        return grad;
    }

    private Drzava dajDrzavu(int id,Grad grad) {
        try {
            dajDrzavuUpit.setInt(1,id);
            ResultSet rs=dajDrzavuUpit.executeQuery();
            if(!rs.next()){
                return null;
            }
            return dajDrzavuIzResultSeta(rs,grad);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    private Grad dajGrad(int id) {
        try {
            dajGradUpit.setInt(1,id);
            ResultSet rs=dajGradUpit.executeQuery();
            if(!rs.next()){
                return null;
            }
            return dajGradIzResultSeta(rs);

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drzava dajDrzavuIzResultSeta(ResultSet rs,Grad grad) throws SQLException {
        return new Drzava(rs.getInt(1),rs.getString(2),grad);
    }

    public Drzava nadjiDrzavu(String naziv) {
        try {
            nadjiDrzavuUpit.setString(1,naziv);
            ResultSet rs=nadjiDrzavuUpit.executeQuery();
            if(!rs.next())
                return null;
            return dajDrzavuIzResultSeta(rs,dajGrad(rs.getInt(3)));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            ResultSet rs=odrediIdDrzaveUpit.executeQuery();
            int id=1;
            if(rs.next()){
                id=rs.getInt(1);

            }
            dodajDrzavuUpit.setInt(1,id);
            dodajDrzavuUpit.setString(2, drzava.getNaziv());
            //dodajGradoveUpit.setInt(3,grad.getBrojStanovnika());
            dodajDrzavuUpit.setInt(3,drzava.getGlavniGrad().getId());
            dodajDrzavuUpit.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void dodajGrad(Grad grad) {
    try {
        ResultSet rs=odrediIdGradUpit.executeQuery();
        int id=1;
        if(rs.next()){
            id=rs.getInt(1);

        }
        dodajGradoveUpit.setInt(1,id);
        dodajGradoveUpit.setString(2, grad.getNaziv());
        dodajGradoveUpit.setInt(3,grad.getBrojStanovnika());
        dodajGradoveUpit.setInt(4,grad.getDrzava().getId());
        dodajGradoveUpit.executeUpdate();
    }catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void obrisiDrzavu(String nazivDrzave) {
        try {
            nadjiDrzavuUpit.setString(1,nazivDrzave);
            ResultSet rs=nadjiDrzavuUpit.executeQuery();
            if(!rs.next())
                return;
            Drzava drzava=dajDrzavuIzResultSeta(rs,null);
            obrisiGradoveZaDrzave.setInt(1,drzava.getId());
            obrisiGradoveZaDrzave.executeUpdate();
            obrisiDrzavuUpit.setInt(1,drzava.getId());
            obrisiDrzavuUpit.executeUpdate();

        } catch (SQLException greska) {
            greska.printStackTrace();
        }
    }

    public void obrisiGrad(String grad) {
       /* try {
            upit=conn.prepareStatement("DELETE FROM grad WHERE id=?");
            upit.setString(1,grad);

        } catch (SQLException greska1) {
            greska1.printStackTrace();
        }*/
    }
}

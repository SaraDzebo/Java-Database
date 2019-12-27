package ba.unsa.etf.rpr;

public class Grad {
    private int id;
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.id = id;
        this.naziv = naziv;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
    }
    public Grad(){}

    public int getIdGrad() {
        return id;
    }

    public void setIdGrad(int id) {
        this.id = id;
    }

    public String getNazivGrada() {
        return naziv;
    }

    public void setNazivGrad(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public String getDrzava() {
        return naziv;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }
}

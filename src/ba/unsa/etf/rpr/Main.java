package ba.unsa.etf.rpr;


import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
       System.out.println("Gradovi su:\n" + ispisiGradove());
        glavniGrad();
    }

    private static void glavniGrad() {
        GeografijaDAO dao=GeografijaDAO.getInstance();
        Scanner sc=new Scanner(System.in);
        System.out.println("Unesite naziv drzave: ");
        String naziv=sc.nextLine();
        Grad grad=dao.glavniGrad(naziv);
        if(grad==null){
            System.out.println("Nepostojeća država");
        }
        else{
            System.out.println("Glavni grad države "+naziv+" je "+grad.getNaziv());
        }

    }


    public static String ispisiGradove() {
        GeografijaDAO dao=GeografijaDAO.getInstance();
        String s="";
        for(Grad grad:dao.gradovi()){
            s+=grad.getNaziv()+" ("+grad.getDrzava().getNaziv()+") - "+grad.getBrojStanovnika()+"\n";
        }
        return s;
    }
}

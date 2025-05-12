package it.univaq.disim.inf.wmservices.model;
import java.util.Date;

public class Proposta{
    private int ID;
    private Date data;
    private String revisione;
    private String note;
    private String rev_motivazione;
    private Richiesta ric;
    private Prodotto prod;
    private Utente tec;

    public Proposta(){
        this.ID = 0;
        this.data = new Date();
        this.revisione = "";
        this.note = "";
        this.rev_motivazione = "";
        this.ric = null;
        this.prod = null;
        this.tec = null;
    }
        public Proposta(int id,String r, String n, String rev, Richiesta ric, Prodotto prod, Utente tec){
        this.ID=id;
        this.data = new Date();
        this.revisione = r;
        this.note = n;
        this.rev_motivazione = rev;
        this.ric = ric;
        this.prod = prod;
        this.tec = tec;
    }
        
    public int getID() {
        return ID;
    }
    public void setID(int id) {
        this.ID = id;
    }
    public Date getData() {
        return data;
    }
    public void setData(Date data) {
        this.data = data;
    }
    public String getRevisione() {
        return revisione;
    }
    public void setRevisione(String revisione) {
        this.revisione = revisione;
    }
    public String getNote() {
        return note;
    }
    public void setNote(String note) {
        this.note = note;
    }
    public String getRev_motivazione() {
        return rev_motivazione;
    }
    public void setRev_motivazione(String rev_motivazione) {
        this.rev_motivazione = rev_motivazione;
    }
    
    public Richiesta getRic() {
        return ric;
    }
    public void setRic(Richiesta ric) {
        this.ric = ric;
    }
    public Prodotto getProd() {
        return prod;
    }
    public void setProd(Prodotto prod) {
        this.prod = prod;
        
    }
    public Utente getTec() {
        return tec;
    }
    public void setTec(Utente tec) {
        this.tec = tec;
    }

}

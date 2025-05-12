package it.univaq.disim.inf.wmservices.model;
import java.util.Date;

import java.util.HashMap;
import java.util.Map;

public class Richiesta{
    private int ID;
    private Date data;
    private String stato;
    private String note;
    private Utente ord;
    private Utente tec;
    private String IDcat;
    private Map<String, String> caratteristiche = new HashMap<>();
    private Proposta proposta;

    public Richiesta(){
        this.ID = 0;
        
        //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        this.data = new Date();
        this.stato = "";
        this.note = "";
        this.ord = null;
        this.IDcat = "";
        this.tec = null;
        this.caratteristiche = null;
        this.proposta = null;
    }
    
    public Richiesta(int id, String s, String n, String v, Utente ord, String IDcat, Utente tec, Map<String, String> car, Proposta p){
        this.ID = id;
        this.data = new Date();
        this.stato = s;
        this.note = n;
        this.ord = ord;
        this.IDcat = IDcat;
        this.tec = tec;
        this.caratteristiche = car;
        this.proposta = p;
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
    public String getStato() {
        return stato;
    }
    public String getNote() {
        return note;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }
    public void setNote(String note) {
        this.note = note;
    }
    
    public Utente getOrd() {
        return ord;
    }
    public void setOrd(Utente ord) {
        this.ord = ord;
    }
    public String getIDcat() {
        return IDcat;
    }
    public void setIDcat(String idCat) {
        this.IDcat = idCat;
    }
    public Utente getTec() {
        return tec;
    }
    public void setTec(Utente tec) {
        this.tec = tec;
    }
    public Map<String, String> getCaratteristiche() {
        return caratteristiche;
    }

    public void setCaratteristiche(Map<String, String> c) {
        this.caratteristiche = c;
    }
    public Proposta getProposta() {
        return proposta;
    }
    public void setProposta(Proposta p) {
        this.proposta = p;
    }
    
}

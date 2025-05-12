package it.univaq.disim.inf.wmservices.model;

public class Prodotto{
    private String codice;
    private String nome;
    private String nome_produttore;
    private double prezzo;
    private String url;

    public Prodotto() {
        this.codice="";
        this.nome = "";
        this.nome_produttore = "";
        this.prezzo = 0;
        this.url = "";
    }
     public Prodotto(String c,String n, String np, double p, String url) {
        this.codice = c;
        this.nome = n;
        this.nome_produttore = np;
        this.prezzo = p;
        this.url = url;
    }
    
    public String getCodice() {
        return codice;
    }
    public void setCodice(String codice) {
        this.codice = codice;
    } 
     
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome_produttore() {
        return nome_produttore;
    }
    public void setNome_produttore(String nome_produttore) {
        this.nome_produttore = nome_produttore;
    }
    public double getPrezzo() {
        return prezzo;
    }
    public void setPrezzo(double prezzo) {
        this.prezzo = prezzo;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}

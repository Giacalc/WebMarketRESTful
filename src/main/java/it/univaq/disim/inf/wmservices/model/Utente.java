package it.univaq.disim.inf.wmservices.model;

public class Utente{
    private String username;
    private String nome;
    private String cognome;
    private String password;
    private String email;
    private String ruolo;

    public Utente(){
        this.username = "";
        this.nome = "";
        this.cognome = "";
        this.password = "";
        this.email = "";
        this.ruolo = "";
    }
    public Utente(String u, String n, String c, String p, String m, String r) {
        this.username = u;
        this.nome = n;
        this.cognome = c;
        this.password = p;
        this.email = m;
        this.ruolo = r;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCognome() {
        return cognome;
    }
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getRuolo() {
        return ruolo;
    }
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}

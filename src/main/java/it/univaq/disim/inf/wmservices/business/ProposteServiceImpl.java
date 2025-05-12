package it.univaq.disim.inf.wmservices.business;

import it.univaq.disim.inf.wmservices.model.Prodotto;
import it.univaq.disim.inf.wmservices.model.Proposta;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import java.util.List;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ProposteServiceImpl implements ProposteService {

    private DataSource dataSource;
    
    public ProposteServiceImpl() {
        try {
            //inizializzazione contesto DB
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            dataSource = (DataSource) envContext.lookup("jdbc/wmservices");
        } catch (NamingException e) {
            throw new RuntimeException("Errore durante il lookup del DataSource", e);
        }
    }
    
    @Override
    public List<Proposta> getProposte(String user) throws SQLException{
        List<Proposta> proposte = new ArrayList<>();

        Connection conn = dataSource.getConnection();
        String query = "SELECT proposta.ID FROM proposta "
                + "INNER JOIN richiesta ON richiesta.ID=proposta.ID_richiesta "
                + "WHERE ID_ordinante=? OR proposta.ID_tecnico=? ";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, user);
        stmt.setString(2, user);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Proposta p = getProposta(rs.getInt("ID"));
            proposte.add(p);
        }

        conn.close();
        return proposte;
    }
    
    @Override
    public Proposta getProposta(int prop) throws SQLException {
        Connection conn = dataSource.getConnection();
        String query = "SELECT * "
                + "FROM proposta WHERE "
                + "ID=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, prop);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            Proposta p = new Proposta();
            p.setID(rs.getInt("ID"));
            p.setRevisione(rs.getString("revisione"));
            p.setData(rs.getDate("data"));
            p.setNote(rs.getString("note"));
            p.setRev_motivazione(rs.getString("rev_motivazione"));

            //ID esterni
            int ricID = rs.getInt("ID_richiesta");
            String tecID = rs.getString("ID_tecnico");
            String prodID = rs.getString("ID_prodotto");

            if(ricID != 0) {
                RichiesteService richiesteService = RichiesteServiceFactory.getRichiesteService();
                p.setRic(richiesteService.getRichiesta(ricID));
            }
            if(tecID != null) {
                Utente t = getUtente(tecID);
                p.setTec(t);
            }
            if(prodID != null) {
                Prodotto prod = getProdotto(prodID);
                p.setProd(prod);
            }

            conn.close();
            return p;
        }
        
        conn.close();
        return null;
    }
    //helper
    private Prodotto getProdotto(String prod) throws SQLException {
        Connection conn = dataSource.getConnection();
        String query = "SELECT * "
                + "FROM prodotto WHERE "
                + "codice=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, prod);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            Prodotto pr = new Prodotto();
            pr.setCodice(rs.getString("codice"));
            pr.setPrezzo(rs.getDouble("prezzo"));
            pr.setNome(rs.getString("nome"));
            pr.setNome_produttore(rs.getString("nome_produttore"));
            pr.setUrl(rs.getString("url"));

            conn.close();
            return pr;
        }

        conn.close();
        return null;
    }  
    //helper
    private Utente getUtente (String user) throws SQLException {
        Connection conn = dataSource.getConnection();
        String query = "SELECT * "
                + "FROM utente WHERE "
                + "username=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Utente u = new Utente();
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setRuolo(rs.getString("ruolo"));
            u.setNome(rs.getString("nome"));
            u.setCognome(rs.getString("cognome"));

            conn.close();
            return u;      
        }
        
        conn.close();
        return null;   
    }

    @Override
    public int accettaProposta(int p, String user) throws SQLException {
        Connection conn = dataSource.getConnection();
        
        //è lo stesso utente che ha ricevuto la proposta?
        String query = "SELECT * FROM proposta "
                + "INNER JOIN richiesta on richiesta.ID=proposta.ID_Richiesta "
                + "WHERE proposta.ID=? AND richiesta.ID_Ordinante=?";
        PreparedStatement stmtc = conn.prepareStatement(query);
        stmtc.setInt(1, p);
        stmtc.setString(2, user);
        ResultSet result = stmtc.executeQuery();
        if (result.next()){
            query = "UPDATE proposta SET "
                + "revisione=? "
                + "WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "Accettata");
            stmt.setInt(2, p);

            int accetta = stmt.executeUpdate();
            conn.close();
            return accetta;       
        } else{
            conn.close();
            return -1;
        }
    }

    @Override
    public int rifiutaProposta(int p, String user, String rif) throws SQLException {
        Connection conn = dataSource.getConnection();
        
        //è lo stesso utente che ha ricevuto la proposta?
        String query = "SELECT * FROM proposta "
                + "INNER JOIN richiesta on richiesta.ID=proposta.ID_Richiesta "
                + "WHERE proposta.ID=? AND richiesta.ID_Ordinante=?";
        PreparedStatement stmtc = conn.prepareStatement(query);
        stmtc.setInt(1, p);
        stmtc.setString(2, user);
        ResultSet result = stmtc.executeQuery();
        if (result.next()){
            query = "UPDATE proposta SET "
                + "revisione=?, rev_motivazione=? "
                + "WHERE ID=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "Rifiutata");
            stmt.setString(2, rif);
            stmt.setInt(3, p);

            int rifiuta = stmt.executeUpdate();
            conn.close();
            return rifiuta;       
        } else{
            conn.close();
            return -1;
        }
    }

    
    @Override
    public int insertProposta(Proposta p, String tec) throws SQLException {
        Connection conn = dataSource.getConnection();
        //è lo stesso tecnico a cui è in carico la richiesta?
        String query = "SELECT * FROM richiesta "
                + "WHERE ID=? AND richiesta.ID_tecnico=?";
        PreparedStatement stmtc = conn.prepareStatement(query);
        stmtc.setInt(1, p.getRic().getID());
        stmtc.setString(2, tec);
        ResultSet result = stmtc.executeQuery();
        if (result.next()){       
            //aggiornamento richiesta
            RichiesteService richiesteService = RichiesteServiceFactory.getRichiesteService();
            Richiesta r =richiesteService.getRichiesta(p.getRic().getID());
            if(r.getStato().equals("Presa in carico")) {
                //aggiornamento richiesta
                    query = "UPDATE richiesta SET"
                    + " stato=? WHERE ID=?";
                PreparedStatement pspr = conn.prepareStatement(query);
                pspr.setString(1, "In attesa");
                pspr.setInt(2, r.getID());
                pspr.executeUpdate();

                //prodotto
                query = "INSERT INTO prodotto(codice, nome_produttore, nome, prezzo, URL) "
                        + "VALUES(?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1,p.getProd().getCodice());
                ps.setString(2,p.getProd().getNome_produttore());
                ps.setString(3,p.getProd().getNome());
                ps.setDouble(4,p.getProd().getPrezzo());
                ps.setString(5,p.getProd().getUrl());

                int rowsInserted = ps.executeUpdate();
                if (rowsInserted == 1) {
                    //proposta
                    query = "INSERT INTO proposta (revisione,note,data,ID_richiesta,ID_tecnico,ID_prodotto)"
                            + " VALUES(?,?,?,?,?,?)";
                    PreparedStatement psp = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                    psp.setString(1, "In attesa");
                    psp.setString(2, p.getNote());
                    psp.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                    psp.setInt(4, p.getRic().getID());
                    psp.setString(5,p.getTec().getUsername());
                    psp.setString(6, p.getProd().getCodice());

                    rowsInserted = psp.executeUpdate();
                    if (rowsInserted == 1) {
                        ResultSet keys = psp.getGeneratedKeys();
                        keys.next();
                        int p_key = keys.getInt(1);
                        conn.close();
                        return p_key;
                    }
                }
            }
        } else {
            conn.close();
            return -1;
        }
        conn.close();
        return 0;
    }

    @Override
    public int updateProposta(Proposta p, int pID, String tec) throws SQLException {
        Connection conn = dataSource.getConnection();    
        //è lo stesso tecnico che ha mandato la proposta?
        String query = "SELECT * FROM proposta "
                + "WHERE ID=? AND ID_tecnico=?";
        PreparedStatement stmtc = conn.prepareStatement(query);
        stmtc.setInt(1, pID);
        stmtc.setString(2, tec);
        ResultSet result = stmtc.executeQuery();
        if (result.next()) {
        
            Proposta p_old = getProposta(pID);

            //aggiornamento prodotto
                query = "UPDATE prodotto SET"
                + " codice=?,nome_produttore=?,nome=?,prezzo=?,URL=? WHERE codice=?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1,p.getProd().getCodice());
                ps.setString(2,p.getProd().getNome_produttore());
                ps.setString(3,p.getProd().getNome());
                ps.setDouble(4,p.getProd().getPrezzo());
                ps.setString(5,p.getProd().getUrl());
                ps.setString(6,p_old.getProd().getCodice());

            int rowsInserted = ps.executeUpdate();
            if (rowsInserted == 1) {
                //aggiornamento proposta
                query = "UPDATE proposta SET "
                        + "revisione=?,note=?,data=?,ID_richiesta=?,ID_tecnico=?,ID_prodotto=?"
                        + " WHERE ID=?";
                PreparedStatement psp = conn.prepareStatement(query);
                psp.setString(1, p.getRevisione());
                psp.setString(2, p.getNote());
                psp.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                psp.setInt(4, p.getRic().getID());
                psp.setString(5,p.getTec().getUsername());
                psp.setString(6, p.getProd().getCodice());
                psp.setInt(7, pID);

                rowsInserted = psp.executeUpdate();
                conn.close();
                return rowsInserted;
            }
        } else {
            conn.close();
            return -1;
        }

        conn.close();
        return 0;
    }
    
}

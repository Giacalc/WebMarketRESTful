package it.univaq.disim.inf.wmservices.business;

import it.univaq.disim.inf.wmservices.model.Prodotto;
import it.univaq.disim.inf.wmservices.model.Proposta;
import java.util.List;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import it.univaq.disim.inf.wmservices.model.Utente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class RichiesteServiceImpl implements RichiesteService {

    private DataSource dataSource;
    
    public RichiesteServiceImpl() {
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
    public List<Richiesta> getRichieste(String user) throws SQLException {
        List<Richiesta> richieste = new ArrayList<>();

        Connection conn = dataSource.getConnection();
        String query = "SELECT ID "
                + "FROM richiesta WHERE "
                + "ID_ordinante=? OR ID_Tecnico=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, user);
        stmt.setString(2, user);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Richiesta r = getRichiesta(rs.getInt("ID"));
            richieste.add(r);
        }
      
        conn.close();
        return richieste;
    }
    
    @Override
    public Richiesta getRichiesta(int ric) throws SQLException {
        Connection conn = dataSource.getConnection();
        String query = "SELECT * "
                + "FROM richiesta WHERE "
                + "ID=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, ric);
        ResultSet rs = stmt.executeQuery();

        if(rs.next()) {
            Richiesta r = new Richiesta();
            r.setID(rs.getInt("ID"));
            r.setStato(rs.getString("stato"));
            r.setData(rs.getDate("data"));
            r.setNote(rs.getString("note"));
            r.setIDcat(rs.getString("ID_categoria"));

            //ID esterni
            String ordID = rs.getString("ID_ordinante");
            String tecID = rs.getString("ID_tecnico");

            if(ordID != null) {
                Utente o = getUtente(ordID);
                r.setOrd(o);
            }
            if(tecID != null) {
                Utente t = getUtente(tecID);
                r.setTec(t);
            }

            //helper caratteristiche
            Map<String,String> car = getCarRic(r.getID());
            r.setCaratteristiche(car);
            //helper proposta
            Proposta pr = getPropRic(ric);
            r.setProposta(pr);

            conn.close();
            return r;
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
    //helper
    private Map<String,String> getCarRic (int idRic) throws SQLException {
        Map<String,String> c = new HashMap<>();
        Connection conn = dataSource.getConnection();
            String query = "SELECT * "
                    + "FROM caratteristica_richiesta WHERE "
                    + "ID_richiesta=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idRic);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                c.put(rs.getString("nome_caratteristica"), rs.getString("valore"));    
            }
        conn.close();
        return c;      
    }
    //helper
    private Proposta getPropRic (int idRic) throws SQLException {
        Connection conn = dataSource.getConnection();
            String query = "SELECT * FROM proposta WHERE ID_richiesta=? " + 
                    "ORDER BY ID DESC " +
                    "LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idRic);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Proposta p = new Proposta();
                p.setID(rs.getInt("ID"));
                p.setRevisione(rs.getString("revisione"));
                p.setData(rs.getDate("data"));
                p.setNote(rs.getString("note"));
                p.setRev_motivazione(rs.getString("rev_motivazione"));
                
                String prodID = rs.getString("ID_prodotto");
                
                if(prodID != null) {
                    Prodotto prod = getProdottoPropRic(prodID);
                    p.setProd(prod);
                }

                conn.close();
                return p;
            }
 
        conn.close();
        return null;      
    }
    //helper
    private Prodotto getProdottoPropRic(String prod) throws SQLException {
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
    
    @Override
    public List<Richiesta> getRichiesteInCorso(String ordID) throws SQLException {
        List<Richiesta> richieste = new ArrayList<>();

        Connection conn = dataSource.getConnection();
        String query = "SELECT ID "
                + "FROM richiesta "
                + "WHERE stato != 'Completata' AND ID_ordinante=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, ordID);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Richiesta r = getRichiesta(rs.getInt("ID"));
            richieste.add(r);
        }
        conn.close();

        return richieste;
    }
    
    @Override
    public List<Richiesta> getRichiesteDaPrendereInCarico() throws SQLException {
        List<Richiesta> richieste = new ArrayList<>();

        Connection conn = dataSource.getConnection();
        String query = "SELECT ID "
                + "FROM richiesta "
                + "WHERE stato = 'Da prendere in carico'";
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Richiesta r = getRichiesta(rs.getInt("ID"));
            richieste.add(r);
        }
        conn.close();
        return richieste;
    }
    
    @Override
    public List<Richiesta> getRichiesteInCarico(String idTec) throws SQLException {
        List<Richiesta> richieste = new ArrayList<>();

        Connection conn = dataSource.getConnection();
        String query = "SELECT ID "
                + "FROM richiesta "
                + "WHERE ID_tecnico=? AND stato != 'Completata'";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, idTec);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Richiesta r = getRichiesta(rs.getInt("ID"));
            richieste.add(r);
        }
        conn.close();
        return richieste;
    }

    @Override
    public int inCaricoRichiesta(String tecID, Richiesta r) throws SQLException {
                
        Connection conn = dataSource.getConnection(); 
        String query = "UPDATE richiesta SET "
                + "ID_tecnico=?, stato=?"
                + "WHERE ID=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, tecID);
        stmt.setString(2, "Presa in carico");
        stmt.setInt(3, r.getID());

        int update = stmt.executeUpdate();
        conn.close();
        return update;
    }
    
    @Override
    public int insertRichiesta(Richiesta ric) throws SQLException{
        Connection conn = dataSource.getConnection();

        String query = "INSERT INTO richiesta (data,note,stato,ID_ordinante,ID_categoria)"
                + " VALUES(?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        ps.setDate(1, new java.sql.Date(System.currentTimeMillis()));
        ps.setString(2, ric.getNote());
        if (ric.getStato() == null || ric.getStato().equals("") ) {
            ps.setString(3,"Da prendere in carico");
        } else {
            ps.setString(3, ric.getStato());
        }
        
        ps.setString(4,ric.getOrd().getUsername());
        ps.setString(5, ric.getIDcat());

        int rowsInserted = ps.executeUpdate();
        if (rowsInserted == 1) {
            ResultSet keys = ps.getGeneratedKeys();
            keys.next();
            int IDric = keys.getInt(1);

            Map<String, String> caratteristiche = ric.getCaratteristiche();
            if (caratteristiche != null) {
                //query per ogni caratteristica
                query = "SELECT nome FROM caratteristica WHERE nome = ?";
                PreparedStatement psCar_chiave = conn.prepareStatement(query);

                String query_iCar = "INSERT INTO caratteristica_richiesta (ID_richiesta, nome_caratteristica, valore) VALUES(?, ?, ?)";
                PreparedStatement psCar_insert = conn.prepareStatement(query_iCar);

                for (Map.Entry<String, String> entry : ric.getCaratteristiche().entrySet()) 
                {
                    String chiave = entry.getKey();
                    String valore = entry.getValue();

                    psCar_chiave.setString(1, chiave);
                    ResultSet rsC = psCar_chiave.executeQuery();

                    if (rsC.next()) {
                        String nomeCar = rsC.getString("nome");

                        psCar_insert.setInt(1, IDric);
                        psCar_insert.setString(2, nomeCar);
                        psCar_insert.setString(3,valore);
                        psCar_insert.addBatch();  
                    }
                    rsC.close();
                }

                psCar_insert.executeBatch();
                psCar_chiave.close();
                psCar_insert.close();
            }
            
            conn.close();
            return IDric;
        }
        conn.close();
        return 0;
    }   
            
    @Override
    public int deleteRichiesta(int rID) throws SQLException {   
        int delete = 0; 
        Connection conn = dataSource.getConnection();
        String query = "DELETE FROM richiesta "
                + "WHERE ID=?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, rID);

        delete = stmt.executeUpdate();
        conn.close();
        return delete;
    }
}

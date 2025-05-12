package it.univaq.disim.inf.wmservices.business;

import java.util.List;
import it.univaq.disim.inf.wmservices.model.Richiesta;
import java.sql.SQLException;

public interface RichiesteService {
    
    List<Richiesta> getRichieste(String user) throws SQLException;
    
    Richiesta getRichiesta(int r) throws SQLException;
    
    List<Richiesta> getRichiesteInCorso(String ordID) throws SQLException;
    
    List<Richiesta> getRichiesteDaPrendereInCarico() throws SQLException;
    
    List<Richiesta> getRichiesteInCarico(String tecID) throws SQLException;
    
    int inCaricoRichiesta(String tecID, Richiesta r) throws SQLException;
    
    int insertRichiesta(Richiesta ric) throws SQLException;
    
    int deleteRichiesta(int ricID) throws SQLException;

}

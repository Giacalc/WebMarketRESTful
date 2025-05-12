package it.univaq.disim.inf.wmservices.business;

import it.univaq.disim.inf.wmservices.model.Proposta;
import java.sql.SQLException;
import java.util.List;

public interface ProposteService {
    
    List<Proposta> getProposte(String user) throws SQLException;
    
    Proposta getProposta(int p) throws SQLException;

    int accettaProposta(int p, String user) throws SQLException;
    
    int rifiutaProposta(int p, String user, String rif) throws SQLException;
    
    int insertProposta(Proposta p, String tec) throws SQLException;
    
    int updateProposta(Proposta p, int pID, String tec) throws SQLException;
    
}

package it.univaq.disim.inf.wmservices.business;

public class ProposteServiceFactory {
    private final static ProposteServiceImpl service = new ProposteServiceImpl();

    public static ProposteServiceImpl getProposteService() {
        return service;
    }
}

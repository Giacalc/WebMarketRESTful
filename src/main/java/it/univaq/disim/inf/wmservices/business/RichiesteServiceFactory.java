package it.univaq.disim.inf.wmservices.business;

public class RichiesteServiceFactory {
    private final static RichiesteServiceImpl service = new RichiesteServiceImpl();

    public static RichiesteServiceImpl getRichiesteService() {
        return service;
    }
}

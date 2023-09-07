package org.plexus.inventoryservice.excepciones;

public class ResourceNotFoundException extends RuntimeException{

    private static final String DESCRIPTION = "Not Found Exception (404)";
    private static final long serialVersionUID = 1L;

    private String skuCode;
    private int quantity;

    public ResourceNotFoundException(Long  id, int quantity) {
        super(String.format("%s no encontrado con : %s : '%s'",id,quantity));
        this.skuCode = skuCode;
        this.quantity = quantity;
    }

    public ResourceNotFoundException(String detail) {
        super(DESCRIPTION + ". " + detail);
    }
}

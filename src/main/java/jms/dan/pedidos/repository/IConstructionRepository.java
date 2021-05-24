package jms.dan.pedidos.repository;

import jms.dan.pedidos.dto.ClientDTO;

public interface IConstructionRepository {
    ClientDTO getClientAssociated(Integer idConstruction);
    Integer getClientIdAssociated(Integer idConstruction);
}

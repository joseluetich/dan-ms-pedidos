package jms.dan.pedidos.repository;

import jms.dan.pedidos.dto.ClientDTO;
import jms.dan.pedidos.dto.ConstructionDTO;

public interface IConstructionRepository {
    ClientDTO getClientAssociated(Integer idConstruction);
    Integer getClientIdAssociated(Integer idConstruction);
    ConstructionDTO getConstructionById(Integer idConstruction);
}

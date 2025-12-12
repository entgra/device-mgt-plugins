package io.entgra.device.mgt.plugins.emqx.initializer.dao;

import io.entgra.device.mgt.core.device.mgt.common.exceptions.DBConnectionException;
import io.entgra.device.mgt.plugins.emqx.exhook.dto.EmqxConnection;

import java.sql.SQLException;

public interface EmqxConnectionDAO {

    boolean isEmqxConnectionExists(String clientId) throws DBConnectionException;

    EmqxConnection getEmqxConnectionDetailsByClientId(int clientId) throws DBConnectionException, SQLException;

    void saveEmqxConnectionDetails(EmqxConnection emqxConnection) throws DBConnectionException, SQLException;

    void deleteEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException;
}

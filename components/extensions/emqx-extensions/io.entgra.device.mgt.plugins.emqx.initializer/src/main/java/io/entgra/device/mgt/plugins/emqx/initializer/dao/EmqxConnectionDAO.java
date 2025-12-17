package io.entgra.device.mgt.plugins.emqx.initializer.dao;

import io.entgra.device.mgt.core.device.mgt.common.exceptions.DBConnectionException;
import io.entgra.device.mgt.plugins.emqx.exhook.dto.EmqxConnection;

import java.sql.SQLException;

public interface EmqxConnectionDAO {

    /**
     * Checks whether an EMQX connection record exists for the given client ID.
     *
     * @param clientId the MQTT client identifier
     * @return {@code true} if a connection record exists, {@code false} otherwise
     * @throws DBConnectionException if a database connection error occurs
     */
    boolean isEmqxConnectionExists(String clientId) throws DBConnectionException;

    /**
     * Retrieves EMQX connection details associated with the given client ID.
     *
     * @param clientId the MQTT client identifier
     * @return the {@link EmqxConnection} details, or {@code null} if not found
     * @throws DBConnectionException if a database connection error occurs
     * @throws SQLException if an SQL execution error occurs
     */
    EmqxConnection getEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException;

    /**
     * Persists EMQX connection details in the database.
     *
     * <p>If a record already exists for the given client ID, the behavior
     * depends on the implementation (e.g., overwrite or reject).</p>
     *
     * @param emqxConnection the EMQX connection details to be stored
     * @throws DBConnectionException if a database connection error occurs
     * @throws SQLException if an SQL execution error occurs
     */
    void saveEmqxConnectionDetails(EmqxConnection emqxConnection) throws DBConnectionException, SQLException;

    /**
     * Deletes EMQX connection details associated with the given client ID.
     *
     * @param clientId the MQTT client identifier
     * @throws DBConnectionException if a database connection error occurs
     * @throws SQLException if an SQL execution error occurs
     */
    void deleteEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException;
}

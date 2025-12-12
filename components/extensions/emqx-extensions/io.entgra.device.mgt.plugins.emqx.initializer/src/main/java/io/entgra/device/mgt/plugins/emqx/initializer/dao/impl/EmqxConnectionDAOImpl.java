package io.entgra.device.mgt.plugins.emqx.initializer.dao.impl;

import io.entgra.device.mgt.core.device.mgt.common.exceptions.DBConnectionException;
import io.entgra.device.mgt.plugins.emqx.exhook.dto.EmqxConnection;
import io.entgra.device.mgt.plugins.emqx.initializer.dao.EmqxConnectionDAO;
import io.entgra.device.mgt.plugins.emqx.initializer.dao.EmqxConnectionDAOFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmqxConnectionDAOImpl implements EmqxConnectionDAO {
    private static final Log log = LogFactory.getLog(EmqxConnectionDAOImpl.class);

    @Override
    public void saveEmqxConnectionDetails(EmqxConnection emqxConnection) throws DBConnectionException, SQLException {
        String sql = "INSERT INTO EMQX_CONNECTION (DEVICE_ID, CLIENT_ID, ACCESS_TOKEN, SCOPES, TENANT_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = EmqxConnectionDAOFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, emqxConnection.getDeviceId());
            ps.setString(2, emqxConnection.getClientId());
            ps.setString(3, emqxConnection.getAccessToken());
            ps.setString(4, emqxConnection.getScopes());
            ps.setInt(5, emqxConnection.getTenantId());
//            ps.setTimestamp(6, emqxConnection.getCreatedTime());
            ps.executeUpdate();
        }
    }

    @Override
    public boolean isEmqxConnectionExists(String clientId) throws DBConnectionException {
        final String sql = "SELECT 1 FROM EMQX_CONNECTION WHERE CLIENT_ID = ? LIMIT 1";
        try (
                Connection conn = EmqxConnectionDAOFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DBConnectionException(
                    "Error checking existence of EMQX_CONNECTION for clientId: " + clientId, e);
        }
    }

    @Override
    public EmqxConnection getEmqxConnectionDetailsByClientId(int clientId)
            throws DBConnectionException {
        final String sql = "SELECT ID, DEVICE_ID, CLIENT_ID, ACCESS_TOKEN, SCOPES, TENANT_ID FROM EMQX_CONNECTION WHERE CLIENT_ID = ?";
        try (
                Connection conn = EmqxConnectionDAOFactory.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ){
            ps.setInt(1, clientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DBConnectionException("Failed to fetch EMQX connection for clientId: " + clientId, e);
        }
    }

    @Override
    public void deleteEmqxConnectionDetailsByClientId(String clientId) throws DBConnectionException, SQLException {
        String sql = "DELETE FROM EMQX_CONNECTION WHERE CLIENT_ID = ?";
        try (Connection conn = EmqxConnectionDAOFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientId);
            ps.executeUpdate();
        }
    }

    private EmqxConnection mapRow(ResultSet rs) throws SQLException {
        EmqxConnection dto = new EmqxConnection();
        dto.setId(rs.getInt("ID"));
        dto.setDeviceId(rs.getInt("DEVICE_ID"));
        dto.setClientId(rs.getString("CLIENT_ID"));
        dto.setAccessToken(rs.getString("ACCESS_TOKEN"));
        dto.setScopes(rs.getString("SCOPES"));
        dto.setTenantId(rs.getInt("TENANT_ID"));
//        dto.setCreatedTime(rs.getTimestamp("CREATED_TIME"));
        return dto;
    }


}




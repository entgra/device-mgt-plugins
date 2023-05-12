/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.entgra.device.mgt.plugins.iot.agent.virtual;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import io.entgra.device.mgt.plugins.iot.agent.virtual.ui.AgentUI;

/**
 *
 * @author charitha
 */
public class VirtualAgentUI {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AgentUI().setVisible(true);
            }
        });
    }

}

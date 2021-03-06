/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fedex.smartpost.mts.forms;

import javax.swing.JOptionPane;

import com.fedex.smartpost.mts.services.WindowsRegistryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

/**
 *
 * @author 796812
 */
public class CredentialUI extends javax.swing.JFrame {
	private static final Log logger = LogFactory.getLog(CredentialUI.class);
	private  MainUI parentUI;

	/**
	 * Creates new form CredentialUI
	 */
	public CredentialUI() {
		initComponents();
		setLocation(WindowsRegistryService.getWindowLocation());
	}

	public CredentialUI(MainUI parentUI) {
		this.parentUI = parentUI;
		initComponents();
		try {
			txtOracleUser.setText(WindowsRegistryService.getOracleUserFromRegistry());
			txtTeraUser.setText(WindowsRegistryService.getTeraDataUserFromRegistry());
			txtPassword.setText(WindowsRegistryService.getPasswordFromRegistry());
		}
		catch (Exception e) {
			logger.info("No previous values...Let the user set them.");
		}
		setLocation(WindowsRegistryService.getWindowLocation());
	}

	private boolean fieldsFilled() {
		if (StringUtils.isEmpty(txtOracleUser.getText())) {
			logger.info("Oracle username is empty.");
			return false;
		}
		if (StringUtils.isEmpty(txtTeraUser.getText())) {
			logger.info("TeraData username is empty.");
			return false;
		}
		if (StringUtils.isEmpty(new String(txtPassword.getPassword()))) {
			logger.info("Password is empty.");
			return false;
		}
		logger.info("All fields are filled in");
		return true;
	}

	private void setCredentials() {
		WindowsRegistryService.setRegistryValues(txtOracleUser.getText(), txtTeraUser.getText(), txtPassword.getPassword());
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        lblOracleUser = new javax.swing.JLabel();
        lblTeraUser = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        txtOracleUser = new javax.swing.JTextField();
        txtTeraUser = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnSave = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database Credentials");
        setName("credentialUI"); // NOI18N
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 24)); // NOI18N
        lblTitle.setText("Database Credentials");

        lblOracleUser.setText("Oracle Username:");

        lblTeraUser.setText("TeraData Username:");

        lblPassword.setText("Password:");

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTeraUser)
                            .addComponent(lblOracleUser)
                            .addComponent(lblPassword))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTeraUser)
                            .addComponent(txtOracleUser, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPassword)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSave)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOracleUser)
                    .addComponent(txtOracleUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTeraUser)
                    .addComponent(txtTeraUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSave)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
		if (!fieldsFilled()) {
			JOptionPane.showMessageDialog(this, "Please fill in your database credentials.", "Need to complete form", JOptionPane.ERROR_MESSAGE);
		}
		else {
			setCredentials();
			JOptionPane.showMessageDialog(this, "Database credentials saved.");
			logger.info("Database credentials saved to Windows Registry.");
			this.setVisible(false);
			parentUI.setVisible(true);
		}
    }//GEN-LAST:event_btnSaveActionPerformed

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
		WindowsRegistryService.setWindowLocation(getLocation());
    }//GEN-LAST:event_formComponentMoved
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel lblOracleUser;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblTeraUser;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTextField txtOracleUser;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtTeraUser;
    // End of variables declaration//GEN-END:variables
}

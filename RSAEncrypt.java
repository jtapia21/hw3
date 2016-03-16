package project3;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class RSAEncrypt {

	private JFrame frmRsaEncryption;
	private JTextField txtPrivateKey;
	private JTextField txtPublicKey;
	private JTextField txtNValue;

	private messageBlocker msg;
	
	private xmlParseAndRead files;
	
	private primeNumHandler prime;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					RSAEncrypt window = new RSAEncrypt();
					window.frmRsaEncryption.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public RSAEncrypt() {
		msg = new messageBlocker();
		files = new xmlParseAndRead();
		prime = new primeNumHandler();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmRsaEncryption = new JFrame();
		frmRsaEncryption.setTitle("RSA Encryption / Decryption");
		frmRsaEncryption.setBounds(100, 100, 460, 400);
		frmRsaEncryption.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmRsaEncryption.setResizable(false);
		
		JTextArea txtrTest = new JTextArea();
		txtrTest.setText("What would you like to do?");
		txtrTest.setBounds(10, 175, 431, 165);
		frmRsaEncryption.getContentPane().add(txtrTest);
		
		JMenuBar menuBar = new JMenuBar();
		frmRsaEncryption.setJMenuBar(menuBar);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		JMenuItem mntmCreateKey = new JMenuItem("Create Key");
		mnOptions.add(mntmCreateKey);
		mntmCreateKey.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent a){
				if(a.getSource().equals(mntmCreateKey)){
					prime.getKeys();
				}
			}
		});
		
		JMenuItem mntmBlockAFile = new JMenuItem("Block a File");
		mntmBlockAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String s;
				int a;
				while(true) {
					try {
						s = (String)JOptionPane.showInputDialog(null, "Please provide a chunk block size for the file.", "RSA - Saving Public Key", JOptionPane.PLAIN_MESSAGE, null, null, "");
						a = Integer.parseInt(s);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Please provide an integer value.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
						continue;
					}
					break;
				}
				
				if(!msg.block(a)) {
					JOptionPane.showMessageDialog(null, "The Blocking procedure has failed. Please check that the file is not corrupt.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		mnOptions.add(mntmBlockAFile);
		
		JMenuItem mntmUnblockAFile = new JMenuItem("Unblock a File");
		mntmUnblockAFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!msg.unblock()) {
					JOptionPane.showMessageDialog(null, "The unblocking procedure has failed. Please check that the file is not corrupt.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		mnOptions.add(mntmUnblockAFile);
		
		JMenuItem mntmEncryptDecrypt = new JMenuItem("Encrypt / Decrypt");
		mnOptions.add(mntmEncryptDecrypt);
		mntmEncryptDecrypt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent b){
					if(msg.unblock()){
						JOptionPane.showMessageDialog(null, "The unblocking procedure has failed. Please check that the file is not corrupt.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
					}
			}
		});
		
		JMenu mnXml = new JMenu("XML");
		menuBar.add(mnXml);
		
		JMenuItem mntmImportKeys = new JMenuItem("Import Keys");
		mntmImportKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(files.readFile()) {
					txtrTest.setText("Files have been successfully read.\nYou can now begin encrypting / decrypting.");
					txtPrivateKey.setText(files.priKey());
					txtPublicKey.setText(files.pubKey());
					txtNValue.setText(files.extKey());
				} else {
					txtrTest.setText("Your files failed to work.\n\nYou may be missing values or mismatched the files.");
				}
			}
		});
		mnXml.add(mntmImportKeys);
		
		JMenuItem mntmSaveKeys = new JMenuItem("Save Keys");
		mntmSaveKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(files.writeXML()) {
					txtrTest.setText("Files have been successfully Saved!");
				} else {
					txtrTest.setText("Error: you do not have any keys to save, or you have already\nsaved the keys are you using.");
				}
			}
		});
		mnXml.add(mntmSaveKeys);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "RSA Encryption and Decryption - By Jesse Tapia and Lukasz Przybos.\n\n"+
													"Lukasz and Jesse both collaborated in the March of 2016 on this project.\n"+
													"They both studied Computer Science at the University of Illinois at Chicago, and collaborated on this assignment in their CS342 class.", "RSA - About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmAbout);
		
		JMenuItem mntmInfo = new JMenuItem("Info");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "RSA Encryption and Decryption - By Jesse Tapia and Lukasz Przybos.\n\n"+
						"Users will need to first create a public and private key by selecting under the \"Options\" menu the \"Create a Key\" option, or fetch a saved XML file of their previously used public and private key.\n"+
						"To properly encrypt, the user should first take their .txt file message and have it encoded as a BLK file, by selecting \"Options\", and choosing \"Block a File\".\n"+
						"Then, in the same menu, select \"Encrypt / Decrypt\" to encrypt or decrypt your messages.", "RSA - Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		mnHelp.add(mntmInfo);
		frmRsaEncryption.getContentPane().setLayout(null);
		
		txtPrivateKey = new JTextField();
		txtPrivateKey.setEnabled(false);
		txtPrivateKey.setBounds(10, 32, 431, 20);
		frmRsaEncryption.getContentPane().add(txtPrivateKey);
		txtPrivateKey.setColumns(10);
		
		txtPublicKey = new JTextField();
		txtPublicKey.setEnabled(false);
		txtPublicKey.setBounds(10, 88, 431, 20);
		frmRsaEncryption.getContentPane().add(txtPublicKey);
		txtPublicKey.setColumns(10);
		
		txtNValue = new JTextField();
		txtNValue.setEnabled(false);
		txtNValue.setBounds(10, 144, 431, 20);
		frmRsaEncryption.getContentPane().add(txtNValue);
		txtNValue.setColumns(10);
		
		JLabel lblPrivateKey = new JLabel("Private Value");
		lblPrivateKey.setBounds(10, 11, 120, 14);
		frmRsaEncryption.getContentPane().add(lblPrivateKey);
		
		JLabel lblPublicKey = new JLabel("Public Value");
		lblPublicKey.setBounds(10, 63, 120, 14);
		frmRsaEncryption.getContentPane().add(lblPublicKey);
		
		JLabel lblNValue = new JLabel("N Value");
		lblNValue.setBounds(10, 119, 120, 14);
		frmRsaEncryption.getContentPane().add(lblNValue);
		
		
	}
}

package project3;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/*
 * XML parse and reader
 * Written by: Lukasz Przybos
 * CS342, HW3
 * 
 * Goal: To allows the user to grab xml private and public key files,
 * which then can be used to implement encryption and decryption. 
 * User then is allowed to store their keys if they chose to make
 * new ones in the prime generating class instead.
 */

public class xmlParseAndRead {
	private String publickey;
	private String privatekey;
	private String extrakey;
	private boolean newKeys = false;	// Check if the keys are new, or they are taken from an XML file.
	
	private JFileChooser chooser;
	private FileNameExtensionFilter filt;
	
	public xmlParseAndRead() {
		publickey = privatekey = extrakey = null;
		filt = new FileNameExtensionFilter("XML files - Select your TWO key files.", "xml");
		chooser = new JFileChooser("./");
		chooser.setFileFilter(filt);
		chooser.setMultiSelectionEnabled(true);
	}
	
	// Adds manual keys if the user creates their own prime number
	// keys. Checks to see if the keys were successfully inserted.
	public boolean addKeys(String evalue, String dvalue, String nvalue) {
		this.privatekey = dvalue;
		this.publickey = evalue;
		this.extrakey = nvalue;
		
		this.newKeys = isReady();
		if(!this.newKeys) {
		//System.out.println("Incorrect keys were given. System is not ready to encrypt / decrypt.");
		}
		return this.newKeys;
	}
	
	// Read files to grab 
	public boolean readFile() {
		File[] files = null;
		File xmlFile = null;
		int returnVal = 0;
		
		try {
			do {
				returnVal = chooser.showOpenDialog(null);
				files = chooser.getSelectedFiles();
				
				for(int i = 0, l = files.length; i < l; ++i) {
					System.out.println("You chose to file in index " + i + ": " + files[i].getName());
				}
				
				if(returnVal == JFileChooser.CANCEL_OPTION) {
					return false;
				} else if(files.length != 2) {
					returnVal = JFileChooser.CANCEL_OPTION;
					JOptionPane.showMessageDialog(null, "Error: Select exactly TWO xml files that contain your private and public key.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				}
			} while(returnVal != JFileChooser.APPROVE_OPTION);
			
			for(int i = 0, l = files.length; i < l; ++i) {
				xmlFile = files[i];
				if(!xmlFile.exists()) {
					xmlFile.createNewFile();
					JOptionPane.showMessageDialog(null, "Error: File doesn't exist. Abort.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
				
				DocumentBuilderFactory dbFac = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbBuild = dbFac.newDocumentBuilder();
				Document doc = dbBuild.parse(xmlFile);
				
				doc.getDocumentElement().normalize();
				
				NodeList rsa = doc.getElementsByTagName("rsakey");
				
				Node item = rsa.item(0);
				System.out.println("Root Node is: " + item.getNodeName());
				if(item.getNodeType() == Node.ELEMENT_NODE) {
					Element el = (Element) item;
					
					if(el.getElementsByTagName("dvalue").item(0) != null) {
						this.privatekey = el.getElementsByTagName("dvalue").item(0).getTextContent();
						System.out.println("Key value for private key: " + this.privatekey);
					}
					
					if(el.getElementsByTagName("evalue").item(0) != null) {
						this.publickey = el.getElementsByTagName("evalue").item(0).getTextContent();
						System.out.println("Key value for public key: " + this.publickey);
					}
					
					if(el.getElementsByTagName("nvalue").item(0) != null) {
						String eKey = el.getElementsByTagName("nvalue").item(0).getTextContent();
						
						if(this.extrakey == null) {
							this.extrakey = eKey;
						} else if (!this.extrakey.equals(eKey)) {
							JOptionPane.showMessageDialog(null, "Error: The provided files do not have correct n value keys. XML parsing aborted.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
							this.extrakey = null;
							return false;
						}
					} else {
						JOptionPane.showMessageDialog(null, "Error: One of the provided files is missing an n value key. XML parsing aborted.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
						return false;
					}
				}
			}
			return this.isReady();
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Error: Something went wrong reading xml file. Aborted.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
	}
	
	public void writeXML() {
		try {
			File a, b;
			String s;
			
			do {
				s = (String)JOptionPane.showInputDialog(null, "Please provide a name for the PUBLIC key file.", "RSA - Saving Public Key", JOptionPane.PLAIN_MESSAGE, null, null, "");
				a = new File(s+".xml");
				if(a.exists()) {
					JOptionPane.showMessageDialog(null, "Error: File already exists. Select a new name", "RSA - File already exists", JOptionPane.INFORMATION_MESSAGE);
				}
				
			} while(a.exists());
			
			do {
				s = (String)JOptionPane.showInputDialog(null, "Please provide a name for the PRIVATE key file.", "Saving Private Key", JOptionPane.PLAIN_MESSAGE, null, null, "");
				b = new File(s+".xml");
				if(b.exists()) {
					JOptionPane.showMessageDialog(null, "Error: File already exists. Select a new name", "RSA - File already exists", JOptionPane.INFORMATION_MESSAGE);
				}
			} while(b.exists());
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("rsakey");
			doc.appendChild(rootElement);

			Element prkey = doc.createElement("dvalue");
			prkey.appendChild(doc.createTextNode(this.privatekey));
			rootElement.appendChild(prkey);
			
			Element eKey = doc.createElement("nvalue");
			eKey.appendChild(doc.createTextNode(this.extrakey));
			rootElement.appendChild(eKey);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(b);

			transformer.transform(source, result);
			
			docFactory = DocumentBuilderFactory.newInstance();
			docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.newDocument();
			rootElement = doc.createElement("rsakey");
			doc.appendChild(rootElement);

			prkey = doc.createElement("evalue");
			prkey.appendChild(doc.createTextNode(this.publickey));
			rootElement.appendChild(prkey);
			
			eKey = doc.createElement("nvalue");
			eKey.appendChild(doc.createTextNode(this.extrakey));
			rootElement.appendChild(eKey);
			
			transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();
			source = new DOMSource(doc);
			result = new StreamResult(a);

			transformer.transform(source, result);
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error: Something went wrong writing the XML file. Aborted.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		return;
	}
	
	public boolean isReady() {
		return (this.extrakey != null && this.privatekey != null && this.publickey != null);
	}
	
	public String pubKey() {
		return this.publickey;
	}
	
	public String priKey() {
		return this.privatekey;
	}

	public String extKey() {
		return this.extrakey;
	}
	
	public boolean isNew() {
		return this.newKeys;
	}
}

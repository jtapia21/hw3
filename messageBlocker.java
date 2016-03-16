package project3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * Message Blocking and Unblocking class
 * Written by: Lukasz Przybos
 * CS342, HW3
 * 
 * Goal: to take messages from a file and break them down into numerical chunks based
 * on value of 100 ASCII words, instead of the traditional 128. This means that we 
 * need to ensure that our table indexing is matching correctly our table.
 * 
 * TABLE:
 * 
 * 		0	1	2	3	4	5	6	7	8	9
 *    |--------------------------------------
 * 	0 |\0  \v  \t  \n  \r  SP   !   "   #   $
 *  1 | %	&	'	(	)	*	+	,	-	.
 *  2 |	/	0	1	2	3	4	5	6	7	8
 *  3 |	9	:	;	<	=	>	?	@	A	B
 *  4 |	C	D	E	F	G	H	I	J	K	L
 *  5 |	M	N	O	P	Q	R	S	T	U	V
 *  6 |	W	X	Y	Z	[	\	]	^	_	`
 *  7 |	a	b	c	d	e	f	g	h	i	j
 *  8 |	k	l	m	n	o	p	q	r	s	t
 *  9 |	u	v	w	x	y	z	{	|	}	~
 *    |--------------------------------------
 */
public class messageBlocker {
	private File a, b;
	private int blockSize; 	// Hold onto block size so we know how to unblock and block...
	
	private JFileChooser chooser;
	private FileNameExtensionFilter filt;
	
	private BufferedReader br;
	private PrintWriter write;
	
	public messageBlocker() {
		a = b = null;
		blockSize = 0;
		
		chooser = new JFileChooser("./");
		chooser.setMultiSelectionEnabled(false);
	}
	
	// Set up our chunks of code to be turned into blocks
	// Also adds 00 characters if our code chunk block sizes
	// are not clean division, or as % chunkSize == 0.
	public boolean block(int size) {
		blockSize = size * 2;
		
		filt = new FileNameExtensionFilter("TXT file - Select the file to be blocked.", "txt");
		chooser.setFileFilter(filt);
		
		int returnVal;
		
		do {
			returnVal = chooser.showOpenDialog(null);
			a = chooser.getSelectedFile();
			
			if(returnVal == JFileChooser.CANCEL_OPTION) {
				return false;
			} else if(!a.exists()) {
				JOptionPane.showMessageDialog(null, "Error: File doesn't exist.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				returnVal = JFileChooser.CANCEL_OPTION;
			}
		} while(returnVal != JFileChooser.APPROVE_OPTION);
		
		b = new File("./"+a.getName().replaceFirst(".txt", "")+" - blocked.blk");
		
		try {
			
			if(b.createNewFile()) {
//				System.out.println("New file made..");
			}
		
			br = new BufferedReader(new FileReader(a));
			write = new PrintWriter(new FileOutputStream("./"+a.getName().replaceFirst(".txt", "")+" - blocked.blk", false));
			
			String newline = "";
			
			int line = br.read();
			
			while(line != -1) {
				
				line = adjustBlock(line);
				
				if(line < 10) {
					newline = "0" + line + newline;
				} else {
					newline = line + newline;
				}
				
				line = br.read();
			}
			
			int length = newline.length();
//			System.out.println("Length of new line is: " + length);
			while(length >= blockSize) {
				write.println(newline.substring((length - blockSize), length));
				length -= blockSize;
			}

			if(length != 0) {
				String lastline = newline.substring(0, length);
//				System.out.println("Length of last line is: " + lastline.length());
				for(; length != blockSize; length += 2) {
					write.print("00");
				}
				write.print(lastline);
			}
			
			write.close();
			br.close();
		} catch (Exception e) {
			return false;
		}
		
		JOptionPane.showMessageDialog(null, "The File has been successfully encoded as a BLK file.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
		
		return true;
	}
	
	// Grab a 'blocked' file (extension .blk) and make it unblock
	// and readable ASCII again. Program will determine on its own
	// how big the block chunks on the file are.
	public boolean unblock() {
		filt = new FileNameExtensionFilter("BLK file - Select the file to be unblocked.", "blk");
		chooser.setFileFilter(filt);
		
		int returnVal;
		
		do {
			returnVal = chooser.showOpenDialog(null);
			a = chooser.getSelectedFile();
			
			if(returnVal == JFileChooser.CANCEL_OPTION) {
				return false;
			} else if(!a.exists()) {
				JOptionPane.showMessageDialog(null, "Error: File doesn't exist.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				returnVal = JFileChooser.CANCEL_OPTION;
			}
		} while(returnVal != JFileChooser.APPROVE_OPTION);
		
		b = new File("./"+a.getName().replaceFirst(".blk", "")+" - unblocked.txt");
		
		try {
			
			if(b.createNewFile()) {
//				System.out.println("New file made..");
			}
		
			br = new BufferedReader(new FileReader(a));
			write = new PrintWriter(new FileOutputStream("./"+a.getName().replaceFirst(".blk", "")+" - unblocked.txt", false));
			
			String line = br.readLine();
			
			if(line == null || ((blockSize = line.length()) == 0)) {
				br.close();
				write.close();
				JOptionPane.showMessageDialog(null, "Error: This file is empty.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
//			System.out.println("size I got was " + blockSize);
			String newString = "", word = "";
			do {
				for(int i = blockSize; i > 0; i -= 2) {
					word = line.substring(i - 2, i);
					char letter = this.adjustUnblock((char)Integer.parseInt(word));
					// Get rid of null terminators. They screw everything up.
					if((int)letter != 0) { 
						newString += letter;
					}
//					System.out.println("Word so far is " + newString);
				}
				
				
			} while((line = br.readLine()) != null);
			write.print(newString);
			
			write.close();
			br.close();
		} catch (Exception e) {
			return false;
		}
		
		JOptionPane.showMessageDialog(null, "The File has been successfully decoded as a TXT file.", "RSA - Error!", JOptionPane.INFORMATION_MESSAGE);
		
		return true;
	}
	
	// Convert the character to fit our 100 ASCII character range.
	private char adjustBlock(int word) {
		if(word > 31 && word < 127) {
			word -= 27;
		} else {
			switch(word) {
			case 0:
				break;
			case 9:
			case 10:
				word -= 7;
				break;
			case 11:
				word = 1;
				break;
			case 13:
				word = 4;
				break;
			default:
				word = 36;
				break;
			}
		}
		return (char)word;
	}
	
	// Convert the character to fit the standard 128 ASCII character range.
	private char adjustUnblock(int word) {
		if(word > 4 && word < 101) {
			word += 27;
		} else {
			switch(word) {
			case 0:
				break;
			case 1:
				word = 11;
				break;
			case 2:
			case 3:
				word += 7;
				break;
			case 4:
				word = 13;
				break;
			default:
				word = 36;
				break;
			}
		}
		return (char)word;
	}
	
}

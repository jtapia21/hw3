package project3;

import java.util.Arrays;
import java.util.Random;
import javax.swing.JOptionPane;

/*
 * prime number check and get public and private keys
 * Written by: Lukasz Przybos
 * CS342, HW3
 * 
 * our goal is to make the prime numbers and check if they are prime
 *  then make the calculations to aquire the public and private keys
 */


public class primeNumHandler {
	xmlParseAndRead files;
	
	public primeNumHandler() {
		files = new xmlParseAndRead();
	}
	
	public int[] findPrimes() {
		int p;
		String userInput1 = JOptionPane.showInputDialog("Enter your prime number");
		
		p = Integer.parseInt(userInput1); //reads user input
		int pVal = checkPrime(p);
		int len = Integer.toString(pVal).length(); // makes the length of user input into the array
		int[] pArray = new int[len]; 
		
		//puts every digit in each position in the array
		for (int i = 0; i < len; i++){			
			pArray[i] = pVal % 10;
			pVal /= 10;
		}//End of for loop
		return pArray;
	}

	public static int checkPrime(int prime) {
		Boolean isPrime = true; //checker
		
		//check if the number the user inputs is prime
		for(int i = 3; i < prime; i+=2) {
			if(prime % i == 0 ){
				isPrime = false;
			}//End of if statement
		}//End of for loop.
		
		//if user number is not prime ask again 
		if(isPrime == false) {
			System.out.println(prime + " Is not prime");
			JOptionPane.showMessageDialog(null, prime + " is not prime: ");
		}//End of if statement
		//else shows that number is prime 
		else if(isPrime == true){
			System.out.println(prime + " Is prime");
			JOptionPane.showMessageDialog(null, prime + " is prime: ");
		}//End of else statement
		
		return prime;
		
	}
	
	public int[] getPrime(){
		int p = 0;
		//------------------------------------------------------------------------------------------
		int pVal = getRanNum(p);
		JOptionPane.showMessageDialog(null, "Your prime number: " +  pVal);
		int len = Integer.toString(pVal).length(); // makes the length of user input into the array
		int[] pArray = new int[len];
		//puts every digit in each position in the array
		for (int i = 0; i < len; i++){			
			pArray[i] = pVal % 10;
			pVal /= 10;
		}//End of for loop
		
		return pArray;
	}

	private long gcd(long e1, long m1) {
		while (e1 != m1){
			if(e1 > m1){
				e1 = e1 - m1;
			}
			else {
				m1 = m1 - e1;
			}
		}
		return e1;
	}
	
	private long gcd2(long e1, long m1) {
		long x = 0, y = 1, lX = 1, lY = 0, temp = 0;
		while (m1 != 0) {
			long q = e1 / m1;
			long r = e1 % m1;
			e1 = m1;
			m1 = r;
			temp = x;
			x = lX - q * x;
			lX = temp;
			temp = y;
			y = lY - q * y;
			lY = temp;
		}
		return temp;
	}
	
	private int getRanNum(int prime) {
		boolean isPrime = false; //checker
		
		//the loop will continue will it finds a prime number
		Random r = new Random();
		while(!isPrime) {
			prime = r.nextInt(500000000) * 2 + 99999999; //generates a large number
			Boolean fDivisor = false;
			System.out.println("trying :" + prime);
			int aThird = prime/3 + 1; // to calculate a third of the prime
			
			//checks if the number is prime 
			for (int current = 3; current < aThird; current += 2) {
				if(prime%current  == 0){
					fDivisor  = true;
				}//End of if statement 
			}//End of for loop
			if(!fDivisor){ //Can't find divisor so the number is prime 
				isPrime = true;
			}//End of if statement
		}//End of while loop
		System.out.println("This is prime: " + prime);
		return prime;
	}
	private  int[] subOne(int[]Array) {
		int[] oneArr = {1,0,0,0,0,0,0,0,0};
		int[] newArr = new int[Math.min(Array.length, oneArr.length)];
		
		for(int i = 0; i < newArr.length; i++){
			newArr[i] = Array[i] - oneArr[i];
		}
		for (int i = 0; i < newArr.length / 2; i++) {
			  int temp = newArr[i];
			  newArr[i] = newArr[newArr.length - 1 - i];
			  newArr[newArr.length - 1 - i] = temp;
			}
		return newArr;
		
	}
	private long[] lMulti(int[] pArray, int[] qArray) {
		long[] value = new long[Math.max(pArray.length, qArray.length)];
		for (int i = 0; i < value.length; i++){
			value[i] = pArray[i] * qArray[i];
		}
		for (int i = 0; i < value.length / 2; i++) {
			  long temp = value[i];
			  value[i] = value[value.length - 1 - i];
			  value[value.length - 1 - i] = temp;
			}
		return value;
	}
	
	public long[] getpqVal(int[] pArray, int[] qArray) {
		
		//subtracting L = (p-1) * (q - 1)
		int[] L1 = subOne(pArray);
		
		int[] L2 = subOne(qArray);
		
		long[] L = lMulti(L1, L2);
		JOptionPane.showMessageDialog(null, "L is: " + Arrays.toString(L).replace("[", "")
				.replace(",", "").replace("]", "").replace(" ", ""));
		
		return L;
	}
	public String[] getNVal(int[] pArray, int[] qArray){
		long[] n = lMulti(pArray,qArray);
		JOptionPane.showMessageDialog(null, "N is: " + Arrays.toString(n).replace("[", "")
				.replace(",", "").replace("]", "").replace(" ", ""));
		String[] n2 = new String[n.length];
		for(int j = 0; j < n.length; j++){
			n2[j] = String.valueOf(n[j]);
		}
		return n2;
	}
	public String[] getEVal(){
		long e1 = 269401212;
		long m1 = 819745602;
		long e = gcd(e1, m1);
		JOptionPane.showMessageDialog(null, "e: " +  e);
		int len3 = Long.toString(e).length();
		long[] e2 = new long[len3];
		for (int k = 0; k < len3; k++){			
			e2[k] = e % 10;
			e /= 10;
		}//End of for loop
		for (int i = 0; i < e2.length / 2; i++) {
			  long temp = e2[i];
			  e2[i] = e2[e2.length - 1 - i];
			  e2[e2.length - 1 - i] = temp;
			}
		String[] e3 = new String[e2.length];
		for(int j = 0; j < e2.length; j++){
			e3[j] = String.valueOf(e2[j]);
		}
		return e3;
	}
	public String[] getDVal() {
		long e1 = 269401212;
		long m1 = 819745602;
		long d = gcd2(e1, m1);
		JOptionPane.showMessageDialog(null, "d: " +  d);
		int len4 = Long.toString(d).length();
		long[] d2 = new long[len4];
		for (int w = 0; w < len4; w++){			
			d2[w] = d % 10;
			d /= 10;
		}//End of for loop
		for (int i = 0; i < d2.length / 2; i++) {
			  long temp = d2[i];
			  d2[i] = d2[d2.length - 1 - i];
			  d2[d2.length - 1 - i] = temp;
			}
		String[] d3 = new String[d2.length];
		for(int j = 0; j < d2.length; j++){
			d3[j] = String.valueOf(d2[j]);
		}
		return d3;
	}
	public void getKeys(){
		//===========================================================================================
		int[] pArray = getPrime();
		int[]qArray = getPrime();
		//get n value
		String[] n = getNVal(pArray,qArray);
		String nvalue = Arrays.toString(n);
		//===========================================================================================
		//multiplying the two primes p*q
		//send both arrays p & q
		getpqVal(pArray, qArray);
		//--------------------------------------------------------------------------------------------
		//find e  
		String[] e = getEVal();
		String evalue = Arrays.toString(e);
		//--------------------------------------------------------------------------------------------
		//Find d
		String[] d = getDVal();
		String dvalue =Arrays.toString(d);
		
		files.addKeys(evalue, dvalue, nvalue);
	}
}
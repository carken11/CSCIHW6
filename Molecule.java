// COURSE: CSCI1620
// TERM: Spring 2019
//
// NAME: Cameron Gilinsky and Carter Kennell
// RESOURCES: Piazza discussion board posts by the
//				students and instructors for this class.

package molecule;

import java.util.Stack;

import molecule.exceptions.InvalidAtomException;
import molecule.exceptions.InvalidSequenceException;

//public class Molecule<E extends java.lang.Comparable<E>> implements Cloneable
/**
 * Objects of the Molecule class represent a single chemical molecule made
 * up of any number of hydrogen, carbon, and oxygen atoms. The class
 * provides functionality to compute the atomic weight of the molecule.
 * @author ckgilinsky and ckennell
 */
public class Molecule implements Comparable<Molecule>, Cloneable
{
	/**
	 * Molecular weight of hydrogen.
	 */
	private static final int HYDROGEN = 1;
	
	/**
	 * Molecular weight of carbon.
	 */
	private static final int CARBON = 12;
	
	/**
	 * Molecular weight of oxygen.
	 */
	private static final int OXYGEN = 16;
	
	/**
	 * Stack to hold weight values and parentheses.
	 */
	@SuppressWarnings("rawtypes")
	private Stack stack = new Stack<Integer>();
	
	/**
	 * The molecule input sequence surrounded by parentheses.
	 */
	private String sequence;
	
	/**
	 * Molecule sequence without the parentheses.
	 */
	private String trimSequence;
	
	/**
	 * The weight of the molecule.
	 */
	private int weight;
	
	/**
	 * Stack to be used for parenthesis checking.
	 */
	@SuppressWarnings("rawtypes")
	private Stack parenthesis = new Stack<Character>();
	
	/**
	 * Creates a new Molecule made up of the H, C, and O atoms in the configuration
	 * specified by sequenceIn.
	 * @param sequenceIn The sequence of atoms for this Molecule.
	 * @throws InvalidAtomException if any non C, H, O atom exists in sequenceIn
	 * @throws InvalidSequenceException if unmatched parentheses exist in sequenceIn or
	 * 		   incoming sequence is null or empty String.
	 */
	public Molecule(String sequenceIn)
	{
		try
		{
			trimSequence = sequenceIn.toUpperCase();
			setSequence(sequenceIn);
		}
		catch (InvalidAtomException iae)
		{
			throw new InvalidAtomException('c');
		}
		catch (InvalidSequenceException ise)
		{
			throw new InvalidSequenceException();
		}
	}
	
	/**
	 * Updates the sequence of atoms represented by this Molecule.
	 * @param sequenceIn The new molecular sequence to be used for this Molecule.
	 * @throws InvalidAtomException if any non C, H, O atom exists in sequenceIn
	 * @throws InvalidSequenceException if unmatched parentheses exist in sequenceIn
	 */
	@SuppressWarnings("unchecked")
	public void setSequence(String sequenceIn)
	{
		if (isValid(sequenceIn))
		{
			sequence = String.format("(" + sequenceIn + ")");
			String number = "";
			int result = 0;
			int num = 0;
			
			for (int i = 0; i < sequence.length(); i++)
			{
				char currentChar = sequence.charAt(i);
				char nextChar;
				
				if (i == sequence.length() - 1)
				{
					nextChar = 'X';
				}
				else
				{
					nextChar = sequence.charAt(i + 1);
				}
				
				if (Character.isDigit(currentChar))
				{
					number += Character.toString(currentChar);
					
					if (Character.isDigit(nextChar))
					{
						continue;
					}
					else
					{
						num = Integer.parseInt(number);
						number = "";
						num *= (int) stack.pop();
						stack.push(num);
						num = 0;
						
					}
				}	
				else if (Character.isLetter(currentChar))
				{
					stack.push(atomWeight(currentChar));
				}
				else if (currentChar == '(')
				{
					stack.push(-1);
				}
				else if (currentChar == ')')
				{
					//System.out.println(result);
				
					result = 0;
					
					while ((int) stack.peek() >= 0)
					{
						System.out.println((int) stack.peek());
						//System.out.println(Arrays.toString(stack.toArray()));
						result += (int) stack.pop();
						System.out.println((int) stack.peek());
					}				
					stack.push(result);
				}
				else
				{
					throw new InvalidAtomException(currentChar);
				}
			}
			
			while (!stack.empty())
			{
				if ((int) stack.peek() >= 0)
				{
					weight += (int) stack.pop();
				}
				else
				{
					stack.pop();
				}	
			}
		}
		else
		{
			throw new InvalidSequenceException();
		}
	}
	
	/**
	 * Retrieves a String containing this Molecule's sequence of atoms.
	 * @return Molecular sequence of the Molecule.
	 */
	public String getSequence()
	{
		return trimSequence;
	}
	
	/**
	 * Retrieves this Molecule's weight, which is calculated based on the Molecule's
	 * sequence per the algorithm specified.
	 * @return Weight of the Molecule.
	 */
	public int getWeight()
	{
		return weight;
	}
	
	/**
	 * Generates and returns a String with the Molecule's sequence and weight.
	 * The format of the String is
	 * 
	 * [SEQUENCE               ]: WEIGHT
	 * 
	 * Where SEQUENCE has a field width of 25 and is left justified (square brackets
	 * are just placeholders and will not appear in actual return values.
	 * WEIGHT should be left-justified. There is no space following the SEQUENCE field and the
	 * colon.
	 */
	@Override
	public String toString()
	{
		return String.format("%-25s: %d", trimSequence, weight);
	}
	
	/**
	 * Static utility method to return the atomic weight of a given atom.
	 * Supported atoms are Carbon (C), Hydrogen (H), and Oxygen (O), and
	 * the value of the atom parameter corresponds to the single letter
	 * abbreviation for these atoms (case insensitive). Atomic weights
	 * are given in their nearest whole number:
	 * 
	 * Hydrogen - 1
	 * Carbon - 12
	 * Oxygen - 16
	 * 
	 * @param atom Character for atom abbreviation
	 * @return Atomic weight of passed atom
	 * @throws InvalidAtomException Thrown if an unsupported atom is passed
	 */
	public static int atomWeight(char atom) throws InvalidAtomException
	{
		int ret = 0;
		
		if (atom == 'h' || atom == 'H')
		{
			ret = HYDROGEN;
		}
		else if (atom == 'c' || atom == 'C')
		{
			ret = CARBON;
		}
		else if (atom == 'o' || atom == 'O')
		{
			ret = OXYGEN;
		}
		else
		{
			throw new InvalidAtomException(atom);
		}
		
		return ret;
	}
	
	
	/**
	 * Compares this Molecule to a passed Molecule, determining natural order. Molecules with the same
	 * weight (regardless of sequence) are considered equal. All others are ordered
	 * relative to the magnitude of their weights.
	 * @param other Incoming Molecule to be compared with this (local) Molecule.
	 * @return Returns an int less than 0 if the local Molecule is less than the
	 * 		   passed Molecule, an int greater than 0 if the local Molecule is
	 * 		   greater than the passed Molecule, and a 0 if the Molecules are equal.
	 */
	@Override
	public int compareTo(Molecule other)
	{
		return ((Integer) getWeight()).compareTo((Integer) other.getWeight());
	}
	
	/**
	 * Returns a deep copy of the Molecule. The reference returned should refer to a
	 * completely separate molecule with no direct or indirect aliasing of any
	 * instance data in the originalMolecule.
	 * 
	 * NOTE: This method should NOT throw a CloneNotSupportedException.
	 * 
	 * @return Deep copy of the calling Molecule.
	 */
	@Override
	public Object clone()
	{
		//super.clone();
		Molecule temp = new Molecule(getSequence());
		return temp;
	}

	/**
	 * Helper method to check if sequence is valid.
	 * @param moleculeIn The molecule to be checked.
	 * @return Whether the sequence is valid.
	 */
	@SuppressWarnings("unchecked")
	public boolean isValid(String moleculeIn)
	{	
		for (int i = 0; i < moleculeIn.length(); i++)
		{
			if (moleculeIn.charAt(i) == '(')
			{
				parenthesis.push(i);
			}
			else if (moleculeIn.charAt(i) == ')')
			{
				if (parenthesis.empty())
				{
					return false;
				}
				else
				{
					parenthesis.pop();
				}
			}
		}
		
		return parenthesis.empty();
	}
}
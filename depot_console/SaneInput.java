package depot_console;

import java.time.LocalDateTime;
import java.util.Scanner;

import depot_system.Tanker.LiquidType;
/**
 * Utility class for reading from console while handling exceptions.
 * @author cmpsnowa
 *
 */
class SaneInput {
	final Scanner console = new Scanner(System.in);
	
	public String readString(){
		String x = "";
		while(x.equals("")){
			x = console.nextLine();
		}
		return x;
	}
	
	public int readUnsignedInteger(){
		String x = "";
		int y = 0;
		while(x.equals("")){
			x = console.nextLine();
			try {
				y = Integer.parseUnsignedInt(x);
			} catch (NumberFormatException e) {
				System.out.println("EXPECTED NON-NEGATIVE NATURAL NUMBER");
				x = "";
			}
		}
		
		return y;
	}
	
	public LiquidType readLiquidType(){
		String x = "";
		LiquidType y = LiquidType.UNDEFINED;
		while(x.equals("")){
			x = console.nextLine().toUpperCase();
			try {
				y = LiquidType.valueOf(x);
			} catch (IllegalArgumentException e) {
				System.out.println("EXPECTED ANY OF FOLLOWING VALUES");
				System.out.println(java.util.Arrays.asList(LiquidType.values()));
				x = "";
			}
		}
		
		return y;
	}
	
	public LocalDateTime readDate(){
		String x = "";
		LocalDateTime y = null;
		while(x.equals("")){
			x = console.nextLine();
			try {
				y = java.time.LocalDateTime.parse(x);
			} catch (Exception e) {
				System.out.println("EXPECTED DATE");
				x = "";
			}
		}
		
		return y;
	}
}

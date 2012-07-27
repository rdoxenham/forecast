
public class UnitPrices 
{
	public static double UP_TO_8 = 1.0;
	public static double UP_TO_16 = 2.0;
	public static double GREATER = 3.0;
	
	public double getUnits(int vCPU)
	{
		if(vCPU <= 8) return 1.0;
		if(vCPU <= 16) return 2.0;
		else return 3.0;
	}
}

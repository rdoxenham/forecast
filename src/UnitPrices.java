
public class UnitPrices 
{
	public static double UP_TO_8 = 1.0;
	public static double UP_TO_16 = 2.0;
	public static double GREATER = 3.0;
	
	public double getUnits(int vCPU, int vMem)
	{
		//if(vCPU <= 8) return UP_TO_8;
		//if(vCPU <= 16) return UP_TO_16;
		//else return GREATER;

		// Temporary time-based only unit count
		return 1.0;
	}
}

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class VM 
{
	String vmName;
	String osType;
	String hypervisor;
	String vmid;
	String fcid;
	int vCPUs;
	int vMem;
	double unitCount;
	
	public VM(String name, String id, String hyp, String type, int cpus, int mem)
	{
		this.vmName = name;
		this.vmid = id;
		this.hypervisor = hyp;
		this.osType = type;
		this.vCPUs = cpus;
		this.vMem = mem;
		this.unitCount = 0;
		this.fcid = getHash();
	}
	
	public String getHypervisor()
	{
		return hypervisor;
	}
	
	public String getHash()
	{
		String hashword = null;
		String concat = vmid + vmName;
		
		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(concat.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			hashword = hash.toString(16);
		}
		
		catch (NoSuchAlgorithmException nsae) 
		{
			// Ignore for now
		}
		
		return hashword;
	}
	
	public String getVMName()
	{
		return vmName;
	}
	
	public String getVMID()
	{
		return vmid;
	}
	
	public int getCPU()
	{
		return vCPUs;
	}

	public int getMem()
	{
		return vMem;
	}
	
	public String getOSType()
	{
		return osType;
	}
	
	public void setCount(double units)
	{
		this.unitCount = units;
	}
	
	public double getCount()
	{
		return unitCount;
	}
}


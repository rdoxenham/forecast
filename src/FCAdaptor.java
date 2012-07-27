import java.util.ArrayList;
import java.util.Date;

public abstract class FCAdaptor 
{
	String connectionURL, connectionUser, connectionPass;
	int unitCount = 0;
	Date lastCapture;
	
	ArrayList<VM> oldVMList = new ArrayList<VM>();
	ArrayList<VM> newVMList = new ArrayList<VM>();
	ArrayList<VM> tempVMList = new ArrayList<VM>();
	
	public FCAdaptor(String url, String username, String password)
	{
		this.connectionURL = url;
		this.connectionUser = username;
		this.connectionPass = password;
	}
	
	public int execute()
	{
		// Empty stub to allow inheritance
		return 0;
	}
	
	public ArrayList<VM> getLatest()
	{
		tempVMList.clear();
		
		if(oldVMList.size() > 0)
		{
			for(VM thisVM: newVMList)
			{
				for(VM olderVM: oldVMList)
				{
					if(thisVM.getVMName().equals(olderVM.getVMName()))
					{
						tempVMList.add(thisVM);
					}
				}
			}
			
			oldVMList = newVMList;
			newVMList.clear();
		}
		
		else
		{
			oldVMList = newVMList;
		}
		
		return tempVMList;
	}
	
	public Date getLastUpdate()
	{
		return lastCapture;
	}
}
import java.util.ArrayList;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;  
import java.sql.Connection;  
import java.sql.ResultSet;
import java.sql.Statement;

public class FCDatabase 
{
	String dbType;
	String dbPath;
	String dbUser;
	String dbPass;
	Connection db;
	Statement sql;
	DatabaseMetaData metaDB;
	boolean dbTested = false;
	
	public FCDatabase(String type, String path, String user, String pass)
	{
		this.dbType = type;
		this.dbPath = path;
		this.dbUser = user;
		this.dbPass = pass;
		this.dbTested = quickTest();
	}
	
	public boolean quickTest()
	{
		if(dbType.equals("postgres"))
		{
			try 
			{  
			    Class.forName("org.postgresql.Driver");
			}
			
			catch(ClassNotFoundException cnfe) 
			{  
			    System.out.println("Couldn't find the " + dbType + " database driver!");
			    return false;
			}
			
			try
			{
				db = DriverManager.getConnection("jdbc:postgresql:" + dbPath, dbUser, dbPass);
				sql = db.createStatement();
				String sql_query = "select count(*) from pg_catalog.pg_database where datname = '" + dbPath + "';";
				ResultSet results = sql.executeQuery(sql_query);
				if(results == null) return false;
				
				// If there is no table, it will cause an exception! :-) </hack>
				sql_query = "select * from vm_stats;";
				results = sql.executeQuery(sql_query);
				return true;
			}
			
			catch(Exception err)
			{
				return false;
			}
		}
		
		else if(dbType.equals("mysql"))
		{
			try 
			{  
			    Class.forName("org.mysql.Driver");  
			} 
			
			catch (ClassNotFoundException cnfe) 
			{  
			    System.out.println("Couldn't find the " + dbType + " database driver!");
			    return false;
			}  
		}
		
		return true;
	}
	
	public ArrayList<FCAdaptor> getAdaptors()
	{
		// TODO add support for mysql here :-/
		ArrayList <FCAdaptor> adaptors = new ArrayList<FCAdaptor>();
		
		try
		{
			db = DriverManager.getConnection("jdbc:postgresql:" + dbPath, dbUser, dbPass);
			sql = db.createStatement();
			ResultSet results = sql.executeQuery("SELECT * FROM adaptors");
			
			if(results != null)
			{
				while(results.next())
				{
					String adaptorURL = results.getString(2);
					String adaptorType = results.getString(3);
					String adaptorUser = results.getString(4);
					String adaptorPass = results.getString(5);
					
					FCAdaptor tempAdaptor;
					
					if (adaptorType == "RHEV")
					{
						tempAdaptor = new RHEVAdaptor(adaptorURL, adaptorUser, adaptorPass);
					}
					
					else
					{
						tempAdaptor = new RHEVAdaptor(adaptorURL, adaptorUser, adaptorPass);
					}
		
					adaptors.add(tempAdaptor);
				}
			
				results.close();
			}
		}
	
		catch(Exception err)
		{
			// Do nothing - will be handled in Forecast.class
		}
		
		return adaptors;
	}
	
	public boolean dbUpdate(ArrayList<VM> vmList)
	{
	    ArrayList <VM> dbVMList = new ArrayList<VM>();
		
	    // Firstly, get a list of all existing VM's in the database
	    
		if(dbType.equals("postgres"))
		{			
			try
			{
				db = DriverManager.getConnection("jdbc:postgresql:" + dbPath, dbUser, dbPass);
				sql = db.createStatement();
				ResultSet results = sql.executeQuery("SELECT * FROM vm_stats");
				
				if(results != null)
				{
					while(results.next())
					{
						String vmID = results.getString(2);
						String vmName = results.getString(3);
						String vmHyp = results.getString(4);
						String vmType = results.getString(5);
						int vmCpu = results.getInt(6);
						int vmMem = results.getInt(7);
						int vmUnits = results.getInt(8);
						VM tempVM = new VM(vmName, vmID, vmHyp, vmType, vmCpu, vmMem);
						tempVM.setCount(vmUnits);
						dbVMList.add(tempVM);
					}
					
					results.close();
				}
			}
			
			catch(Exception err)
			{
				return false;
			}
		}
		
		else if(dbType.equals("mysql"))
		{
			// Do nothing :-)
		}
		
		else
		{
			System.out.println("No supported database found!");
			return false;
		}
		
		ArrayList<String> vmExists = new ArrayList<String>();
		
		// To avoid an exception, check whether there is a list of existing ones!
		
		if(dbVMList.size() > 0)
	    {	
	    	for(VM thisVM: vmList)
	    	{	
	    		for(VM dbVM: dbVMList)
	    		{
	    			if(thisVM.getHash().equals(dbVM.getHash()))
	    			{
	    				vmExists.add(thisVM.getVMName());
	    				String sqlstring = "UPDATE vm_stats SET vm_units = vm_units + " + thisVM.getCount();;
	    				sqlstring += ", last_updated = CURRENT_TIMESTAMP WHERE vm_name = '" + thisVM.getVMName() + "';";

	    				try
	    				{
	    					sql.execute(sqlstring);
	    				}
	    				
	    				catch(Exception err)
	    				{
	    					return false;
	    				}
	    				
	    				if(thisVM.getCPU() != dbVM.getCPU())
	    				{
	    					sqlstring = "UPDATE vm_stats SET vm_vcpu = " + thisVM.getCPU();;
		    				sqlstring += "WHERE vm_name = '" + thisVM.getVMName() + "';";
		    				
		    				try
		    				{
		    					sql.execute(sqlstring);
		    				}
		    				
		    				catch(Exception err)
		    				{
		    					return false;
		    				}
					}

					if(thisVM.getMem() != dbVM.getMem())
					{
						sqlstring = "UPDATE vm_stats SET vm_mem = " + thisVM.getMem();;
						sqlstring += "WHERE vm_name = '" + thisVM.getVMName() + "';";

						try
						{
							sql.execute(sqlstring);
						}

						catch(Exception err)
						{
							return false;
						}
					}

				}
			}
		}
	}
		
		dbVMList.clear();
		
		for(VM thisVM: vmList)
		{
			if(!vmExists.contains(thisVM.getVMName()))
			{
				try
				{
					String sqlstring = "INSERT INTO vm_stats(fc_id, vm_id, vm_name, vm_hyp, vm_type, vm_vcpu, vm_mem, vm_units, first_created, last_updated)";
					sqlstring += " VALUES('" + thisVM.getHash() + "','" + thisVM.getVMID();
					sqlstring += "','" + thisVM.getVMName() + "','" + thisVM.getHypervisor();
					sqlstring += "','" + thisVM.getOSType() + "'," + thisVM.getCPU() + "," + thisVM.getMem();
					sqlstring += "," + thisVM.getCount() + ", CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);";
					sql.execute(sqlstring);
				}
				
				catch(Exception err)
				{
					// Need to reconsider returning false here... it's not technically a fail - gets messy!
					//return false;
				}
			}
		}
		
		return true;
	}
}

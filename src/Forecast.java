import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import org.apache.commons.daemon.*;


public class Forecast implements Daemon
{
	// Set interval timer
	public static int interval_minutes = 5;
	public static String configLoc = "/etc/forecast.conf";
	
	public ArrayList<FCAdaptor> workerList;
	public ArrayList<FCAdaptor> oldWorkers = new ArrayList<FCAdaptor>();
	public ArrayList<VM> updateVMList = new ArrayList<VM>();
	public FCDatabase dbWorker;
	public Date now;
	public String DB_TYPE, DB_PATH, DB_USER, DB_PASS;
	public static Timer timer = null;
	
	public void loadConfiguration()
	{
		Properties configFile = new Properties();
		
		try 
		{
			configFile.load(new FileInputStream(configLoc));
			DB_TYPE = configFile.getProperty("DB_TYPE");
			DB_PATH = configFile.getProperty("DB_PATH");
			DB_USER = configFile.getProperty("DB_USER");
			DB_PASS = configFile.getProperty("DB_PASS");
		} 
		
		catch (IOException e) 
		{
			System.out.println("\nERROR: No configuration file found! [Exiting]");
			System.exit(0);
		}
		
		// Test database connection, if it doesn't work- we need to quit!
		dbWorker = new FCDatabase(DB_TYPE,DB_PATH,DB_USER,DB_PASS);
		if (!dbWorker.dbTested)
		{
			System.out.println("\nERROR: Database cannot be found or connection problem! [Exiting]");
			System.exit(0);
		}
	}
	
	public void start_exec()
	{
		timer = new Timer();
		timer.schedule(new TimerTask()
		{
			public void run() 
			{
				now = new Date();
				System.out.println("\nForecast executing at: " + now);
				
				if (!oldWorkers.isEmpty())
				{
					ArrayList<FCAdaptor> tempList = new ArrayList<FCAdaptor>();
					tempList = dbWorker.getAdaptors();

					for(FCAdaptor newAdaptor: dbWorker.getAdaptors())
					{
						int i = 0;

						for(FCAdaptor oldAdaptor: oldWorkers)
						{
							if (newAdaptor.connectionURL.equals(oldAdaptor.connectionURL))
							{
								tempList.remove(i);
								tempList.add(oldAdaptor);
							}
						}

						i++;
					}
					workerList = tempList;
				}

				else workerList = dbWorker.getAdaptors();

				if (workerList.size() == 0)
				{
					System.out.println("WARNING: No adaptors found or are all disabled. Halting until next scan!");
					System.out.println("Please configure adaptors using 'forecast --configure'");
					return;
				}

				else System.out.println("Found " + workerList.size() + " adaptor profile(s).");

				for(FCAdaptor anAdaptor: workerList)
				{
					int success = anAdaptor.execute();
					if(success == 1)
					{
						updateVMList.addAll(anAdaptor.getLatest());
					}
				}

				if(updateVMList.size() != 0)
				{	
					if(dbWorker.dbUpdate(updateVMList))
					{
						System.out.println("Database update was successful.");
						updateVMList.clear();
					}
				
					else
					{
						System.out.println("Could not write to database, caching for next time. Please fix this!");
					}
				}
				
				else
				{
					System.out.println("Nothing to update at this time. Skipping database-update.");
				}

				oldWorkers = workerList;
			}
			
		}, 0, (interval_minutes * 60 * 1000));
	}
	
	public static void main(String[] args)
	{
		Forecast forecast = new Forecast();
		System.out.print("Loading configuration... ");
		forecast.loadConfiguration();
		forecast.start_exec();
	}

	@Override
    public void start() throws Exception
    {
		System.out.println("Forecast v1.0: Started");
		System.out.println("----------------------");
        main(null);
    }

	@Override
    public void init(DaemonContext dc) throws Exception
    {
        // Empty method
    }

    @Override
    public void stop() throws Exception
    {
        System.out.println("Forecast: Killing Daemon...");
        if (timer != null)
        {
            timer.cancel();
        }
    }

    @Override
    public void destroy()
    {
        System.out.println("Forecast: Stopped!");
    }

}
	

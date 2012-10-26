import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.lang.Long;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class RHEVAdaptor extends FCAdaptor
{
	URLConnection conn;
	URL url;
	HttpURLConnection httpConn;
	StringBuilder responseBuilder;

	public RHEVAdaptor(String url, String username, String password) 
	{
		super(url, username, password);
	}

	public int execute()
	{
		if(makeConnection())
		{
			try 
			{
				httpConn = (HttpURLConnection)conn;
				responseBuilder = new StringBuilder();

				BufferedReader rd = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
				String line;

				while ((line = rd.readLine()) != null)
				{
					responseBuilder.append(line + '\n');
				}
			}
			
			catch(Exception err)
			{
				System.out.println(err);
			}
			
			try
			{
				DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(responseBuilder.toString()));
				Document doc = db.parse(is);
				NodeList nodes = doc.getElementsByTagName("vm");
				
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				
				newVMList.clear();
				UnitPrices prices = new UnitPrices();
				int unknown_count = 0;
				
				for (int i = 0; i < nodes.getLength(); i++) 
				{
					Element element = (Element) nodes.item(i);
					String vmID = element.getAttribute("id");
					NodeList name = element.getElementsByTagName("name");
					Element line = (Element) name.item(0);
					String vmName = line.getFirstChild().getNodeValue().trim();
					String osType = xpath.evaluate("os[1]/@type", element);
					String cpuSockets = xpath.evaluate("cpu/topology/@sockets", element);
					String cpuCores = xpath.evaluate("cpu/topology/@cores", element);
					int vCPUs = Integer.parseInt(cpuCores) * Integer.parseInt(cpuSockets);
					NodeList memlist = element.getElementsByTagName("memory");
					Element mem = (Element) memlist.item(0);
					String memString = mem.getFirstChild().getNodeValue();
					Long memLong = Long.parseLong(memString)/1048576;
					int vMem = memLong.intValue();
					String vmStatus = xpath.evaluate("status/state/text()", element);

					if(osType.equalsIgnoreCase("unassigned")) unknown_count++;
					if(vmStatus.equals("up") || vmStatus.equals("UP"))
					{
						if(osType.contains("rhel") || osType.contains("RHEL"))
						{
							VM tempVM = new VM(vmName, vmID, "RHEV", osType, vCPUs, vMem);
							tempVM.setCount(prices.getUnits(vCPUs, vMem));
							newVMList.add(tempVM);
						}
					}
				}

				if (unknown_count > 0) System.out.format("WARNING: %s VM(s) have *unassigned* OS type!\n", unknown_count);
			}
			
			catch(Exception err)
			{
				System.out.println("ERROR: " + err);
			}
			
			Date now = new Date();
			lastCapture = now;
			return 1;
		}
		
		else
		{
			//System.out.println("ERROR: Could not connect to RHEV API at " + connectionURL);
			return 0;
		}
	}
	
	public boolean makeConnection()
	{
		Authenticator.setDefault(new Authenticator() 
		{
		    protected PasswordAuthentication getPasswordAuthentication() 
		    {
		    	return new PasswordAuthentication(connectionUser, connectionPass.toCharArray());
		    }
		});
		
		javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier()
		{
			public boolean verify(String hostname,javax.net.ssl.SSLSession sslSession) 
			{ 
				return true; 
			}
		});
		
		try 
		{
			System.out.println("Connecting to: " + connectionURL);
			url = new URL(connectionURL);
			conn = url.openConnection();
			conn.connect();
			return true;
		}
		
		catch(Exception err)
		{
			System.out.println("Could not connect! Error: " + err);
			return false;
		}
	
	}
}

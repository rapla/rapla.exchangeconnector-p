package org.rapla.plugin.exchangeconnector.server.exchange;

import java.net.URI;
import java.net.URISyntaxException;

import microsoft.exchange.webservices.data.ExchangeService;
import microsoft.exchange.webservices.data.ExchangeVersion;
import microsoft.exchange.webservices.data.NameResolutionCollection;
import microsoft.exchange.webservices.data.ResolveNameSearchLocation;
import microsoft.exchange.webservices.data.WebCredentials;

import org.rapla.framework.RaplaException;

/**
 * This class is obliged with the task to provide a connection to a specific Exchange Server-instance
 * by means of generating an {@link ExchangeService} object
 *
 * @author lutz
 */
public class EWSConnector {

    private static final int SERVICE_DEFAULT_TIMEOUT = 10000;
    private ExchangeService service;
    String user;
	
//	private final Character DOMAIN_SEPERATION_SYMBOL = new Character('@');

    public EWSConnector(String fqdn, String exchangeUsername,String exchangePassword) throws RaplaException  {
    	this( fqdn,new WebCredentials(exchangeUsername, exchangePassword));
    }
    /**
     * The constructor
     *
     * @param fqdn        : {@link String}
     * @param credentials : {@link WebCredentials}
     * @throws Exception
     */
    public EWSConnector(String fqdn, WebCredentials credentials) throws RaplaException  {
        super();
        ExchangeService tmpService = new ExchangeService(ExchangeVersion.Exchange2010_SP1); //, DateTools.getTimeZone());//, DateTools.getTimeZone());

        tmpService.setCredentials(credentials);
        tmpService.setTimeout(SERVICE_DEFAULT_TIMEOUT);

        //define connection url to mail server, assume https
        this.user = credentials.getUser();
        URI uri;
		try {
			uri = new URI(fqdn + "/ews/Exchange.asmx");
			tmpService.setUrl(uri);
		} catch (URISyntaxException e) {
			throw new RaplaException(e.getMessage(), e);
		}

        // removed because auto is discovery not yet support
        // tmpService.autodiscoverUrl(getUserName()+this.DOMAIN_SEPERATION_SYMBOL+getFqdn());//autodiscover the URL with the parameter "username@fqdn
        this.service = tmpService;

        /*   // test if connection works
        getService().resolveName(getUserName(), ResolveNameSearchLocation.DirectoryOnly, true);
        FindItemsResults<Item> items = tmpService.findItems(WellKnownFolderName.Calendar, new SearchFilter.IsEqualTo(EmailMessageSchema.IsRead, Boolean.TRUE), new ItemView(100));
        for (Item item : items) {
            System.out.println(item.getSubject());
        }*/
        //SynchronisationManager.logInfo("Connected to Exchange at + " + uri + " at timezone: " + DateTools.getTimeZone());
    }


    /**
     * @return {@link ExchangeService} the service
     */
    public ExchangeService getService() {
        return service;
    }


    public void test( ) throws Exception {
		NameResolutionCollection nameResolutionCollection = getService().resolveName(user, ResolveNameSearchLocation.DirectoryOnly, true);
		if (nameResolutionCollection.getCount() == 1) {
			String smtpAddress = nameResolutionCollection.nameResolutionCollection(0).getMailbox().getAddress();
			if (!smtpAddress.isEmpty()) {
				//return smtpAddress;
			}
		}
		//throw new Exception("Credentials are invalid!");
	}

}
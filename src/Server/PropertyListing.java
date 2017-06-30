package Server;

import java.util.List;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebResult;

@WebService
public class PropertyListing{

	private PropertyUtility properties;

	public PropertyListing(){
		properties = new PropertyUtility();
	}
	
	@WebMethod 
	@WebResult(partName = "properties_response") 
	public List<Property> getProperties(){//get a list of properties that the service is managing and send to client
		return this.properties.getProperties();
	}
	
	@WebMethod //Add a property from client to the web
	public void addProperty(String type, int area, int bedrooms, int price, long startTime, long endTime){
		this.properties.addProperty(type,area,bedrooms,price,startTime,endTime);
	}

	@WebMethod
	public String bidOnProperty(String propertyID,int bid){
		if(properties.checkProperty(propertyID)) {//ensure property is on list
			if (properties.checkPropertySaleTime(propertyID)){//check that property can be bidded on
				if (properties.addBidToProperty(propertyID, bid)) {//check if bid is higher then current and update if it is
					return "Successes bid has been accepted";
				} else {
					return "Failed bid was to low!";
				}
			}else{
				return "Failed Property not on sale!";
			}
		}else {
			return "Failed Property ID invalid!";
		}
	}

	@WebMethod
	public String PropertyBidNotification(String id){
		 return properties.getBidNotification(id);//wait till a new higher bid is put on property with selected id
	}

}

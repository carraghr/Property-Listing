package Server;

import java.util.*;

public class PropertyUtility{	
	
	private Map<String, Property> propertyMap;
	private int newHouseID = 4;
	private int newApartID = 3;

	public PropertyUtility(){
		propertyMap = new HashMap<String, Property>();
		generatePropertyList();
	}
	
	public Property getProperty(String id){
		return propertyMap.get(id);//get property with id.
	}
	
	public void generatePropertyList(){
		//gen data for testing
		Date now = new Date();
		long time = now.getTime();

		propertyMap.put("H1", new Property("H1",3,2,375000,time,time + 100000l));
		propertyMap.put("H2", new Property("H2",5,3,360000,time,time+ 200000l));
		propertyMap.put("H3", new Property("H3",3,3,500000,time,time+ 300000l));
		propertyMap.put("A1", new Property("A1",2,9,550000,time,time+ 400000l));
		propertyMap.put("A2", new Property("A2",7,1,150000,time,time+ 500000l));
	}
	
	public List<Property> getProperties(){
		List <Property> list = new ArrayList <Property>();//create list of properties
		Set <String> keys = propertyMap.keySet();//get keys to loop through map
		for (String key: keys)
			list.add(propertyMap.get(key));//add property to list to be retured
		return list;
	}
	
	public void addProperty(String type, int area, int bedrooms, int price, long startTime, long endTIme){
		if(type.equals("H")){//add a house property
			Property temp = new Property(type+newHouseID, area, bedrooms, price, startTime, endTIme);
			propertyMap.put("H" + newHouseID, temp);
			newHouseID++;
		}else{//add an appartment property
			Property temp = new Property(type+newApartID, area, bedrooms, price, startTime, endTIme);
			propertyMap.put("A" + newApartID, temp);
			newApartID++;
		}
	}

	public boolean checkProperty(String id){
		return propertyMap.containsKey(id);
	}

	public boolean addBidToProperty(String id, int bid){
		Property temp = propertyMap.get(id);
		if(temp!=null){
			if(temp.updateBid(bid)){
				propertyMap.put(id, temp);
				return true;
			}
			return false;
		}
		return false;
	}

	public String getBidNotification(String id){
		try{
			while(!propertyMap.get(id).isUpdated()){}//loop incase of false reading
			return "A bid of "+propertyMap.get(id).getBid()+" has been made on property "+id;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return "";
	}

	public boolean checkPropertySaleTime(String id){
		Property temp = propertyMap.get(id);
		Date now = new Date();
		long time = now.getTime();//check time through linux time stamp
		return temp.getStartTime() < time && time < temp.getEndTime();
	}
}

package Client;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.text.ParseException;
import java.util.List;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.concurrent.ExecutionException;

public class Client {

	/*
		This class is used to handle async calls to PropertyBidNotification from bidOnProperty
	*/
    static class BidHandler implements AsyncHandler<PropertyBidNotificationResponse> {
        public void handleResponse(Response<PropertyBidNotificationResponse> future) {
            try {
				//get responce of updated bid
                PropertyBidNotificationResponse response = future.get();
                String responseMessage = response.getReturn();
				//print responce to console
                System.out.println(responseMessage);
            }
            catch(InterruptedException e) { System.err.println(e); }
            catch(ExecutionException e) { System.err.println(e); }
        }

	}

	//basic method of checking if inputted year is a leap or not save from incorrect input
    public static boolean isLeapYear(int year){
        if(year % 4 != 0){
            return false;
        }else if(year % 400 == 0){
            return true;
        }else if(year % 100 == 0){
            return false;
        }else{
            return true;
        }
    }

	//method to get a new date from keyboard input also gets the time 
	static Date getDate(){
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Year in format 2xxx  ");
        String year = sc.next();
        int check = Integer.parseInt(year);
        System.out.println();
		//use linux time stamp need to check its within range
        while(check<1970 || check > 2038){
            System.out.print("Enter Year in format 2xxx  ");
            year = sc.next();
            check = Integer.parseInt(year);
            System.out.println();
        }

        boolean isLeap = isLeapYear(check);

        System.out.print("Enter month in format xx ie 01,02, ... 11  ");
        String m = sc.next();
        int checkm = Integer.parseInt(m);
        System.out.println();
        while(checkm < 1 || checkm > 12|| m.length() == 1){
            System.out.print("Enter month in format xx ie 01,02, ... 11  ");
            m = sc.next();
            checkm = Integer.parseInt(m);
        }

        int maxDays=0;

        if(checkm == 2 ){//feb
            if(isLeap){
                maxDays=29;
            }else{
                maxDays=28;
            }
        }else if(checkm == 1 ||checkm == 3||checkm == 5||checkm == 7||checkm == 8||checkm == 10||checkm == 12){
            maxDays=31;
        }else{
            maxDays=30;
        }

        System.out.print("Enter day in format xx ie 01,02, or " +maxDays+"  ");
        String day = sc.next();
        int checkd = Integer.parseInt(day);
        System.out.println();
        while(checkd < 0 || checkd > maxDays|| day.length() == 1){
            System.out.print("Enter day in format xx ie 01,02, or " +maxDays+"  ");
            day = sc.next();
            checkd = Integer.parseInt(day);
        }


        System.out.print("Enter hour in format xx ie 01,02, or 23  ");
        String startHour = sc.next();
        int checkH = Integer.parseInt(startHour);
        System.out.println();
        while(checkH < 0 || checkH > 23|| startHour.length() == 1){
            System.out.print("Enter hour in format xx ie 01,02, or 23  ");
            startHour = sc.next();
            checkH = Integer.parseInt(startHour);
        }

        System.out.print("Enter Minute in format xx ie 01,02, or 59  ");
        String startMin = sc.next();
        int checkMin = Integer.parseInt(startMin);
        System.out.println();
        while(checkMin < 0 || checkMin > 59|| startMin.length() == 1){
            System.out.print("Enter Minute in format xx ie 01,02, or 59  ");
            startMin = sc.next();
            checkMin = Integer.parseInt(startMin);
        }

        String target = ""+year+"-"+m+"-"+day +" "+startHour+":"+startMin+":00.0";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date result = null;
        try {
            result = df.parse(target);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void bidOnProperty(PropertyListing port, String id, int amount){
		//create object to send information to service
        BidOnProperty bid = new BidOnProperty();
        bid.setArg0(id);
        bid.setArg1(amount);

        BidOnPropertyResponse response = port.bidOnProperty(bid);

        String message = response.getReturn();
		
		//check if the bid was accepted, means look out for higher bids
        if(message.indexOf("accepted") > -1){
            PropertyBidNotification nof = new PropertyBidNotification();
            nof.setArg0(id);
			//assign responce to bid handler class
            port.propertyBidNotificationAsync(nof,new BidHandler());
        }

        System.out.println(message);
    }

    public static void getProperties(PropertyListing port){
		//get list of properties
        GetPropertiesResponse properties = port.getProperties(new GetProperties());

        List<Property> propertiesList = properties.getReturn();
        System.out.println();
        System.out.printf("%4s %4s %7s %12s %12s %12s %12s","Type","Area","Bedrooms","Price","Start Time","End Time", "Current Bid");
		//formatted printing of list
        for (Property p : propertiesList) {
            System.out.println();
            Date temp1 = new Date(p.getStartTime());
            Date temp2 = new Date(p.getEndTime());

            System.out.printf("%4s %,4d %,7d %,12d ",p.getType(),p.getArea(),p.getNumberOfBedrooms(),p.getPrice());
            System.out.printf("    %1$tH:%1$tM:%1$tS",temp1);
            System.out.printf("      %1$tH:%1$tM:%1$tS ",temp2);
            System.out.printf("%,12d",p.getBid());
        }
        System.out.println();
    }


    public static void addProperty(PropertyListing port,String type,int district,int numberOfBedrooms,int price,long start, long end){
        AddProperty property = new AddProperty();
		//Set all information that is needed for a property
        property.setArg0(type);
        property.setArg1(district);
        property.setArg2(numberOfBedrooms);
        property.setArg3(price);
        property.setArg4(start);
        property.setArg5(end);
		//send all information to web
        port.addProperty(property);
    }

    public static void main(String[] args) throws Exception{

        PropertyListingService service = new PropertyListingService();
        PropertyListing port = service.getPropertyListingPort();

        Scanner sc = new Scanner(System.in);
        String quit = "s";

        while(!quit.equals("q")){

            System.out.println("Main Menu");
            System.out.println("Options");
            System.out.println("l to get list of properties");
            System.out.println("p to put a property on server");
            System.out.println("b to put a bid on a property");
            System.out.println("q to quit");

            String choice = sc.next();

            if(choice.length()==1){

                if(choice.equals("l")){
                    getProperties(port);
                }else if(choice.equals("p")){//get info on property to be added to web

                    System.out.print("Enter property H or A  ");
                    String type = sc.next();
                    type = type.toUpperCase();

                    System.out.print("Enter area 1 to 24  ");
                    int area = sc.nextInt();
                    while(area < 0 && area>24){
                        System.out.print("Enter area 1 to 24  ");
                        area = sc.nextInt();
                    }

                    System.out.print("Enter number of bedrooms 1 to ...  ");
                    int bedrooms = sc.nextInt();
                    while(bedrooms < 0){
                        System.out.print("Enter number of bedrooms 1 to ...  ");
                        bedrooms = sc.nextInt();
                    }

                    System.out.print("Enter price 1 to ...  ");
                    int price = sc.nextInt();

                    while(price < 0){
                        System.out.print("Enter price 1 to ...  ");
                        price = sc.nextInt();
                    }

                    System.out.println("Enter Date for start of sale");
                    Date start = getDate();
                    System.out.println("Enter Date for end of sale");
                    Date end = getDate();

                    addProperty(port,type,area,bedrooms,price,start.getTime(),end.getTime());

                }else if(choice.equals("b")){

                    System.out.println("Enter property type to place bid");
                    String type = sc.next();
                    System.out.println("Enter bid");
                    int bid = sc.nextInt();
                    bidOnProperty(port,type,bid);

                }else if(choice.equals("q")){
                    quit = "q";
                }

            }else{
                System.out.println("Input does not match options available!");
            }

            System.out.println();
        }
    }
}

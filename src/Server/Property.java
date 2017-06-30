package Server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Property {
	private String type;
	private int area;
	private int numberOfBedrooms;
	private int price;
	private int bid;
	private long bidStartTime;
	private long bidEndTime;
	private boolean isFirstBid;
	private ReentrantLock propertyLock = new ReentrantLock();
	private Condition bidIsUpdated = propertyLock.newCondition();


	public Property() {}
	
	public Property(String type, int area,int numberOfBedrooms, int price,long start, long end){
		setType(type);
		setArea(area);
		setNumberOfBedrooms(numberOfBedrooms);
		setPrice(price);
		setStartTime(start);
		setEndTime(end);
	}


	
	public void setStartTime(long start){
		bidStartTime = start;
	}
	
	public void setEndTime(long end){
		bidEndTime = end;
	}
	
	public long getStartTime(){
		return bidStartTime;
	}
	
	public long getEndTime(){
		return bidEndTime;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setArea(int area){
		this.area = area;
	}
	
	public int getArea(){
		return this.area;
	}
	
	public void setNumberOfBedrooms(int numberOfBedrooms){
		this.numberOfBedrooms = numberOfBedrooms;
	}
	
	public int getNumberOfBedrooms(){
		return this.numberOfBedrooms;
	}
	
	public void setPrice(int price){
		this.price = price;
	}
	
	public int getPrice(){
		return this.price;
	}
	
	public void setBid(int bid){
		this.bid = bid;
	}

	public int getBid(){
		return this.bid;
	}

	public boolean updateBid(int bid){
		if(bid > this.bid){
			this.bid=bid;
			//lock to send signal
			propertyLock.lock();
			bidIsUpdated.signalAll();
			propertyLock.unlock();
			return true;
		}else{
			return false;
		}
	}

	public boolean isUpdated() throws InterruptedException {
		propertyLock.lock();
		bidIsUpdated.await();
		propertyLock.unlock();
		return true;
	}
}

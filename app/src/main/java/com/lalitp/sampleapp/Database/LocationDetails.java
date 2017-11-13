package com.lalitp.sampleapp.Database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "mylocation")
public class LocationDetails implements Serializable {

	/**
	 * Model class for product_details database table
	 */
	private static final long serialVersionUID = -222864131214757024L;
	
	public static final String ID_FIELD = "_id";
	public static final String NAME_FIELD = "location_name";
	public static final String LATTITUDE_FIELD = "location_lat";
	public static final String LONGITUDE_FIELD = "location_long";
	public static final String COL_TIMESTAMP = "timestamp";
	public static final String LOCATION_PIC = "location_pic";

	// Primary key defined as an auto generated integer 
	// If the database table column name differs than the Model class variable name, the way to map to use columnName
	@DatabaseField(generatedId = true, columnName = ID_FIELD)
	public long locationId;

	// Define a String type field to hold user's id
	@DatabaseField(columnName = NAME_FIELD)
	public String locName;

	// Define a String type field to hold product's id
	@DatabaseField(columnName = LATTITUDE_FIELD)
	public String locLat;

	// Define a String type field to hold category's id
	@DatabaseField(columnName = LONGITUDE_FIELD)
	public String locLong;

	// Define a String type field to hold product's quantity
	@DatabaseField(columnName = LOCATION_PIC)
	public String locPic;


	// Define a String type field to hold product's date of insertion
	@DatabaseField(columnName = COL_TIMESTAMP)
	public long timestamp;

	// Default constructor is needed for the SQLite, so make sure you also have it
	public LocationDetails(){
		
	}
	
	//For our own purpose, so it's easier to create a LocationDetails object


	public LocationDetails(long locationId, String locName, String locLat, String locLong, String locPic) {
		this.locationId = locationId;
		this.locName = locName;
		this.locLat = locLat;
		this.locLong = locLong;
		this.locPic = locPic;
	}

	@Override
	public String toString() {
		return "LocationDetails{" +
				"_id=" + locationId +
				", location_name='" + locName + '\'' +
				", location_lat='" + locLat + '\'' +
				", location_long='" + locLong + '\'' +
				", location_pic='" + locPic + '\'' +
				", timestamp=" + timestamp +
				'}';
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public String getLocName() {
		return locName;
	}

	public void setLocName(String locName) {
		this.locName = locName;
	}

	public String getLocLat() {
		return locLat;
	}

	public void setLocLat(String locLat) {
		this.locLat = locLat;
	}

	public String getLocLong() {
		return locLong;
	}

	public void setLocLong(String locLong) {
		this.locLong = locLong;
	}

	public String getLocPic() {
		return locPic;
	}

	public void setLocPic(String locPic) {
		this.locPic = locPic;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}

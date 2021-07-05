import java.util.*;
import java.net.*;
import java.io.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class WeatherApp
{
    //authentication key for open weather api
    private static String apiKeyOpenWeather = "***openWeatherApiKey***";
    
    //authentication key for tom tom api
    private static String apiKeyTomTom = "***tomtomApiKey***";
    
    //regex to extract latitude from geo encoding api response
    private static String geoEncodingLatitudeRegex = "(?<=\"lat\":)([0-9.]*)(?=,)";
    
    //regex to extract longitude from geo encoding api response
    private static String geoEncodingLongitudeRegex = "(?<=\"lon\":)([0-9.]*)(?=[,\\}])";
    
    //regex to extract location from reverse geo encoding api response
    private static String reverseGeoEncodingRegex = "(?<=\"name\":\")([a-zA-Z ]*)(?=\")";
    
    //regex to filter latitude and longitude of all intermediate points
    private static String getIntermediateRoutesRegex = "(?<=\"points\":\\[)(.*)(?=\\]\\}\\])";
    
    //regex to extract latitude and longitude from all filtered points in format -> latitude:longitude
    private static String getIntermediateRoutesPointRegex = "(?<=\\{\"latitude\":)([0-9.]*)(?=,)|(?<=\"longitude\":)([0-9.]*)(?=\\})";

    //regex to extract weather details in format -> temparature:weatherReport:description
    private static String getWeatherRegex = "(?<=\"temp\":)([0-9.]*)(?=[,}])|(?<=\"main\":\")([a-zA-Z ]*)(?=\")|(?<=\"description\":\")([a-zA-Z ]*)(?=\")";
    
    
    //make http request
    private static String makeHttpRequest(String requestUrl){
        try{
            
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestMethod("GET");
            
            StringBuilder result = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
            }
            connection.disconnect();
            return result.toString();
        }catch(Exception e){
            System.out.println("Exception occurred! " + e.getMessage());
            return "";
        }
    }
    
    //get data from json String
    private static List<String> getDataFromString(String text ,String regex){
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        
        int groupCount = matcher.groupCount();
        int groupCounter = 1;
        List<String> data = new ArrayList<>();
        String s = "";
        while(matcher.find()){
            if(groupCounter > 1 && groupCounter <= groupCount){
                s+=":"; 
                s+=matcher.group(groupCounter);
            }else{
                s=matcher.group(groupCounter);
            }
            if(groupCounter == groupCount){
                data.add(s);
            }
            groupCounter=(groupCounter%groupCount)+1;
        }
        return data;
    }
    
    
    //return latitude and longitude of the location
    private static ArrayList<String> getGeoEncoding(String location){
        
        //request format : https://api.openweathermap.org/geo/1.0/direct?q=rohtak&appid=apiKey
        String locationUrl = "https://api.openweathermap.org/geo/1.0/direct?q=";
        locationUrl = locationUrl + location + "&appid=" + apiKeyOpenWeather;
        
        //make http request to get latitude and longitude
        String response = makeHttpRequest(locationUrl);
        
        //response format : [{"name":"Rohtak","lat":28.9,"lon":76.5667,"country":"IN"}]
        List<String> latitude = getDataFromString(response ,geoEncodingLatitudeRegex);
        List<String> longitude = getDataFromString(response ,geoEncodingLongitudeRegex);
        
        ArrayList<String> coordinates = new ArrayList<>();
        if(latitude.size() > 0 && longitude.size() > 0){
            coordinates.add(latitude.get(0).trim());
            coordinates.add(longitude.get(0).trim());
        }
        
        return coordinates;
    }
    
    //return location from latitude and longitude 
    private static String getReverseGeoEncoding(String latitude ,String longitude){
        
        //request format : http://api.openweathermap.org/geo/1.0/reverse?lat=51.5098&lon=-0.1180&limit=5&appid=apiKey
        String locationUrl = "http://api.openweathermap.org/geo/1.0/reverse?lat=";
        locationUrl += latitude + "&lon=" + longitude + "&limit=1&appid=" + apiKeyOpenWeather;
        
        //make http request to get latitude and longitude
        String response = makeHttpRequest(locationUrl);
        
        //response format : [{"name":"City of London","local_names":{"ar":"مدينة لندن","ascii":"City of London","bg":"Сити","ca":"La City","de":"London City","el":"Σίτι του Λονδίνου","en":"City of London","fa":"سیتی لندن","feature_name":"City of London","fi":"Lontoon City","fr":"Cité de Londres","gl":"Cidade de Londres","he":"הסיטי של לונדון","hi":"सिटी ऑफ़ लंदन","id":"Kota London","it":"Londra","ja":"シティ・オブ・ロンドン","la":"Civitas Londinium","lt":"Londono Sitis","pt":"Cidade de Londres","ru":"Сити","sr":"Сити","th":"นครลอนดอน","tr":"Londra Şehri","vi":"Thành phố Luân Đôn","zu":"Idolobha weLondon"},"lat":51.5128,"lon":-0.0918,"country":"GB"}]
        List<String> location = getDataFromString(response ,reverseGeoEncodingRegex);
        
        if(location.size() > 0){
            return location.get(0);
        }
        return "<no registered name>";
    }
    
    //to get list of intermediate points
    private static ArrayList<ArrayList<String>> getIntermediateRoutes(ArrayList<String> sourceEncoding ,ArrayList<String> destinationEncoding){
        
        //request format : https://api.tomtom.com/routing/1/calculateRoute/28.692446,76.923973:28.8896682,76.5462944/json?instructionsType=text&language=en-US&key=apiKey
        String routeUrl = "https://api.tomtom.com/routing/1/calculateRoute/";
        
        //append source latitude and longitude separated by ,
        routeUrl = routeUrl + sourceEncoding.get(0) + "," + sourceEncoding.get(1);
        
        //add separater between souce and destination coordinates
        routeUrl = routeUrl + ":";
        //append destination latitude and longitude separated by ,
        routeUrl = routeUrl + destinationEncoding.get(0) + "," + destinationEncoding.get(1);
        
        //add extra details
        routeUrl = routeUrl + "/json?instructionsType=text&language=en-US&key=";
        
        //add tom tom api key
        routeUrl = routeUrl + apiKeyTomTom;
        
        //make http request to get intermediate stops
        String response = makeHttpRequest(routeUrl);
    
        //response format : {"routes":[{"summary":{"lengthInMeters":49699},"legs":[{"points":[{"latitude":28.69245,"longitude":76.92397},{"latitude":28.69253,"longitude":76.92382},{"latitude":28.89068,"longitude":76.5456},{"latitude":28.88954,"longitude":76.54558}]}]}]}
        List<String> filteredPoints = getDataFromString(response ,getIntermediateRoutesRegex);
        
        //every item is in format -> latitude:longitude
        List<String> points = new ArrayList<>();
        
        ArrayList<ArrayList<String>> pointCoordinates = new ArrayList<>();
        
        if(filteredPoints.size() > 0){
            points = getDataFromString(filteredPoints.get(0) ,getIntermediateRoutesPointRegex);
            
            
            for(int i=0;i<points.size();++i){
                String[] point = points.get(i).split(":");
                pointCoordinates.add(new ArrayList<>(){{
                    add(point[0]);
                    add(point[1]);
                }});
            }
        }
        
        return pointCoordinates;
    }
    
    //return weather based on latitude and longitude
    private static String getWeather(String latitude ,String longitude){
        //call open weather api to get weather details
        //https://api.openweathermap.org/data/2.5/onecall?lat=28.692446&lon=76.923973&lang=en&units=metric&appid=apiKey&exclude=minutely,hourly,daily
        
        String weatherUrl = "https://api.openweathermap.org/data/2.5/onecall?lat=";
        weatherUrl = weatherUrl + latitude + "&lon=" + longitude + "&appid=" + apiKeyOpenWeather+ "&lang=en&units=metric&exclude=minutely,hourly,daily";
        
        String response = makeHttpRequest(weatherUrl);
        
        //weather response format : {"lat":28.6924,"lon":76.924,"timezone":"Asia/Kolkata","timezone_offset":19800,"current":{"dt":1625391565,"sunrise":1625356732,"sunset":1625406849,"temp":38.07,"feels_like":36.75,"pressure":998,"humidity":20,"dew_point":11.21,"uvi":0.95,"clouds":50,"visibility":10000,"wind_speed":3.92,"wind_deg":232,"wind_gust":5.01,"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}]}}
        List<String> weatherData = getDataFromString(response ,getWeatherRegex);
        if(weatherData.size() > 0){
            String[] weather = weatherData.get(0).split(":");
            return "temparature(in celsius) : " + (weather.length > 0 ? weather[0] : "") + " ,report : " + (weather.length > 1 ? weather[1] : "") + " ,description : " + (weather.length > 2 ? weather[2] : "");
        }
        return "";
    }
    
    
	public static void main(String[] args) {
	    
	    Scanner sc = new Scanner(System.in);
	    
	    System.out.println("Hey ,want to travel ,concerned about weather ,Weather app is there to help ,provide us following details");
	    
		//user will enter source and destination
		System.out.println("Enter source : ");
		String source = sc.nextLine();
		System.out.println("Enter destination : ");
		String destination = sc.nextLine();
		
		//convert to lowercase and remove extra spaces around the String
		if(source != null){
		    source = source.toLowerCase().trim();
		}
		if(destination != null){
		    destination = destination.toLowerCase().trim();
		}
		
		//get latitude and longitude of source and destination
		//first element in the list is latitude and second is longitude
	    ArrayList<String> sourceEncoding = getGeoEncoding(source);
		ArrayList<String> destinationEncoding = getGeoEncoding(destination);
	    
		
		//get intermediate stops between source and destination
		ArrayList<ArrayList<String>> stops = getIntermediateRoutes(sourceEncoding ,destinationEncoding);
		
		int counter = 0;
		for(int i=0;i<stops.size();){
		    String latitude = stops.get(i).get(0);
		    String longitude = stops.get(i).get(1);
		    String weather = getWeather(latitude ,longitude);
		    String location = getReverseGeoEncoding(latitude ,longitude);
		    if(!weather.isEmpty())
		        System.out.println(Integer.toString(++counter) +  " Location " + location + " ,Latitude " + latitude + " ,Longitude " + longitude + " , " + weather);
		    System.out.println("");
		    if(i==stops.size()-1){
    		    ++i;
		    }
		    i = i>stops.size() ? stops.size()-1 : i!=stops.size()-1 ? i + (stops.size()/10 + 1) : i;
		}
		
	}
}

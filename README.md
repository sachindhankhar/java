# java
java application to provide weather details of points in the route from a to b

external dependency : 
1. tom tom api is used to get route details : intermediate points with latitude and longitude : signup to https://developer.tomtom.com/ to get api key
2. open weather api is used to get geoencoding ,reverse geoencoding and weather details : signup to https://openweathermap.org/ to get api key

update the api keys in the java source code

to compile : javac WeatherApp.java <br />
to run : java WeatherApp

sample output : <br />

Hey ,want to travel ,concerned about weather ,Weather app is there to help ,provide us following details <br />
Enter source : <br />
delhi <br />
Enter destination : <br />
chandigarh <br />
1 Location Delhi Latitude 28.66674 Longitude 77.21674 temparature(in celsius) : 30.05 ,report : Haze ,description : haze <br />

2 Location National Capital Territory of Delhi Latitude 28.74346 Longitude 77.26269 temparature(in celsius) : 30.13 ,report : Clouds ,description : scattered clouds <br />

3 Location \<no registered name\> Latitude 28.91597 Longitude 77.17452 temparature(in celsius) : 30.1 ,report : Clouds ,description : broken clouds <br />

4 Location Gannaur Latitude 29.06443 Longitude 77.06275 temparature(in celsius) : 30.05 ,report : Clouds ,description : broken clouds <br />

5 Location \<no registered name\> Latitude 29.28175 Longitude 76.99987 temparature(in celsius) : 34.12 ,report : Clouds ,description : overcast clouds <br />

6 Location Gharaunda Latitude 29.53863 Longitude 76.97293 temparature(in celsius) : 33.73 ,report : Clouds ,description : overcast clouds <br />

7 Location \<no registered name\> Latitude 29.77027 Longitude 76.96248 temparature(in celsius) : 32.77 ,report : Clouds ,description : scattered clouds <br />

8 Location Kurukshetra Latitude 30.05829 Longitude 76.87980 temparature(in celsius) : 31.33 ,report : Clouds ,description : few clouds <br />

9 Location \<no registered name\> Latitude 30.35073 Longitude 76.80988 temparature(in celsius) : 31.37 ,report : Clear ,description : clear sky <br />

10 Location Basi Latitude 30.60791 Longitude 76.83604 temparature(in celsius) : 31.13 ,report : Rain ,description : light rain <br />

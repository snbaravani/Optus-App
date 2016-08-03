# Optus-App
 

This app is built on Spring 4.x and tested on Tomcat 7. ALl the APIs are secured with basic HTTP authentication and authorization. Test case document is attached in the the project (Test Case Document.doc) with screenshots of the results for each API. Initial set up and entry to each API is logged.

This project contains two APIs .

 TOP words API  : 

cURL Command: curl -H "Authorization: Basic b3B0dXM6Y2FuZGlkYXRlcw==" http://localhost:8080/OptusApp/counter-api/top/4

Search API:

cURL command: curl  -H "Content-Type: application/json” -H "Authorization: Basic b3B0dXM6Y2FuZGlkYXRlcw==" http://localhost:8080/OptusApp/counter-api/search -d search.json  -X POST 

Author: Subbu Baravani
baravani.aus@gmail.com

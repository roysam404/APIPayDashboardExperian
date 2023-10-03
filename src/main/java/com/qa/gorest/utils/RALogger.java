package com.qa.gorest.utils;

import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.specification.MultiPartSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


//import org.apache.log4j.Logger;
//import org.apache.log4j.LogManager;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.print.RequestPrinter;
import io.restassured.internal.print.ResponsePrinter;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class RALogger {

    private static ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    private static final Logger consoleLogger = LogManager.getLogger(RALogger.class);
    private static final Logger fileLogger = LogManager.getLogger(RALogger.class);

    private static final String MESSAGE_SEPARATOR = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";

    private RALogger() {
    }

    public static void addToLog( String logMessage ) {
        try {
            byteArrayOutputStream.write( ( logMessage + "\t" ).getBytes() );
            //fileLogger.info(logMessage);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public static synchronized void logOutput() {
        StringBuilder log = new StringBuilder( byteArrayOutputStream.toString() );

        if ( log.length() != 0 ) {
            for ( String line : log.toString().split( "\t" ) ) {
                fileLogger.info( line.replace( "\r", "" ) );
            }
        }

        byteArrayOutputStream.reset();
    }

    public static void logOutputToFile(String logMessage){
        addToLog(logMessage);
        logOutput();
    }

    public static void printRequest(FilterableRequestSpecification requestSpec){
//        logOutputToFile("Method: "+requestSpec.getMethod());

        Headers allHeaders= requestSpec.getHeaders();
        List<MultiPartSpecification> multiParts = requestSpec.getMultiPartParams();
//
//        logOutputToFile("Request Headers: "+allHeaders.toString());
//        logOutputToFile("URI: "+requestSpec.getURI());
//        logOutputToFile("Body: "+requestSpec.getBody());
//        logOutputToFile("Base URI: "+requestSpec.getBaseUri());
//        logOutputToFile("Cookies: "+requestSpec.getCookies());
//        logOutputToFile("Base URI: "+requestSpec.getBaseUri());
//        logOutputToFile("Request params:"+ requestSpec.getRequestParams());
//        logOutputToFile("Query params:"+ requestSpec.getQueryParams());
//        logOutputToFile("Form params:"+ requestSpec.getFormParams());
//        logOutputToFile("Path params:"+ requestSpec.getNamedPathParams());
//        List<MultiPartSpecification> multiParts = requestSpec.getMultiPartParams();
//        logOutputToFile("Multi parts: "+multiParts.toString());


        String completeRequest="\n Method: "+ requestSpec.getMethod() + "\n"
                +"URI: "+requestSpec.getURI()+ "\n"
        +"Body: "+requestSpec.getBody()+ "\n"
        +"Base URI: "+requestSpec.getBaseUri()+ "\n"
        +"Cookies: "+requestSpec.getCookies()+ "\n"
        +"Base URI: "+requestSpec.getBaseUri()+ "\n"
        +"Request params:"+ requestSpec.getRequestParams()+ "\n"
        +"Query params:"+ requestSpec.getQueryParams()+ "\n"
        +"Form params:"+ requestSpec.getFormParams()+ "\n"
        +"Path params:"+ requestSpec.getNamedPathParams()+ "\n"
        +"Multi parts: "+multiParts.toString();
        logOutputToFile(completeRequest);
    }

    public static class LogFilter implements Filter {

        @Override
        public Response filter( FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx ) {

            Response response = null;

            try {

                // send the request
                response = ctx.next( requestSpec, responseSpec );

            } catch ( Exception e ) {
                addToLog( "Could not connect to the environment" );
                addToLog( e.getMessage() );
                throw new AssertionError( "Could not connect to the environment" );
            } finally {
                logOutputToFile("\n \n -------------------- Request -------------------- \n \n ");
                // print the request
                //RequestPrinter.print( requestSpec, requestSpec.getMethod(), requestSpec.getURI(), LogDetail.ALL, requestSpec.getConfig().getLogConfig().blacklistedHeaders(), new PrintStream( byteArrayOutputStream ),true );
                printRequest(requestSpec);
                // add an empty line
                addToLog( "\n" );
                if ( response != null ) {
                    logOutputToFile("\n \n -------------------- Response -------------------- \n \n ");
                    // print the response
                    ResponsePrinter.print( response, response, new PrintStream( byteArrayOutputStream ), LogDetail.ALL, true, requestSpec.getConfig().getLogConfig().blacklistedHeaders() );
                }
                // add the message separator
                addToLog( MESSAGE_SEPARATOR );

                // print the log
                logOutput();
            }

            return response;
        }
    }
}
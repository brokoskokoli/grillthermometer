package com.example.stean1990.grillthermometer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;


/**
 * Created by stean1990 on 20.05.2016.
 */
public class TempData {
    private Vector<Double> m_temps;
    private String m_Data;
    private String m_error;
    private int m_Size;
    private String m_Output;

    public TempData() {
        m_temps = new Vector();
        m_Data = "";
        m_error = "";
        m_Size = 0;
        m_Output = "";
    }

    public Vector<Double> GetTemps()
    {
        return m_temps;
    }

    public String GetData()
    {
        return m_Data;
    }


    public String GetError()
    {
        return m_error;
    }

    public String GetOutput()
    {
        return m_Output;
    }

    private String AfterFirst( String haystack, String needle )
    {
        int pos = haystack.indexOf( needle );

        return pos >= 0 ? haystack.substring( pos + needle.length() ) : "";
    }

    private String BeforeFirst( String haystack, String needle )
    {
        int pos = haystack.indexOf( needle );

        return pos >= 0 ? haystack.substring( 0, pos ) : haystack;
    }

    private Vector<Double> ExtractTemps( String page )
    {
        Vector<Double> vec = new Vector<Double>();

        String pageremaining = AfterFirst( page, "temperature_indicator");
        while ( !pageremaining.isEmpty() )
        {
            String temp = AfterFirst( pageremaining, ">");
            temp = BeforeFirst( temp, "&" );

            try
            {
                vec.add( Double.parseDouble( temp ) );
            }
            catch (NumberFormatException e)
            {

            }

            pageremaining = AfterFirst( pageremaining, "temperature_indicator");
        }


        return vec;
    }


    private String ExtractTempString( String page )
    {
        Vector<Double> temps = ExtractTemps( page );
        String result = "";
        for ( int i = 0 ; i < temps.size(); ++i )
        {
            if( i != 0 )
            {
                result = result + "/";
            }
            result = result + temps.get(i);
        }

        return result;
    }


    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            return "";
        }
        return total.toString();
    }

    public boolean LoadInfo( final String tempurl )
    {
        m_error = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL( tempurl );
             urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(3000);
            InputStream in = new BufferedInputStream( urlConnection.getInputStream());
            String line = convertStreamToString( in );


            m_Data = line;
            in.close();

            m_Output = ExtractTempString( m_Data );
            m_temps = ExtractTemps( m_Data );

        } catch (MalformedURLException e) {
            m_error = "URL wrong";
        } catch (IOException e) {
            m_error = "Server not found";
        } catch (Exception e) {
            m_error = e.toString();
        } finally {
            urlConnection.disconnect();
        }

        if( m_error != "" )
        {
            return false;
        }

        return true;


    }

}

import java.util.*;

import javax.microedition.io.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.location.*;

import com.sun.midp.lcdui.Text;

public class Tracso01 extends MIDlet implements CommandListener, LocationListener {

  private String serverURL = "http://localhost:8080/tracso/tracso.jsp";
  private Display display;
  private Form form;
  private Command exit = new Command("Exit", Command.EXIT, 1);
  private Command start = new Command("Start", Command.SCREEN, 1);
  private Command stop = new Command("Stop", Command.SCREEN, 1);
  
  private ImageItem logo;
  private TextField uid = new TextField("UID","guest", 50, TextField.ANY);
  private String uidstr = "1945";
  private TextField interval = new TextField("Update Interval(sec)","60", 5, TextField.NUMERIC);
  private int sec = 1;
  private StringItem info = new StringItem("Location:","unknown");
  private LocationProvider locationProvider = null;


  public Tracso01(){
    display = Display.getDisplay (this);
    form = new Form("Tracso01");
    form.addCommand(exit);
    form.addCommand(start);
    form.setCommandListener(this);
    try{
    logo = new ImageItem(null, Image.createImage("/tracso-logo-mini.jpg"), Graphics.VCENTER | Graphics.HCENTER, "Tracso");
    form.append(logo);
    }catch (Exception e) {
		// TODO: handle exception
    	form.append("T R A C S O");
	}
    form.append(uid);
    form.append(interval);
    form.append(info);
    
    try {
	  locationProvider = LocationProvider.getInstance(null);
	} catch (Exception e) {
	  exit();
    }
  }

  public void commandAction(Command c, Displayable s) {
    if (c == exit) {
  	  exit();
	}
	if(c == start){
	  form.removeCommand(start);
      uidstr = (uid.getString() != null)?
        uid.getString() : "1984";
	  sec = (interval.getString() != null)?
	    Integer.parseInt(interval.getString()) : 60;

	  // Start querying GPS data :
	  new Thread(){
        public void run(){
          locationProvider.setLocationListener(Tracso01.this, sec, -1, -1);
	    }
	  }.start();


	  form.addCommand(stop);
	}
	if(c == stop){
	  form.removeCommand(stop);

	  // Stop querying GPS data :
	  new Thread(){
        public void run(){
          locationProvider.setLocationListener(null, -1, -1, -1);
	    }
	  }.start();

	  form.addCommand(start);
	}
  }

  public void startApp () {
    display.setCurrent(form);
  }

  public void pauseApp () {}

  public void destroyApp (boolean forced) {}

  public void exit(){
    destroyApp(false);
    notifyDestroyed();
  }

  public void locationUpdated(LocationProvider provider, Location location){
    if (location != null && location.isValid()) {
      QualifiedCoordinates qc = location.getQualifiedCoordinates();
      info.setText(
        "Lat: " + qc.getLatitude() + "\n" +
        "Lon: " + qc.getLongitude() + "\n" +
        "Alt: " + qc.getAltitude() + "\n"
      );
      HttpConnection connection = null;
      try {
		String url = serverURL+"?reqType=5&uid=" + uidstr +
		  "&lat=" + qc.getLatitude() +
		  "&lon=" + qc.getLongitude();
        connection = (HttpConnection) Connector.open(url);
        int rc = connection.getResponseCode();
        connection.close();
      }
      catch(Exception e){
        e.printStackTrace();
	  }
      finally {
		try {
          connection.close();
        }
        catch(Exception io){
	      io.printStackTrace();
	    }
      }
    }
  }

  public void providerStateChanged(LocationProvider provider, int newState){
  }

}
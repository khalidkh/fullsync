
package net.sourceforge.fullsync.impl;

import java.io.File;

import javax.xml.parsers.FactoryConfigurationError;

import net.sourceforge.fullsync.Preferences;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Michele Aiello
 *
 */
public class ConfigurationPreferences implements Preferences 
{
    private PropertiesConfiguration config;
    private String configFile;
    
    public ConfigurationPreferences( String configFile ) 
    {
        this.configFile = configFile;        
        this.config = new PropertiesConfiguration();
        
        try {
            File file = new File( configFile );
            config.setFile( file );
            if( file.exists() )
            {
                config.load();
            } 
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
        
    }
    
    public void save()
    {
        try {
            config.save();
        } catch( ConfigurationException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public boolean confirmExit() 
	{
	    return config.getBoolean("Interface.ConfirmExit", true);
	}
	public void setConfirmExit( boolean bool )
	{
	    config.setProperty("Interface.ConfirmExit", new Boolean( bool ));
	}
	
	public boolean closeMinimizesToSystemTray() 
	{
		return config.getBoolean("Interface.CloseMinimizesToSystemTray", true);
	}
	public void setCloseMinimizesToSystemTray( boolean bool )
	{
	    config.setProperty("Interface.CloseMinimizesToSystemTray", new Boolean( bool ));
	}
	
	public boolean minimizeMinimizesToSystemTray()
	{
	    return config.getBoolean("Interface.MinimizeMinimizesToSystemTray", false);
	}
	public void setMinimizeMinimizesToSystemTray( boolean bool )
	{
	    config.setProperty("Interface.MinimizeMinimizesToSystemTray", new Boolean( bool ));
	}
	
	public boolean systemTrayEnabled()
	{
	    return config.getBoolean("Interface.SystemTray.Enabled", true);
	}
	public void setSystemTrayEnabled( boolean bool )
	{
	    config.setProperty("Interface.SystemTray.Enabled", new Boolean( bool ));
	}
}
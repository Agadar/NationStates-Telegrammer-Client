package com.github.agadar.nstelegram;

import com.github.agadar.nsapi.NSAPI;
import java.util.List;

/**
 * The controller behind the GUI.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public final class NSTelegramControl 
{
    /** 
     * Initializes the NSTelegramControl, setting the User Agent and telling
     * the NationStates Java Wrapper to do the version check.
     */
    public static void init()
    {
        NSAPI.setUserAgent("Agadar's Telegrammer (https://github.com/Agadar/NationStates-Telegrammer)");
    }

    /**
     * Returns a list of nations that live in the given region.
     * 
     * @param region the region of which the nations to return
     * @return a list of nations that live in the given region
     */
    private static List<String> nationsInRegion(String region)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of up to the specified number of new nations.
     * 
     * @param max the maximum number of new nations to return
     * @return a list of new nations
     */
    private static List<String> newNations(int max)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of up to the specified number of refounded nations.
     * 
     * @param max the maximum number of refounded nations to return
     * @return a list of refounded nations
     */
    private static List<String> refoundedNations(int max)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of up to the specified number of nations that are new delegates.
     * 
     * @param max the maximum number of nations that are new delegates to return
     * @return a list of nations that are new delegates
     */
    private static List<String> newDelegates(int max)
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of all delegate nations.
     * 
     * @return a list of all delegate nations
     */
    private static List<String> delegates()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of all nations that are World Assembly members.
     * 
     * @return a list of all nations that are World Assembly members.
     */
    private static List<String> worldAssemblyMembers()
    {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Returns a list of all nations in the world.
     * 
     * @return a list of all nations in the world.
     */
    private static List<String> allNations()
    {
        throw new UnsupportedOperationException();
    }
}

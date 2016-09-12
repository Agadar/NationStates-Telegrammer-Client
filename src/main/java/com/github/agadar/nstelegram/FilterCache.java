package com.github.agadar.nstelegram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Agadar <https://github.com/Agadar/>
 */
public class FilterCache 
{
    private final Map<String, List<String>> NationsInRegion;
    private final Map<String, List<String>> RegionsWithTag;
    private final Map<String, List<String>> RegionsWithoutTag;
    private final List<String> Delegates;
    private final List<String> WaMembers;
    private final List<String> All;
    
    public FilterCache()
    {
        NationsInRegion = new HashMap<>();
        RegionsWithTag = new HashMap<>();
        RegionsWithoutTag = new HashMap<>();
        Delegates = new ArrayList<>();
        WaMembers = new ArrayList<>();
        All = new ArrayList<>();
    }
}

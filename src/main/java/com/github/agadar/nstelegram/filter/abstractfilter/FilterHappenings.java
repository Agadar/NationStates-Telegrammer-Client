package com.github.agadar.nstelegram.filter.abstractfilter;

import com.github.agadar.nsapi.domain.shared.Happening;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract parent for filters that add nations derived from Happenings.
 * 
 * @author Agadar <https://github.com/Agadar/>
 */
public abstract class FilterHappenings extends FilterAdd
{
    /** Pattern used for extracting nation names from happenings descriptions. */
    private final static Pattern PATTERN = Pattern.compile("\\@\\@(.*?)\\@\\@");
    
    /** 
     * The keyword used for finding the correct happenings in a list of happenings.
     */
    public enum KeyWord
    {
        became,     // new delegates
        refounded,  // refounded nations
        ejected     // ejected nations
    }
    
    /** This instance's keyword. */
    private final KeyWord MyKeyWord;
    
    public FilterHappenings(KeyWord keyWord)
    {
        this.MyKeyWord = keyWord;     
    }
    
    /**
     * From a list of happenings, derives nation names from each happening that
     * contains in its description the supplied string.
     * 
     * @param happenings
     * @return 
     */
    protected Set<String> filterHappenings(Set<Happening> happenings)
    {       
        final Set<String> nations = new HashSet<>();
        
        happenings.forEach(h -> 
        {
            if (h.Description.contains(MyKeyWord.toString()))
            {
                final Matcher matcher = PATTERN.matcher(h.Description);
                
                if (matcher.find())
                {
                    nations.add(matcher.group(1));
                }
            }
        });    
        
        return nations;
    }
}

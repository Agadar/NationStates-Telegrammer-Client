package com.github.agadar.nstelegram.filter;

import com.github.agadar.nsapi.NSAPI;
import com.github.agadar.nsapi.domain.world.World;
import com.github.agadar.nsapi.enums.shard.WorldShard;
import com.github.agadar.nstelegram.filter.abstractfilter.FilterAddOrRemove;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter for adding/removing nations that are in regions without the supplied tags.
 *
 * @author Agadar
 */
public class FilterRegionsWithoutTags extends FilterAddOrRemove
{
    /** This instance's tags. */
    private final Set<String> Tags;
    
    public FilterRegionsWithoutTags(Set<String> tags, boolean add)
    {
        super(add);
        this.Tags = tags;
    }

    @Override
    protected Set<String> retrieveNations()
    {
        // Query local cache.
        if (LocalCache != null)
            return LocalCache;
        
        // Query global cache for regions mapped to tags.
        Set<String> regions = GlobalCache.getRegionsToTagsWithout(Tags);
        
        // If they aren't in the global cache, retrieve from server and cache them.
        if (regions == null)
        {
            final World w = NSAPI.world(WorldShard.RegionsByTag).regionsWithoutTags(
                Tags.toArray(new String[Tags.size()])).execute();

            // This check need be done because when no regions are found, the API
            // nevertheless sends back one empty string.
            if (w != null && (w.RegionsByTag().size() != 1 || 
                    !w.RegionsByTag().get(0).isEmpty()))
            {
                regions = new HashSet<>(w.RegionsByTag());
            }
            else
            {
                regions = new HashSet<>();
            }
            
            GlobalCache.mapRegionsToTagsWithout(Tags, regions);
        }
        
        // Using the regions, now retrieve the nations.
        LocalCache = new FilterRegions(regions, Add).retrieveNations();
        return LocalCache;
    }
}

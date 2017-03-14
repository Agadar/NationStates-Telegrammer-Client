package com.github.agadar.telegrammer.cli;

import com.github.agadar.telegrammer.core.enums.SkippedRecipientReason;
import com.github.agadar.telegrammer.core.manager.HistoryManager;
import com.github.agadar.telegrammer.core.util.Tuple;

/**
 *
 * @author Agadar (https://github.com/Agadar/)
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        HistoryManager hm = HistoryManager.get();
        
        hm.loadHistory();
        
        
        
        hm.history.entrySet().forEach(entry -> {
           System.out.println(entry.getKey().X + " " + entry.getKey().Y + " " + entry.getValue().name()); 
        });
        
        hm.history.put(new Tuple("72383", "aggy1"), SkippedRecipientReason.NOT_FOUND);
        
        hm.saveHistory();
    }
    
}

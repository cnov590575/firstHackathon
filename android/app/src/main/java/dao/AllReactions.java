package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AllReactions {
    //Array : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
    protected static HashMap<UUID, int[]> reactions;

    /**
     * Returns the list of reactions corresponding to the given UUID : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
     * @param postOrMessage The uuid of the post/message you are reacting to
     */
    public static int[] reactions(UUID postOrMessage) {
        if (!reactions.containsKey(postOrMessage)) {
            reactions.put(postOrMessage, new int[]{0, 0, 0, 0, 0});
        }
        return reactions.get(postOrMessage);
    }

    /**
     * Increments the  corresponding reaction number given of the given UUID : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
     * @param uuid The uuid of the message you are reacting to
     * @param number Which reaction is being incremented
     */
    public void incrementReaction(UUID uuid, int number){
        if (!reactions.containsKey(uuid)) {
            reactions.put(uuid, new int[]{0, 0, 0, 0, 0});
        }
        reactions.get(uuid)[number]+=1;
    }

    /**
     * Decrements the  corresponding reaction number given of the given UUID : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
     * @param uuid The uuid of the message you are reacting to
     * @param number Which reaction is being decremented
     */
    public void decrementReaction(UUID uuid, int number){
        Objects.requireNonNull(reactions.get(uuid))[number]-=1;
    }
}

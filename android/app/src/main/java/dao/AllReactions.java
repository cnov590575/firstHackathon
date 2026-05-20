package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class AllReactions {
    //Array : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
    protected static HashMap<UUID, int[]> reactions;
    // Hashmap: Links a user to a hashmap of all the message UUIDs and whether they have
    // already reacted that specific reaction to that specific message
    protected static HashMap<UUID, HashMap<UUID, Boolean[]>> userReactions;


    public static HashMap<UUID, int[]> getAllReactions(){
        if (reactions==null) {
            reactions = new HashMap<>();
        }
        return reactions;
    }

    public static HashMap<UUID, HashMap<UUID, Boolean[]>> getAllUserReactions(){
        if (userReactions==null) {
            userReactions = new HashMap<>();
        }
        return userReactions;
    }


    //Returns the previous reaction state and switches it
    public static boolean react(UUID user, UUID postOrMessage, int reaction) {
        System.out.println(user);
        System.out.println(postOrMessage);
        if (!userReactions.containsKey(user)) {
            userReactions.put(user, new HashMap<>());
        }
        if (!(userReactions.get(user).containsKey(postOrMessage))){
            userReactions.get(user).put(postOrMessage, new Boolean[]{false, false, false, false, false});
            System.out.println("You fucked up");

        }
        Boolean ret = userReactions.get(user).get(postOrMessage)[reaction];
        userReactions.get(user).get(postOrMessage)[reaction]=!userReactions.get(user).get(postOrMessage)[reaction];
        return ret;
    }

    /**
     * Returns the list of reactions corresponding to the given UUID : 1 - Thumbs up, 2 - Heart, 3 - Smile/Laugh, 4 - Crying Face, 5 - Anger
     * @param postOrMessage The uuid of the post/message you are reacting to
     */
    public static int[] postMsgReactions(UUID postOrMessage) {
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
    public static void incrementReaction(UUID uuid, int number){
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
    public static void decrementReaction(UUID uuid, int number){
        Objects.requireNonNull(reactions.get(uuid))[number]-=1;
    }
}

package OwnServer;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {

    private List<ChatGroup> groups;

    /**
     * the model class for a group manager
     * this class holds the list of groups present in the server.
     */
    public GroupManager() {
        this.groups = new ArrayList<>();
    }

    /**
     * checks if a specific group exists by a given name.
     * @param groupName the group name this method should search for.
     * @return if the group exists
     */
    public boolean doesGroupExist(String groupName){
        for (ChatGroup cg : groups) {
            if (cg.getName().equals(groupName)){
                return true;
            }
        }
        return false;
    }

    /**
     * this method returns a ChatGroup object by a given name if the chat-group exists.
     * @param groupName the chat-group this method should search for.
     * @return ChatGroup object or null, if no group is found.
     */
    public ChatGroup getGroupByName(String groupName){

        if (doesGroupExist(groupName)){
            for (ChatGroup cg : groups) {
                if (cg.getName().equals(groupName)){
                    return cg;
                }
            }
        }

        return null;
    }

    /**
     * this method ads a new group the list of existing groups.
     * @param name the name of the new group
     * @param owner the owner of the new group
     */
    public void addNewGroup(String name, String owner){
        groups.add(new ChatGroup(name, owner));
    }

    /**
     * this method ads a new group the list of existing groups.
     * @param chatGroup the new chat-group object.
     */
    public void addNewGroup(ChatGroup chatGroup){
        groups.add(chatGroup);
    }

    public List<ChatGroup> getGroupList() {
       return groups;
    }

    /**
     * tests if ANY group exists.
     * @return if at least one exists return true. else false.
     */
    public boolean doGroupsExist(){
        return !groups.isEmpty();
    }

    /**
     * this method tests if a specific user is part of a specific group.
     * @param username the username searching for.
     * @param groupname the group that needs to be searched for a user.
     * @return boolean of the given user being member of the given group.
     */
    public boolean isUserMemberOfGroup(String username, String groupname) {
        for (String member : getGroupByName(groupname).getMembers()) {
            if (member.equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * tests if a given string is valid. (only containing letters, numbers and underscores.
     * @param messagePayload given string
     * @return boolean of valid-ness
     */
    public static boolean isValidGroupName(String messagePayload){
        return messagePayload.matches("[a-zA-Z0-9_]{5,15}");
    }


}

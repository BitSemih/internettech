package OwnServer;

import java.util.ArrayList;
import java.util.List;

public class ChatGroup {

    private String name;
    private List<String> usernames;
    private String owner;

    /**
     * model class for a chat-group.
     * @param name the name of the chat-group. contains only letters, numbers and underscores. must be between 5 and 15 characters long.
     * @param owner the owner of a the chat-group. the user that made this chat-group.
     */
    public ChatGroup(String name, String owner) {
        this.name = name;

        this.owner = owner;

        this.usernames = new ArrayList<>();
        usernames.add(owner);

    }

    public String getName() {
        return name;
    }

    public List<String> getMembers() {
        return usernames;
    }

    public void addClient(String username) {
        this.usernames.add(username);
    }

    /**
     * checks if a given user is a member of this chat-group
     * @param username the username the method should look for
     * @return
     */
    public boolean isMember(String username){
        for (String u : usernames) {
            if (u.equals(username)){
                return true;
            }
        }
        return false;
    }

    /**
     * this method removes a client from this group.
     * @param username the username that should be removed from this group.
     * @return
     */
    public boolean removeClient(String username){
        for (int i = 0; i < usernames.size(); i++) {
            if (usernames.get(i).equals(username)) {
                usernames.remove(i);
                return true;
            }
        }
        return false;
    }

    public String getOwner() {
        return owner;
    }
}

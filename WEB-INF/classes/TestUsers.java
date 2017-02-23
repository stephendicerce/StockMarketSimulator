public class TestUsers {
    private static User[] users = new User[100];
    private static int count = 0;
    
    private static User addUser(String name) {
	if(count == users.length) {
	    growUserSpace();
	}
	users[count++] = new User(name, 500);
	return users[count - 1];
    }

    public static User getUser(String name) {
	for(int i=0; i<count; ++i) {
	    User u = users[i];
	    if(u.getName().equals(name)) {
		return u;
	    }
	}
	return addUser(name);
    }

    private static void growUserSpace() {
	User[] tmpUsers = users;
	users = new User[users.length * 2];
	for(int i=0; i<tmpUsers.length; ++i) {
	    users[i] = tmpUsers[i];
	}
    }
}

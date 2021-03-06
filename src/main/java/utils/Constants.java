package utils;

public class Constants {

    public static final int RMI_PORT = 8179;

    public static final String SERVICE = "guardian";

    public static final String SERVER_URL = "rmi://10.114.178.246:" + RMI_PORT + "/" + SERVICE;

    public static final String BASE_SALT = "Roger1Ding2Zristiano3";

    public static final int SALT_INTERVAL = 1;

    public static final String CLIENT_0 = "client_0";

    public static final String CLIENT_1 = "client_1";

    public static final int HASH_CACHE_SIZE = 100;

    public static final String REQUEST_LOG_PATH = "src/main/resources/request.log";

    public static final String USERS_INFO_PATH = "src/main/resources/users.info";

}

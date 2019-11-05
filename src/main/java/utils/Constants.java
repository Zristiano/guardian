package utils;

import java.io.File;

public class Constants {

    public static final int RMI_PORT = 8179;

    public static final String SERVICE = "guardian";

    public static final String SERVER_URL = "rmi://localhost:" + RMI_PORT + File.separator + SERVICE;

}

package client;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;
import java.util.UUID;

public class Client {
    public static void main(String[] args) {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        System.out.println(Hashing.sha256().hashString(uuid1, Charset.forName("UTF-8")).hashCode());
        System.out.println(Hashing.sha256().hashString(uuid2, Charset.forName("UTF-8")).hashCode());
        System.out.println(Hashing.sha256().hashString(uuid1, Charset.forName("UTF-8")).hashCode());
        System.out.println(Hashing.sha256().hashString(uuid2, Charset.forName("UTF-8")).hashCode());

    }

}

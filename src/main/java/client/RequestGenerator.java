package client;

import model.User;
import utils.Constants;
import utils.GdLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class RequestGenerator {

    private User[] users;

    private int[] freq;

    private Random random;

    private int userCount;

    public RequestGenerator(int count){
        users = new User[count];
        freq = new int[count];
        random = new Random();
        userCount = count;
        initUsers();
    }

    private void initUsers(){
        try {
            FileReader fileReader = new FileReader(Constants.USERS_INFO_PATH);
            BufferedReader reader = new BufferedReader(fileReader);
            String s ;
            int i = 0 ;
            int prevFreq = 0;
            while ((s = reader.readLine()) !=null && i<userCount){
                String ss[] = s.split(", ");
                String id = ss[0].substring(4);
                String userName = ss[1].substring(9,ss[1].length()-1);
                users[i] = new User(userName, id);
                freq[i] = prevFreq + 1;
                prevFreq = freq[i];
                i++;
            }
        } catch (IOException e) {
            GdLog.e(""+e);
        }
    }

    public User getRandomUser(int userLimit){
        userLimit = userLimit>users.length? users.length : userLimit;
        int freqLimit = freq[userLimit-1];
        int target = random.nextInt(freqLimit)+1;
        int idx = Arrays.binarySearch(freq, target);
        if (idx<0){
            idx = -(idx+1);
        }
        return users[idx];
    }

    public User getUser(int index){
        return users[index];
    }

    /**
     * set the times of frequencies of the user specified by userNum
     * Ex.<p>
     *     there are 50 users having been initiated. Normally, each user has the same chance of sending request,
     *     that is, within 50 random user request, ideally every user fire 1 request. If calling this function as
     *     <I>setUserFrequency(30, 40)</I>, then user30 has 40 times the possibility of normal user to send request.
     * </p>
     * @param userNum user number in the userName
     * @param frequency frequency
     */
    public void setUserFrequency(int userNum, int frequency){
        int freqDiff ;
        if (userNum==0){
            freqDiff = frequency - freq[0];
        }else {
            freqDiff = frequency - (freq[userNum]-freq[userNum-1]);
        }
        for (int i=userNum; i<freq.length; i++){
            freq[i] += freqDiff;
        }
    }
}

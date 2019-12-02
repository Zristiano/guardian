package client;

public class Request {

    private String userId;

    private long requestTime;

    private boolean success;

    private long dropTableUpdateTs;

    public Request(String userId){
        this.userId = userId;
    }

    public void setTime(){
        requestTime = System.currentTimeMillis();
    }

    public void setResult(boolean isSuc){
        success = isSuc;
    }

    public void setDropTableUpdateTs(long dropTableUpdateTs) {
        this.dropTableUpdateTs = dropTableUpdateTs;
    }

    public String getUserId() {
        return userId;
    }

    public long getRequestTime() {
        return requestTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public long getDropTableUpdateTs() {
        return dropTableUpdateTs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(userId).append(' ').append(requestTime).append(' ').append(success).append(' ').append(dropTableUpdateTs);
        return sb.toString();
    }
}

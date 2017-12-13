package max_clique;

import java.util.concurrent.TimeUnit;

public class Time implements Runnable {
    static int timelimit = 0;
    public void run()		
    {
        try{
            TimeUnit.SECONDS.sleep(timelimit);
        }
        catch(Exception e)
        {}
        System.out.println(0);
        System.exit(1);
    }
    public static void setTimeLimit(int limit)	{
        timelimit = limit;
    }
}

package app.zingo.employeemanagements.Utils;

import java.util.concurrent.Executor;

/**
 * Created by ZingoHotels Tech on 27-09-2018.
 */

public class ThreadExecuter implements Executor {
    @Override
    public void execute(Runnable command) {
        new Thread(command).start();
    }
}

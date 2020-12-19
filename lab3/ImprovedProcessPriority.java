import java.util.Comparator;

public class ImprovedProcessPriority implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        sProcess pr1 = (sProcess) o1;
        sProcess pr2 = (sProcess) o2;

        if (pr1.precendence == pr2.precendence && pr1.ioblocking == pr2.ioblocking && pr1.cputime == pr2.cputime)
            return 0;
        else if (pr1.precendence == pr2.precendence && pr1.ioblocking == pr2.ioblocking){
            int priority1 = pr1.cputime;
            int priority2 = pr2.cputime;
            if (priority1- priority2 > 0)
                return 1;
            else return -1;
        }
        else if (pr1.precendence == pr2.precendence){
            int priority1 = pr1.ioblocking;
            int priority2 = pr2.ioblocking;
            if (priority1- priority2 > 0)
                return 1;
            else return -1;
        }
        else if (pr1.precendence > pr2.precendence){
            return 1;
        }
        else return -1;
    }
}

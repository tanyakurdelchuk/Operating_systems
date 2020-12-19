// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

    public static Results Run(int runtime, Vector<sProcess> processVector, Results result) {
        int i = 0;
        int comptime = 0;
        int currentProcess = 0;
        int previousProcess = 0;
        int size = processVector.size();
        int completed = 0;
        String resultsFile = "Summary-Processes";
        Comparator comparator = new ImprovedProcessPriority();

        result.schedulingType = "Batch (Nonpreemptive)";
        result.schedulingName = "Shorted job first";
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            //OutputStream out = new FileOutputStream(resultsFile);
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

//      sort(processVector);
            createPriority(processVector);
            processVector.sort(comparator);
            sProcess process = processVector.elementAt(currentProcess);
            out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
            while (comptime < runtime) {
                if (process.cpudone == process.cputime) {
                    completed++;
                    out.println("Process: " + process.id + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
                    if (completed == size) {
                        result.compuTime = comptime;
                        out.close();
                        return result;
                    }
                    updateVector(processVector);
                    if (processVector.isEmpty())
                        break;
                    currentProcess++;
                    if (currentProcess >= processVector.size())
                        currentProcess = 0;
                    process = processVector.elementAt(currentProcess);

                    if (currentProcess == previousProcess && process.cpudone >= process.cputime) {
                        out.println("Process:" + process.id + "completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
                        break;
                    }
                    out.println("Process: " + process.id + "registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + process.precendence + " " + ")");
                }
                if (process.ioblocking == process.ionext) {
                    out.println("Process: " + process.id + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
                    process.numblocked++;
                    process.ionext = 0;
                    previousProcess = currentProcess;

                    processVector = updateVector(processVector);
                    if (processVector.isEmpty())
                        break;
                    currentProcess++;
                    if (currentProcess >= processVector.size())
                        currentProcess = 0;
                    process = processVector.elementAt(currentProcess);

                    if (currentProcess == previousProcess && process.cpudone >= process.cputime) {
                        out.println("Process: " + process.id + " completed... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
                        break;
                    }

                    out.println("Process: " + process.id + " registered... (" + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.cpudone + " " + process.precendence + " " + ")");
                }
                process.cpudone++;
                if (process.ioblocking > 0) {
                    process.ionext++;
                }
                comptime++;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        return result;
    }

    private static void sort(Vector<sProcess> processes) {
        Collections.sort(processes, new Comparator<sProcess>() {

            @Override
            public int compare(sProcess o1, sProcess o2) {
                sProcess pr1 = (sProcess) o1;
                sProcess pr2 = (sProcess) o2;
                return pr1.cputime - pr2.cputime;
            }
        });
    }

    private static Vector<sProcess> updateVector(Vector<sProcess> processes) {
        Vector<sProcess> vector = new Vector<>();
        for (sProcess process : processes) {
            if (process.cputime > process.cpudone)
                vector.add(process);
        }
        return vector;
    }

    private static void createPriority(Vector<sProcess> processes) {
        int i = 0;
        Collections.sort(processes, new Comparator<sProcess>() {

            @Override
            public int compare(sProcess o1, sProcess o2) {
                sProcess pr1 = (sProcess) o1;
                sProcess pr2 = (sProcess) o2;
                return  pr2.ioblocking - pr1.ioblocking ;
            }
        });
        for (sProcess process : processes) {
            process.precendence = ++i;
        }


    }
}



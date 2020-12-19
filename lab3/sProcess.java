public class sProcess {
  public int id;
  public int cputime;
  public int ioblocking;
  public int cpudone;
  public int ionext;
  public int numblocked;
  public int precendence;

  public sProcess (int id , int cputime, int ioblocking, int cpudone, int ionext, int numblocked , int precendence) {
    this.id = id;
    this.cputime = cputime;
    this.ioblocking = ioblocking;
    this.cpudone = cpudone;
    this.ionext = ionext;
    this.numblocked = numblocked;
    this.precendence = precendence;
  } 	
}

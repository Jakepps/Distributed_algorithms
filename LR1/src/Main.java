import mpi.MPI;
import mpi.MPIException;

public class Main {
    public static void main(String[] args) throws MPIException {
        MPI.Init(args);
        int myrank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        int TAG = 0;
        int message = myrank;

        if (myrank % 2 == 0) {
            if (myrank + 1 != size) {
                MPI.COMM_WORLD.Send(new int[]{message}, 0, 1, MPI.INT,myrank + 1, TAG);
            }
        } else {
            if (myrank != 0) {
                MPI.COMM_WORLD.Recv(new int[]{message}, 0, 1, MPI.INT,myrank - 1, TAG);
                System.out.println("Process " + myrank + " received: " + message);
            }
        }

        MPI.Finalize();
    }
}
